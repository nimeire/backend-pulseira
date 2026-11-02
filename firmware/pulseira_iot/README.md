# Firmware da pulseira IoT

Firmware da NodeMCU ESP-12E com MPU6500, buzina e botão de pânico. A pulseira **não usa GPS**: ela detecta quedas pelo acelerômetro e envia os dados para o back-end por Wi-Fi.

Os arquivos desta pasta **não contêm dados reais**. Nome e senha do Wi-Fi e o endereço do servidor ficam em `config.h` como textos de exemplo, e cada pessoa preenche os seus **no próprio computador**, sem enviar de volta ao GitHub.

## Tutorial: configurar a pulseira em uma rede nova

Serve para qualquer lugar: basta um computador com a Arduino IDE, o cabo USB e o back-end rodando na mesma rede Wi-Fi.

### 1. Preparar o computador (só na primeira vez)
1. Instale a [Arduino IDE](https://www.arduino.cc/en/software).
2. Em **Arquivo > Preferências**, em "URLs adicionais para Gerenciadores de Placas", cole:
   `https://arduino.esp8266.com/stable/package_esp8266com_index.json`
3. Em **Ferramentas > Placa > Gerenciador de Placas**, procure `esp8266` e instale **esp8266 by ESP8266 Community**.
4. Se o Windows não reconhecer a placa ao conectar, instale o driver **CH340**. A porta COM aparece em **Ferramentas > Porta**.

### 2. Descobrir os dados da rede
1. **Wi-Fi:** anote o nome e a senha da rede de **2,4 GHz**. O ESP8266 não conecta em 5 GHz. Se o roteador tiver duas redes, use a de 2,4 GHz. Se for uma rede só com os dois nomes iguais, ela costuma funcionar, mas se não conectar, procure o nome da rede 2,4 GHz nas configurações do roteador.
2. **IP do servidor:** no computador que roda o back-end, abra o terminal e digite `ipconfig` (Windows) ou `ip a` (Linux). Anote o endereço IPv4 da placa de rede em uso, por exemplo `192.168.0.10`. Ele precisa ser da **mesma rede** da pulseira. Se o roteador trocar o IP do computador, repita este passo.
3. **Back-end rodando:** a API precisa estar ligada na porta `8080`. No Windows, permita o Java em redes privadas (e públicas, se for o caso) quando o firewall perguntar.

### HTTP ou HTTPS

O firmware escolhe pelo início da `TELEMETRY_URL`. Com `https://` ele usa conexão segura, **sem validar o certificado do servidor** (`setInsecure`). Isso criptografa o tráfego e serve para o protótipo e para túneis como o da Cloudflare, mas não protege contra alguém que se passe pelo servidor. Cada envio por HTTPS leva 1 a 3 s por causa do handshake. Se o servidor ou túnel mudar de endereço (os túneis gratuitos mudam a cada reinício), atualize o `config.h` e grave de novo.

### 3. Preencher o `config.h`
Abra `config.h` e troque os textos de exemplo:

| Constante | O que colocar | Exemplo |
|---|---|---|
| `WIFI_SSID` | Nome da rede 2,4 GHz | `"MinhaRede"` |
| `WIFI_PASSWORD` | Senha do Wi-Fi | `"minhasenha"` |
| `TELEMETRY_URL` | Rede local: `http://` + IP do servidor + `:8080/api/telemetrias`. Servidor ou túnel com HTTPS: `https://` + endereço público + `/api/telemetrias` | `"http://192.168.0.10:8080/api/telemetrias"` ou `"https://nome-do-tunel.trycloudflare.com/api/telemetrias"` |
| `DEVICE_ID` | Identificador da pulseira, igual ao `dispositivoId` do cadastro | `"ESP12E-PULSEIRA-001"` |

A pulseira precisa estar **cadastrada, ativa e vinculada a uma pessoa** no back-end. Caso contrário, a API responde 400.

### 4. Gravar na placa
1. Conecte a NodeMCU ao computador por USB.
2. Abra `pulseira_iot.ino` na Arduino IDE.
3. Em **Ferramentas**, selecione a placa **NodeMCU 1.0 (ESP-12E Module)** e a **porta COM** da placa.
4. Clique em **Carregar** (seta para a direita) e aguarde "Carregado".
5. Abra o **Monitor Serial** em **115200 baud**.

### 5. Conferir se funcionou
Mantenha a pulseira **parada por 2 s** ao ligar (calibração do giroscópio). No Monitor Serial devem aparecer, a cada 5 s:

```
Telemetria HTTP: 201 (queda=0 panico=0)
acc total: 1.10 g | Wi-Fi: ok
```

Depois de gravar, o computador não é mais necessário: a pulseira liga sozinha em qualquer carregador USB de 5 V, desde que o Wi-Fi configurado esteja ao alcance e o back-end esteja ligado.

### Se não funcionar

| Sintoma | Causa provável e solução |
|---|---|
| Mensagem "ATENCAO: config.h ainda tem os textos de exemplo" | `config.h` não foi preenchido. Preencha e grave de novo. |
| `Wi-Fi: sem conexao` ou LED piscando lento | Senha errada, rede de 5 GHz ou fora de alcance. Confira nome, senha e a banda da rede. |
| `Telemetria HTTP: -1` | A pulseira não alcança o servidor: IP errado, back-end desligado, computador em outra rede ou firewall bloqueando a porta 8080. |
| `Telemetria HTTP: 400` | Pulseira não encontrada, inativa ou sem pessoa vinculada. Confira `DEVICE_ID` e o cadastro. |
| `Telemetria HTTP: 401` ou `403` | O endpoint de telemetria exige login. Confira a configuração de segurança do back-end. |
| Placa não aparece na porta COM | Cabo só de carga (use um cabo de dados) ou falta o driver CH340. |
| Falha ao gravar | Solte o botão de pânico (D3/FLASH) ao conectar e tente de novo. |

## Ligações

| Módulo | Pino do módulo | NodeMCU |
|---|---|---|
| MPU6500 | VCC / GND | 3V / G |
| MPU6500 | SCL / SDA | D1 / D2 |
| Buzina ativa | + / − | D7 / G |
| Botão de pânico | um canto / canto na diagonal | D3 / G |
| LED | LED azul da própria placa (GPIO 2) | nada a ligar |

- O botão usa o pull-up interno, sem resistor externo. D3 é também o botão FLASH da placa, que funciona como botão de pânico.
- Não aperte o botão enquanto a placa liga ou reinicia: com D3 em nível baixo na partida, a placa entra em modo de gravação.
- Os pinos D5 e D6 não devem ser usados nesta placa, pois falharam nos testes elétricos.

## Comportamento

O firmware envia `POST /api/telemetrias` a cada cinco segundos, com aceleração, rotação e `quedaDetectada`.

| Situação | LED azul | Buzina |
|---|---|---|
| Repouso, Wi-Fi e API funcionando | Flash curto a cada 3 s | Silenciosa |
| Repouso, sem Wi-Fi ou API fora | Pisca lento (1 s) | Silenciosa |
| Queda detectada: contagem de 15 s | Pisca rápido | Pisca rápido |
| Queda não cancelada: alarme | Piscadas longas | Toca até o botão cancelar (limite de 5 min) |
| Pânico | 3 piscadas e pausa | 3 bipes e pausa, até o botão encerrar (limite de 5 min) |

### Botão

| Ação | Repouso | Queda | Pânico |
|---|---|---|---|
| Aperto curto | nada | cancela a queda | nada |
| Aperto longo (2 s) | aciona o pânico | nada | encerra o pânico |

Ao cancelar, a buzina dá 2 bipes curtos; ao acionar o pânico, 1 bipe.

### Fluxo da queda

1. O MPU6500 observa queda livre abaixo de `0,45 g` seguida de impacto acima de `2,5 g` em até 1,2 segundo. São limiares de partida: ajuste-os depois de testes controlados, pois não caracterizam detecção médica certificada.
2. Começa a contagem de 15 s com aviso sonoro. Nada é enviado nesse período.
3. Se o botão for apertado, a queda é cancelada e nenhum alerta sai.
4. Sem cancelamento, o firmware envia `quedaDetectada: true`, que gera um alerta `POSSIVEL_QUEDA` no back-end. O envio é repetido a cada 3 s até a API responder 2xx.

### Queda e pânico: sinais separados

O firmware manda **um** envio por alerta, com campos diferentes:

| Evento | Campos do envio | Alerta gerado no back-end |
|---|---|---|
| Queda confirmada | `quedaDetectada: true`, `panicoAcionado: false` | `POSSIVEL_QUEDA` |
| Pânico (botão 2 s) | `quedaDetectada: false`, `panicoAcionado: true` | `PANICO` |

O envio é repetido a cada 3 s até a API responder 2xx. As telemetrias periódicas (5 s) vão sempre com os dois campos `false`, para não repetir o alerta. O campo `quedaCancelada` é só informativo e o back-end o ignora.

### Parar o alarme pelo front

Durante o alarme (queda ou pânico) já enviado ao back-end, a pulseira consulta `GET /api/pulseiras/{DEVICE_ID}/alarme` a cada 2 s. Quando o cuidador marca o alerta como **resolvido** (ou falso positivo) no front, a resposta vira `{"alarmeAtivo":false}`: a buzina e o LED param e a pulseira dá 2 bipes de confirmação. Se a consulta falhar, o alarme continua até o botão ou o limite de 5 min. O endereço é derivado de `TELEMETRY_URL`, então só ele precisa ser ajustado.

## Comandos de teste pelo Monitor Serial

`q` simula queda, `p` simula pânico e `c` cancela.

## Pasta `build/`

Guarda o binário da última compilação, feito **com os textos de exemplo** do `config.h`. Ele não serve para gravar direto, pois não conecta em nenhuma rede: compile e grave sempre depois de preencher o `config.h`. O `.gitignore` do back-end já ignora pastas `build/`.
