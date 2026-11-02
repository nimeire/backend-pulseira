/*
 * Firmware - Pulseira IoT Zelo
 *
 * Hardware (NodeMCU ESP-12E):
 *   MPU6500: VCC->3V, GND->G, SCL->D1, SDA->D2
 *   Buzina:  (+)->D7, (-)->G
 *   Botao:   um canto->D3, canto oposto na diagonal->G (D3 = botao FLASH da placa)
 *   LED:     LED azul da propria placa (GPIO 2, acende em LOW)
 *
 * Abra config.h e preencha Wi-Fi, URL da API e DEVICE_ID antes de enviar.
 */

#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <Wire.h>
#include "config.h"

#ifndef D1
#define D1 5
#endif
#ifndef D2
#define D2 4
#endif
#ifndef D3
#define D3 0
#endif
#ifndef D7
#define D7 13
#endif

constexpr uint8_t BUZZER_PIN = D7;
constexpr uint8_t BUTTON_PIN = D3;
constexpr uint8_t LED_PIN = LED_BUILTIN;  // GPIO 2, ativo em LOW.

constexpr uint8_t MPU_ADDRESS = 0x68;
constexpr uint8_t MPU_WHO_AM_I = 0x75;
constexpr uint8_t MPU_PWR_MGMT_1 = 0x6B;
constexpr uint8_t MPU_ACCEL_XOUT_H = 0x3B;

constexpr unsigned long TELEMETRY_INTERVAL_MS = 5000;
constexpr unsigned long WIFI_RETRY_INTERVAL_MS = 10000;
constexpr unsigned long ALERT_RETRY_INTERVAL_MS = 3000;
constexpr unsigned long ALARM_POLL_INTERVAL_MS = 2000;
constexpr uint16_t HTTP_TIMEOUT_MS = 1500;

constexpr unsigned long DEBOUNCE_MS = 30;
constexpr unsigned long LONG_PRESS_MS = 2000;
constexpr unsigned long FALL_CONFIRM_MS = 15000;
// Limite de segurança: o alarme (queda ou pânico) toca até o botão cancelar ou até este tempo.
constexpr unsigned long ALARM_MAX_MS = 300000;

constexpr float FREE_FALL_THRESHOLD_G = 0.45f;
constexpr float IMPACT_THRESHOLD_G = 2.5f;
constexpr unsigned long FALL_WINDOW_MS = 1200;
constexpr unsigned long FALL_COOLDOWN_MS = 15000;

enum class State : uint8_t { Idle, FallCountdown, FallAlarm, Panic };

struct MotionReading {
  float ax = 0, ay = 0, az = 0;
  float gx = 0, gy = 0, gz = 0;
  float magnitudeG = 0;
};

State state = State::Idle;
unsigned long stateSince = 0;

bool mpuReady = false;
float gyroOffsetX = 0, gyroOffsetY = 0, gyroOffsetZ = 0;
MotionReading lastMotion;

bool waitingForImpact = false;
unsigned long freeFallAt = 0;
unsigned long lastFallAt = 0;

bool buttonStable = false;  // true = pressionado
bool buttonRaw = false;
unsigned long buttonRawAt = 0;
unsigned long pressedAt = 0;
bool longFired = false;

// Alertas aguardando envio ao back-end; so saem quando a API responde 2xx.
bool pendingFallAlert = false;
bool pendingPanicAlert = false;
bool fallCancelledAfterAlert = false;
bool lastSendFailed = false;
unsigned long lastAlertTryAt = 0;
unsigned long lastAlarmPollAt = 0;
unsigned long lastTelemetryAt = 0;
unsigned long lastWiFiAttemptAt = 0;

void buzzerOn() { digitalWrite(BUZZER_PIN, HIGH); }
void buzzerOff() { digitalWrite(BUZZER_PIN, LOW); }
void setBuzzer(bool on) { digitalWrite(BUZZER_PIN, on ? HIGH : LOW); }
void setLed(bool on) { digitalWrite(LED_PIN, on ? LOW : HIGH); }

// Bipe bloqueante curto, usado apenas para confirmacoes.
void beep(unsigned long ms) {
  buzzerOn();
  setLed(true);
  delay(ms);
  buzzerOff();
  setLed(false);
}

void confirmationBeeps(uint8_t count) {
  for (uint8_t i = 0; i < count; ++i) {
    beep(80);
    delay(80);
  }
}

bool mpuWrite(uint8_t reg, uint8_t value) {
  Wire.beginTransmission(MPU_ADDRESS);
  Wire.write(reg);
  Wire.write(value);
  return Wire.endTransmission() == 0;
}

