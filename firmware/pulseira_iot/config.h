#pragma once

// ============================================================
// CONFIGURAÇÃO DA PULSEIRA - preencha ANTES de gravar na placa.
// Não envie este arquivo para o GitHub com dados reais.
// Passo a passo completo no README.md desta pasta.
// ============================================================

// Nome e senha do Wi-Fi. O ESP8266 só conecta em redes de 2,4 GHz.
constexpr char WIFI_SSID[] = "COLOQUE_AQUI_O_NOME_DA_REDE_WIFI_2_4GHZ";
constexpr char WIFI_PASSWORD[] = "COLOQUE_AQUI_A_SENHA_DO_WIFI";

// Endereço da API: troque IP_DO_SERVIDOR pelo IP do computador/servidor onde o
// back-end está rodando (ex.: 192.168.0.10). Mantenha a porta e o caminho.
// Exemplo completo: "http://192.168.0.10:8080/api/telemetrias"
constexpr char TELEMETRY_URL[] = "http://IP_DO_SERVIDOR:8080/api/telemetrias";

// Identificador desta pulseira. Precisa estar cadastrado no back-end (campo
// dispositivoId da pulseira), ativa e vinculada a uma pessoa monitorada.
constexpr char DEVICE_ID[] = "ESP12E-PULSEIRA-001";
