# Workflows n8n – Prenatal Digital SUS

## Importação e ativação automáticas (Docker)

Quando você sobe o ambiente com `docker compose up` na pasta **prenatal-ia**:

1. O container do n8n inicia e o script **n8n-entrypoint.sh** roda.
2. Após o n8n ficar pronto (porta 5678), o workflow **prenatal-alert-webhook.json** é importado automaticamente.
3. O workflow é ativado via CLI: n8n 2.x usa `publish:workflow --id=<id>` (o ID é obtido após o import com `export:workflow --all`).
4. O n8n é reiniciado para aplicar a ativação (o webhook só é registrado após o restart).
5. O webhook `POST /webhook/prenatal-alert` fica disponível e o prenatal-ia deixa de receber 404.

**Nenhum passo manual** é necessário: importação e ativação são feitas pelo Docker.

**Nota:** `--all --active=true` ativa todos os workflows existentes no n8n. Se você criar outros workflows e quiser mantê-los inativos, desative-os na UI após o primeiro start.

## Arquivos

| Arquivo | Descrição |
|--------|------------|
| `prenatal-alert-webhook.json` | Workflow que recebe alertas do prenatal-ia e envia e-mails (gestante/profissional). |
| `n8n-entrypoint.sh` | Script usado pelo Docker: importa o workflow, ativa via CLI e reinicia o n8n. |

## Execução local (sem Docker)

Se estiver rodando o n8n fora do Docker, importe e ative manualmente:

1. Abra http://localhost:5678/home/workflows.
2. **Import from File** (ou menu ⋮ → Import).
3. Selecione `prenatal-alert-webhook.json`.
4. Ative o workflow (toggle **Active**).