bool mpuRead(uint8_t reg, uint8_t *data, uint8_t count) {
  Wire.beginTransmission(MPU_ADDRESS);
  Wire.write(reg);
  if (Wire.endTransmission(false) != 0) return false;
  if (Wire.requestFrom(static_cast<int>(MPU_ADDRESS), static_cast<int>(count)) != count) return false;
  for (uint8_t i = 0; i < count; ++i) data[i] = Wire.read();
  return true;
}

int16_t asInt16(const uint8_t *data, uint8_t index) {
  return static_cast<int16_t>((data[index] << 8) | data[index + 1]);
}

bool readMotion(MotionReading &motion) {
  uint8_t data[14];
  if (!mpuReady || !mpuRead(MPU_ACCEL_XOUT_H, data, sizeof(data))) return false;
  motion.ax = asInt16(data, 0) / 16384.0f;
  motion.ay = asInt16(data, 2) / 16384.0f;
  motion.az = asInt16(data, 4) / 16384.0f;
  motion.gx = asInt16(data, 8) / 131.0f - gyroOffsetX;
  motion.gy = asInt16(data, 10) / 131.0f - gyroOffsetY;
  motion.gz = asInt16(data, 12) / 131.0f - gyroOffsetZ;
  motion.magnitudeG = sqrtf(motion.ax * motion.ax + motion.ay * motion.ay + motion.az * motion.az);
  return true;
}

void initializeMpu() {
  uint8_t who = 0;
  if (!mpuRead(MPU_WHO_AM_I, &who, 1) || who != 0x70) {
    Serial.printf("MPU6500 indisponivel ou inesperado (WHO_AM_I=0x%02X).\n", who);
    return;
  }
  mpuReady = mpuWrite(MPU_PWR_MGMT_1, 0x00);
  delay(100);
  Serial.println(mpuReady ? "MPU6500 pronto." : "Falha ao iniciar MPU6500.");
}

void calibrateGyroscope() {
  if (!mpuReady) return;
  Serial.println("Calibrando giroscopio; mantenha a pulseira parada por 2 segundos.");
  float sumX = 0, sumY = 0, sumZ = 0;
  constexpr uint16_t samples = 200;
  for (uint16_t i = 0; i < samples; ++i) {
    uint8_t data[14];
    if (mpuRead(MPU_ACCEL_XOUT_H, data, sizeof(data))) {
      sumX += asInt16(data, 8) / 131.0f;
      sumY += asInt16(data, 10) / 131.0f;
      sumZ += asInt16(data, 12) / 131.0f;
    }
    delay(10);
    yield();
  }
  gyroOffsetX = sumX / samples;
  gyroOffsetY = sumY / samples;
  gyroOffsetZ = sumZ / samples;
  Serial.printf("Offsets gyro: %.2f, %.2f, %.2f graus/s\n", gyroOffsetX, gyroOffsetY, gyroOffsetZ);
}

void ensureWiFi() {
  if (WiFi.status() == WL_CONNECTED) return;
  const unsigned long now = millis();
  if (lastWiFiAttemptAt != 0 && now - lastWiFiAttemptAt < WIFI_RETRY_INTERVAL_MS) return;
  lastWiFiAttemptAt = now;
  Serial.printf("Conectando ao Wi-Fi %s...\n", WIFI_SSID);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
}

// Campos latitude/longitude/altitude/precisao sao omitidos: a pulseira nao usa mais GPS.
// "quedaDetectada" e "panicoAcionado" so vao true uma vez, no envio do alerta. "quedaCancelada" e ignorado pelo back-end.
String buildTelemetryJson(const MotionReading &motion, bool fallDetected, bool panic, bool fallCancelled) {
  String json;
  json.reserve(400);
  json += "{\"dispositivoId\":\"" + String(DEVICE_ID) + "\",";
  json += "\"aceleracaoX\":" + String(motion.ax * 9.80665f, 3) + ",";
  json += "\"aceleracaoY\":" + String(motion.ay * 9.80665f, 3) + ",";
  json += "\"aceleracaoZ\":" + String(motion.az * 9.80665f, 3) + ",";
  json += "\"rotacaoX\":" + String(motion.gx, 3) + ",";
  json += "\"rotacaoY\":" + String(motion.gy, 3) + ",";
  json += "\"rotacaoZ\":" + String(motion.gz, 3) + ",";
  json += "\"quedaDetectada\":" + String(fallDetected ? "true" : "false") + ",";
  json += "\"quedaCancelada\":" + String(fallCancelled ? "true" : "false") + ",";
  json += "\"panicoAcionado\":" + String(panic ? "true" : "false") + "}";
  return json;
}

