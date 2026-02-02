#!/bin/sh
# Entrypoint que inicia o n8n, importa e ativa o workflow prenatal-alert automaticamente.
# Uso: no docker-compose, montar esta pasta em /workflows e usar este script como entrypoint.

set -e

# Verifica se as variáveis SMTP chegaram ao container (não exibe valores por segurança)
if [ -n "$SMTP_EMAIL" ] && [ -n "$SMTP_PASSWORD" ]; then
  echo "[n8n-init] SMTP configurado: SMTP_EMAIL e SMTP_PASSWORD presentes (envio de e-mail habilitado)."
else
  echo "[n8n-init] AVISO: SMTP nao configurado no container (SMTP_EMAIL ou SMTP_PASSWORD vazios). E-mails nao serao enviados. Defina SMTP_EMAIL e SMTP_PASSWORD nas variaveis de ambiente da maquina e rode docker compose up no mesmo terminal."
fi

# Inicia o n8n em background (CMD do container)
"$@" &
pid=$!

# Aguarda o n8n ficar pronto (até ~60s)
i=0
while [ $i -lt 30 ]; do
  if node -e "require('http').get('http://localhost:5678/', r => process.exit(r.statusCode === 200 ? 0 : 1)).on('error', () => process.exit(1))" 2>/dev/null; then
    break
  fi
  i=$((i + 1))
  sleep 2
done

# Aguarda as migrations do n8n terminarem (evita falha ao importar credenciais/workflow)
echo "[n8n-init] Aguardando migrations do n8n (30s)..."
sleep 30

# Cria e importa credencial SMTP automaticamente (evita "Credentials for Send Email are not set")
if [ -n "$SMTP_EMAIL" ] && [ -n "$SMTP_PASSWORD" ]; then
  echo "[n8n-init] Gerando credencial SMTP para importacao..."
  node -e "
    const cred = [{
      id: 'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
      name: 'smtp',
      type: 'smtp',
      data: {
        user: process.env.SMTP_EMAIL || '',
        password: process.env.SMTP_PASSWORD || '',
        host: 'smtp.gmail.com',
        port: 587,
        secure: false
      }
    }];
    require('fs').writeFileSync('/tmp/smtp-credentials.json', JSON.stringify(cred));
  "
  if [ -f /tmp/smtp-credentials.json ]; then
    echo "[n8n-init] Importando credencial SMTP (nome: smtp)..."
    if n8n import:credentials --input=/tmp/smtp-credentials.json 2>&1; then
      echo "[n8n-init] Credencial SMTP importada com sucesso."
    else
      echo "[n8n-init] AVISO: Falha ao importar credencial SMTP. Verifique o erro acima. No n8n (http://localhost:5678) crie manualmente uma credencial Send Email com nome 'smtp'."
    fi
    rm -f /tmp/smtp-credentials.json
  fi
fi

# Importa e ativa o workflow se o arquivo existir
if [ -f /workflows/prenatal-alert-webhook.json ]; then
  echo "[n8n-init] Importando workflow prenatal-alert-webhook.json..."
  n8n import:workflow --input=/workflows/prenatal-alert-webhook.json || true

  echo "[n8n-init] Exportando workflows para obter ID..."
  n8n export:workflow --all --output=/tmp/all.json || true
  if [ -f /tmp/all.json ]; then
    echo "[n8n-init] Primeiros 400 chars do export:"
    head -c 400 /tmp/all.json
    echo ""
  fi

  node -e "
    var fs = require('fs');
    try {
      const d = require('/tmp/all.json');
      const list = Array.isArray(d) ? d : (d.workflows || (d.data && d.data.workflows) || [d]);
      const matches = list.filter(function(x) { return x.name && x.name.indexOf('Prenatal Alert') >= 0; });
      function hasEmailSendNode(w) {
        const nodes = w.nodes || [];
        return nodes.some(function(n) { return n.type === 'n8n-nodes-base.emailSend'; });
      }
      function hasSendemailNode(w) {
        const nodes = w.nodes || [];
        return nodes.some(function(n) { return n.type === 'n8n-nodes-base.sendemail'; });
      }
      var activate = matches.find(hasEmailSendNode);
      var deactivate = matches.filter(hasSendemailNode);
      console.log(activate && activate.id ? activate.id : '');
      if (deactivate.length) fs.writeFileSync('/tmp/deactivate_ids.txt', deactivate.map(function(w){ return w.id; }).join('\n'));
    } catch (e) { console.error('Parse export:', e.message); }
  " > /tmp/activate_id.txt
  WF_ID=$(cat /tmp/activate_id.txt 2>/dev/null | tr -d '\n')
  echo "[n8n-init] WF_ID obtido (workflow com nó emailSend): '$WF_ID'"

  if [ -f /tmp/deactivate_ids.txt ]; then
    echo "[n8n-init] Removendo workflows antigos (nó sendemail) via API..."
    while read -r old_id; do
      if [ -n "$old_id" ]; then
        n8n update:workflow --id="$old_id" --active=false 2>/dev/null || true
        curl -s -X DELETE "http://localhost:5678/rest/workflows/$old_id" 2>/dev/null && echo "[n8n-init] Workflow $old_id removido." || true
      fi
    done < /tmp/deactivate_ids.txt
  fi

  if [ -n "$WF_ID" ]; then
    echo "[n8n-init] Ativando workflow com update:workflow --id=$WF_ID --active=true ..."
    n8n update:workflow --id="$WF_ID" --active=true || true
    echo "[n8n-init] Workflow ativado (id=$WF_ID). Reiniciando n8n..."
  else
    echo "[n8n-init] AVISO: Workflow nao encontrado no export. Reiniciando n8n..."
  fi
  kill $pid 2>/dev/null || true
  sleep 3
  exec "$@"
fi

# Sem arquivo de workflow: mantém o n8n que já está rodando em background
wait $pid
