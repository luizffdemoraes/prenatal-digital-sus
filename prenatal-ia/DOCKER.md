# Docker - prenatal-ia + n8n

## Subir os serviços

```bash
cd prenatal-ia
docker compose up -d
```

- **n8n**: http://localhost:5678 (interface de criação de workflows)
- **prenatal-ia**: http://localhost:8083 (health: /actuator/health)

## Configurar o webhook no n8n

1. Acesse http://localhost:5678
2. Crie um novo workflow
3. Adicione o nó **Webhook**
4. Defina o path: `prenatal-alert`
5. Método: **POST**
6. Salve e **ative** o workflow
7. A URL do webhook será: `http://localhost:5678/webhook/prenatal-alert`

### Workflow de exemplo

O arquivo `n8n-workflows/prenatal-alert-webhook.json` pode ser importado em **Settings > Import** no n8n como referência.

## Variáveis de ambiente (opcional)

| Variável | Default | Descrição |
|----------|---------|-----------|
| APP_PRONTUARIO_BASE_URL | http://host.docker.internal:8082 | URL do Prontuário |
| APP_AGENDA_BASE_URL | http://host.docker.internal:8080 | URL da Agenda |
| APP_DOCUMENTO_BASE_URL | http://host.docker.internal:8081 | URL do Documento |
| APP_AUTH_BASE_URL | http://host.docker.internal:8079 | URL do Auth (para obter e-mail da gestante por CPF) |
| APP_N8N_WEBHOOK_URL | http://n8n:5678/webhook/prenatal-alert | Webhook n8n (já configurado) |

## Pré-requisitos

Os serviços **Prontuário** (8082), **Agenda** (8080), **Documento** (8081) e **Auth** (8079) devem estar rodando no host para o prenatal-ia conseguir consultá-los.

## Payload enviado ao n8n

O webhook recebe um JSON com `patientEmail` (obtido do Auth por CPF), para que o n8n possa enviar e-mails às gestantes:

```json
{
  "patientId": "...",
  "patientName": "...",
  "patientEmail": "gestante@example.com",
  "gestationalWeeks": 24,
  "alerts": [...]
}
```