bool sendTelemetry(const MotionReading &motion, bool fallDetected, bool panic, bool fallCancelled) {
  if (WiFi.status() != WL_CONNECTED) return false;
  WiFiClient client;
  HTTPClient http;
  http.setTimeout(HTTP_TIMEOUT_MS);
  if (!http.begin(client, TELEMETRY_URL)) return false;
  http.addHeader("Content-Type", "application/json");
  const int status = http.POST(buildTelemetryJson(motion, fallDetected, panic, fallCancelled));
  Serial.printf("Telemetria HTTP: %d (queda=%d panico=%d)\n", status, fallDetected, panic);
  http.end();
  return status >= 200 && status < 300;
}

void enterState(State next) {
  state = next;
  stateSince = millis();
  buzzerOff();
  setLed(false);
  switch (next) {
    case State::Idle:
      Serial.println("[estado] REPOUSO");
      break;
    case State::FallCountdown:
      Serial.println("[estado] QUEDA DETECTADA: aperte o botao em 15 s para cancelar.");
      break;
    case State::FallAlarm:
      Serial.println("[ALERTA] Queda confirmada (sem cancelamento).");
      pendingFallAlert = true;
      break;
    case State::Panic:
      Serial.println("[ALERTA] PANICO acionado.");
      pendingPanicAlert = true;
      break;
  }
}

void cancelCurrent(const char *reason) {
  if (state == State::Idle) return;
  Serial.printf("[cancelado] %s\n", reason);
  if (state == State::FallAlarm && !pendingFallAlert) fallCancelledAfterAlert = true;
  pendingFallAlert = false;
  pendingPanicAlert = false;
  enterState(State::Idle);
  confirmationBeeps(2);
}

void triggerFall(const char *origin) {
  if (state != State::Idle) return;
  lastFallAt = millis();
  waitingForImpact = false;
  Serial.printf("Queda (%s).\n", origin);
  enterState(State::FallCountdown);
}

void updateFallDetection(const MotionReading &motion) {
  const unsigned long now = millis();
  if (state != State::Idle || now - lastFallAt < FALL_COOLDOWN_MS) return;
  if (!waitingForImpact && motion.magnitudeG < FREE_FALL_THRESHOLD_G) {
    waitingForImpact = true;
    freeFallAt = now;
    return;
  }
  if (waitingForImpact && now - freeFallAt > FALL_WINDOW_MS) waitingForImpact = false;
  if (waitingForImpact && motion.magnitudeG > IMPACT_THRESHOLD_G) {
    triggerFall("queda livre + impacto");
  }
}

// Evento do botao: 1 = aperto curto (ao soltar), 2 = aperto longo (ao completar 2 s).
uint8_t pollButton() {
  const unsigned long now = millis();
  const bool raw = digitalRead(BUTTON_PIN) == LOW;
  if (raw != buttonRaw) {
    buttonRaw = raw;
    buttonRawAt = now;
  }
  if (now - buttonRawAt >= DEBOUNCE_MS && raw != buttonStable) {
    buttonStable = raw;
    if (buttonStable) {
      pressedAt = now;
      longFired = false;
    } else if (!longFired) {
      return 1;
    }
  }
  if (buttonStable && !longFired && now - pressedAt >= LONG_PRESS_MS) {
    longFired = true;
    return 2;
  }
  return 0;
}

void handleButton(uint8_t event) {
  if (event == 0) return;
  Serial.printf("Botao: %s\n", event == 1 ? "aperto curto" : "aperto longo (2 s)");
  switch (state) {
    case State::Idle:
      if (event == 2) {
        enterState(State::Panic);
        confirmationBeeps(1);
      }
      break;
    case State::FallCountdown:
    case State::FallAlarm:
      if (event == 1) cancelCurrent("queda finalizada pelo botao");
      break;
    case State::Panic:
      if (event == 2) cancelCurrent("panico encerrado pelo botao");
      break;
  }
}

// Comandos de teste pelo Monitor Serial: q = queda, p = panico, c = cancelar.
void handleSerial() {
  while (Serial.available()) {
    const char command = static_cast<char>(Serial.read());
    if (command == 'q') triggerFall("comando serial");
    else if (command == 'p' && state == State::Idle) enterState(State::Panic);
    else if (command == 'c') cancelCurrent("comando serial");
  }
}

// Atualiza buzina e LED conforme o estado, sem bloquear o loop.
void updateOutputs() {
  const unsigned long now = millis();
  const unsigned long elapsed = now - stateSince;
  bool buzzer = false;
  bool led = false;

  switch (state) {
    case State::Idle:
      if (WiFi.status() == WL_CONNECTED && !lastSendFailed) led = (now % 3000) < 80;  // batimento
      else led = (now % 1000) < 500;                                                   // sem rede/API
      break;
    case State::FallCountdown:
      if (elapsed >= FALL_CONFIRM_MS) {
        enterState(State::FallAlarm);
        return;
      }
      buzzer = led = (elapsed % 300) < 150;
      break;
    case State::FallAlarm:
      if (elapsed >= ALARM_MAX_MS) {
        Serial.println("[alarme] limite de tempo atingido; voltando ao repouso.");
        enterState(State::Idle);
        return;
      }
      buzzer = led = (elapsed % 1200) < 800;
      break;
    case State::Panic: {
      if (elapsed >= ALARM_MAX_MS) {
        Serial.println("[alarme] limite de tempo atingido; voltando ao repouso.");
        enterState(State::Idle);
        return;
      }
      // Tres bipes curtos e pausa, repetindo a cada 2 s.
      const unsigned long phase = elapsed % 2000;
      buzzer = led = phase < 750 && (phase % 300) < 150;
      break;
    }
  }
  setBuzzer(buzzer);
  setLed(led);
}

// Durante o alarme, pergunta ao back-end se o alerta ja foi resolvido pelo front.
// Se a consulta falhar, o alarme continua ate o botao ou o limite de tempo.
bool alarmResolvedByFront() {
  if (WiFi.status() != WL_CONNECTED) return false;
  String url = TELEMETRY_URL;
  url.replace("/api/telemetrias", String("/api/pulseiras/") + DEVICE_ID + "/alarme");
  WiFiClient client;
  HTTPClient http;
  http.setTimeout(HTTP_TIMEOUT_MS);
  if (!http.begin(client, url)) return false;
  const int status = http.GET();
  const String body = status == 200 ? http.getString() : String();
  http.end();
  return body.indexOf("\"alarmeAtivo\":false") >= 0;
}

// Envia alertas pendentes e a telemetria periodica. Sem envio durante a contagem da queda,
// para o botao de cancelar responder sem atraso.
void handleNetwork() {
  const unsigned long now = millis();

  if ((pendingFallAlert || pendingPanicAlert) && now - lastAlertTryAt >= ALERT_RETRY_INTERVAL_MS) {
    lastAlertTryAt = now;
    // Queda e pânico são sinais separados: quedaDetectada gera POSSIVEL_QUEDA, panicoAcionado gera PANICO.
    const bool sent = sendTelemetry(lastMotion, pendingFallAlert, pendingPanicAlert, false);
    lastSendFailed = !sent;
    if (sent) {
      pendingFallAlert = false;
      pendingPanicAlert = false;
      lastTelemetryAt = now;
    }
    return;
  }

  if (state == State::FallCountdown) return;

  const bool alarming = state == State::FallAlarm || state == State::Panic;
  if (alarming && !pendingFallAlert && !pendingPanicAlert && now - lastAlarmPollAt >= ALARM_POLL_INTERVAL_MS) {
    lastAlarmPollAt = now;
    if (alarmResolvedByFront()) {
      cancelCurrent("alerta resolvido pelo front");
      return;
    }
  }

  if (now - lastTelemetryAt >= TELEMETRY_INTERVAL_MS) {
    lastTelemetryAt = now;
    // panicoAcionado só vai true no alerta; nas telemetrias periódicas vai false para não repetir o alerta.
    const bool sent = sendTelemetry(lastMotion, false, false, fallCancelledAfterAlert);
    lastSendFailed = !sent && WiFi.status() == WL_CONNECTED;
    if (sent) fallCancelledAfterAlert = false;
    Serial.printf("acc total: %.2f g | Wi-Fi: %s\n", lastMotion.magnitudeG,
                  WiFi.status() == WL_CONNECTED ? "ok" : "sem conexao");
  }
}

void setup() {
  pinMode(BUZZER_PIN, OUTPUT);
  buzzerOff();
  pinMode(LED_PIN, OUTPUT);
  setLed(false);
  pinMode(BUTTON_PIN, INPUT_PULLUP);

  Serial.begin(115200);
  Wire.begin(D2, D1);
  Wire.setClock(100000);
  WiFi.mode(WIFI_STA);
  delay(300);
  Serial.println("\n=== PULSEIRA IOT ===");
  if (strncmp(WIFI_SSID, "COLOQUE_AQUI", 12) == 0 || strstr(TELEMETRY_URL, "IP_DO_SERVIDOR") != nullptr) {
    Serial.println("ATENCAO: config.h ainda tem os textos de exemplo. Preencha Wi-Fi e URL e grave de novo.");
  }
  initializeMpu();
  calibrateGyroscope();
  ensureWiFi();
  beep(150);
  enterState(State::Idle);
}

void loop() {
  handleSerial();
  ensureWiFi();

  MotionReading motion;
  if (readMotion(motion)) {
    lastMotion = motion;
    updateFallDetection(motion);
  }

  handleButton(pollButton());
  updateOutputs();
  handleNetwork();
  delay(5);
}
