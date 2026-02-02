# Prenatal IA

Serviço de análise de gestações e alertas (regras clínicas). Notificações por **e-mail direto (SMTP)** ou, opcionalmente, via **n8n** (webhook).

## Subir tudo com Docker Compose (comunicação com prontuário/agenda/documento)

Para o prenatal-ia **enxergar** os dados já cadastrados (gestantes, prontuário), ele precisa usar o **mesmo banco** em que os outros serviços gravam. Por padrão, o container do prenatal-ia conecta no **Postgres do host na porta 5432** (onde roda o Postgres do projeto principal com prontuario/agenda/documento).

**Ordem recomendada:**

1. Subir o **Postgres e os serviços que criam os dados** (prontuário, agenda, documento) para que o banco em **localhost:5432** tenha as tabelas e os cadastros.
2. Na pasta **prenatal-ia**:

```bash
docker compose up --build
```

Isso sobe:

1. **n8n** (porta 5678) – importa e ativa o workflow do webhook `prenatal-alert`; o healthcheck só passa quando o workflow está ativo.
2. **prenatal-ia** (porta 8083) – inicia depois do n8n estar saudável e conecta em **host.docker.internal:5432** (Postgres do host), garantindo **comunicação com os dados** já existentes.

Por padrão o prenatal-ia envia **e-mails diretamente por SMTP** (gestante e médico, quando cadastrados). O n8n é **opcional**: só é usado quando `USE_DIRECT_SMTP=false` (envio via webhook n8n).

### Variáveis de ambiente (opcional)

- **SMTP_EMAIL** e **SMTP_PASSWORD** – para o n8n enviar e-mails de alerta. Devem estar definidas nas **variáveis de ambiente da máquina** (o Docker Compose repassa ao container as variáveis do processo que roda `docker compose up`). Ao subir o n8n, o log do container mostrará `[n8n-init] SMTP configurado` ou `[n8n-init] AVISO: SMTP nao configurado no container` — use isso para confirmar se o n8n recebeu as variáveis.
- **DATASOURCE_URL** – por padrão o prenatal-ia em Docker usa `jdbc:postgresql://host.docker.internal:5432/prenatal_digital_sus` (Postgres do host, porta 5432). Para usar outro banco, defina `DATASOURCE_URL` (ex.: `jdbc:postgresql://postgres:5432/prenatal_digital_sus` se subir o Postgres deste compose com `docker compose --profile fullstack up`).

### Dados para o job de análise

O prenatal-ia lê de **prontuario**, **agenda** e **documento**. Com o ajuste acima, o container do prenatal-ia usa o **Postgres do host (porta 5432)** onde esses serviços já criaram tabelas e cadastros. Assim, o job enxerga as gestantes e pode enviar alertas ao n8n.

## Envio de e-mail (SMTP direto — padrão)

Por padrão o prenatal-ia envia e-mails **diretamente por SMTP** (Spring Mail), sem depender do n8n.

- **Gestante:** recebe e-mail com **assunto e corpo claros** (“Pré-natal: pendências no seu acompanhamento”) e lista das **pendências identificadas** (ex.: ultrassom morfológico não encontrado, vacina pendente). Recomendação: procurar a unidade de saúde para agendar.
- **Médico:** quando **medico_email** está cadastrado no prontuário, recebe **alerta clínico** com as pendências que requerem atenção (target PROFESSIONAL), incluindo severidade e ID da gestante.

Defina **SMTP_EMAIL** e **SMTP_PASSWORD** (Gmail: use **senha de app**). O n8n **não é necessário** para notificações quando o envio direto está ativo. Para voltar a usar o n8n (webhook), defina `USE_DIRECT_SMTP=false`.

## Rodar a aplicação fora do Docker

Se quiser apenas **Postgres + n8n** no Docker e a aplicação no host:

```bash
# Terminal 1: sobe Postgres e n8n (sem o serviço prenatal-ia)
docker compose up postgres n8n

# Terminal 2: roda a aplicação (usa localhost:5432 e localhost:5678)
mvn spring-boot:run
```

Se você rodar **só** Postgres + n8n deste compose (sem o serviço prenatal-ia), o Postgres fica em **localhost:5433** (não 5432). Ajuste `application.properties` ou use `DATASOURCE_URL=jdbc:postgresql://localhost:5433/prenatal_digital_sus` ao rodar a aplicação no host.

## Endpoints

- **Health:** `GET http://localhost:8083/actuator/health`
- **Swagger UI:** `http://localhost:8083/swagger-ui.html`
- **n8n:** `http://localhost:5678`

## Por que não recebo e-mail / não vejo interação?

Para **receber e-mails** e ver o fluxo funcionando, três coisas precisam estar certas:

1. **n8n deve estar rodando** quando o prenatal-ia envia alertas.  
   O app chama o webhook `http://localhost:5678/webhook/prenatal-alert`. Se o n8n não estiver no ar (por exemplo, se você rodar só `mvn spring-boot:run` sem subir o n8n), o envio falha e você verá no log: *"Webhook n8n indisponível"*.  
   **Solução:** subir o n8n (ex.: `docker compose up postgres n8n`) e manter a aplicação apontando para essa URL.

2. **SMTP configurado no n8n.**  
   O workflow do n8n envia e-mail usando a credencial SMTP. No Docker Compose isso vem de `SMTP_EMAIL` e `SMTP_PASSWORD` definidas nas **variáveis de ambiente da máquina** (o Compose repassa ao container). Se estiverem vazios no processo que roda `docker compose up`, o n8n não envia e-mail.  
   **Solução:** definir `SMTP_EMAIL` e `SMTP_PASSWORD` nas variáveis de ambiente do sistema/sessão e reiniciar o n8n (rodar `docker compose up` no mesmo terminal que enxerga essas variáveis).

   **Credencial SMTP:** O entrypoint do container n8n **cria e importa automaticamente** a credencial SMTP (nome `smtp`) a partir de `SMTP_EMAIL` e `SMTP_PASSWORD`. Basta subir com `docker compose up` tendo essas variáveis de ambiente definidas; não é necessário criar a credencial manualmente no n8n.

3. **Dados que gerem alertas.**  
   A análise usa gestações ativas em `prontuario.prontuario` (CPF válido, idade gestacional 1–44), exames/vacinas em `documento` e consultas em `agenda`. Só há envio para o n8n quando **há pelo menos um alerta** (ex.: ultrassom morfológico pendente, vacina pendente, consulta não agendada).  
   **Solução:** garantir que existam prontuários com gestação ativa e situações que disparem as regras (ex.: sem exame, sem vacina, sem consulta agendada). Nos logs você verá *"Análise de gestações: X paciente(s) ativo(s)"* e, quando houver alerta enviado, *"Alerta enviado para n8n: patientId=..."*.

4. **E-mail da gestante no prontuário.**  
   O workflow do n8n envia o e-mail para **`prontuario.prontuario.email_paciente`** (o mesmo que o cadastro expõe como **emailPaciente**). Se o prontuário já foi cadastrado com `emailPaciente` (ex.: `maria.silva@tuamaeaquelaursa.com`), não é necessário atualizar nada no banco.  
   O prenatal-ia precisa usar o **mesmo banco** em que o serviço de prontuário gravou os dados. Nos logs: *"Alerta enviado para n8n SEM e-mail da gestante"* indica que o app não está vendo `email_paciente` (conferir `DATASOURCE_URL`); *"patientEmail=ma***@tuamaeaquelaursa.com"* indica que o e-mail será enviado.  
   Para **só conferir** o que está no banco: `scripts/definir-email-gestante-tuamaeaquelaursa.sql` (apenas SELECT, sem UPDATE).

**Teste rápido:**  
- Subir n8n + Postgres (`docker compose up postgres n8n`).  
- Rodar a aplicação (`mvn spring-boot:run`).  
- A análise roda automaticamente a cada 10 minutos. Ver nos logs se apareceu *"X paciente(s) ativo(s)"* e *"Alerta enviado para n8n"*. Se o n8n tiver SMTP configurado, o e-mail será enviado pelo workflow.

## Verificação: deveria receber e-mail? Variáveis configuradas?

### 1) Verificar se há dados na base que gerariam alertas

O cadastro de gestante no serviço de prontuário **cria registros** em `prontuario.prontuario`. Para o prenatal-ia considerar uma gestante na análise, o registro precisa ter: **CPF preenchido com 11 dígitos** (apenas números) e **idade_gestacional_semanas entre 1 e 44**. Se o job não “enxergar” ninguém, pode ser que o prenatal-ia esteja apontando para **outro banco ou outra porta** (ex.: app no host em `localhost:5432` e o prontuário criado em outro Postgres).

Para conferir no **mesmo banco** em que o prenatal-ia conecta (conecte em `prenatal_digital_sus` na porta que você usa no `DATASOURCE_URL`):

```bash
# Exemplo com psql (Postgres na porta 5432 ou 5433)
psql -U postgres -d prenatal_digital_sus -f scripts/verificar-dados-para-alertas.sql
```

Ou execute no DBeaver/pgAdmin a query que está em `scripts/verificar-dados-para-alertas.sql`: ela mostra quantas **gestações ativas** existem (mesmo critério do app: CPF válido, 1–44 semanas). Se o resultado for **0**, não há envio de alerta nem e-mail.

### 2) Checklist das variáveis

| Variável | Onde usa | No repositório | Conclusão |
|----------|----------|----------------|-----------|
| **SMTP_EMAIL** / **SMTP_PASSWORD** | n8n (envio de e-mail pelo workflow) | No `docker-compose.yml` aparece `${SMTP_EMAIL:-}` e `${SMTP_PASSWORD:-}` (valor vem das variáveis de ambiente da máquina). | Devem estar definidas nas **variáveis de ambiente** do processo que roda `docker compose up`. Sem elas o n8n **não envia e-mail**. O log do container mostra `[n8n-init] SMTP configurado` ou `AVISO: SMTP nao configurado`. |
| **app.n8n.webhook-url** | prenatal-ia (chamada ao n8n) | Em `application.properties`: `http://localhost:5678/webhook/prenatal-alert`. | **Configurada**. Ao rodar `mvn spring-boot:run`, o app chama o n8n em localhost:5678; o n8n precisa estar rodando. |
| **DATASOURCE_URL** | prenatal-ia (banco) | Default em `application.properties`: `localhost:5432`. Postgres deste compose expõe a porta **5433** no host. | Se você usa só o Postgres deste projeto, use `DATASOURCE_URL=jdbc:postgresql://localhost:5433/prenatal_digital_sus` ao rodar a aplicação no host. |

**Se você já fez cadastro de gestante e o prontuário foi gerado:** confira (1) se o prenatal-ia usa o **mesmo banco e porta** em que o serviço de prontuário gravou os dados (`DATASOURCE_URL`); (2) se o registro tem **CPF com 11 dígitos** e **idade_gestacional_semanas** entre 1 e 44 (o job ignora CPF nulo ou com formato inválido); (3) se há **pelo menos um alerta** (ex.: exame pendente, vacina pendente, consulta não agendada) — sem alerta o app não chama o n8n.

**Resumo:** Para receber e-mail é preciso: (1) gestação ativa no mesmo banco que o prenatal-ia usa, com critérios acima; (2) n8n rodando; (3) **SMTP_EMAIL** e **SMTP_PASSWORD** definidas nas **variáveis de ambiente da máquina** (o processo que roda `docker compose up` deve enxergá-las para que o Compose as repasse ao container do n8n); (4) **email_paciente** preenchido no prontuário da gestante (ex.: `maria.silva@tuamaeaquelaursa.com`); (5) **credencial SMTP** — criada automaticamente pelo Docker a partir de `SMTP_EMAIL` e `SMTP_PASSWORD` (não é necessário configurar manualmente no n8n).

**Nota sobre o workflow n8n:** O nó "Send Email" do n8n usa o parâmetro **`text`** para o corpo do e-mail (não `message`). O arquivo `n8n-workflows/prenatal-alert-webhook.json` já está corrigido. Se você importou o workflow antes dessa correção, reinicie o n8n com `docker compose up --build` para que o entrypoint reimporte o workflow atualizado.

### 3) Log mostra "patientEmail=ma***@tuamaeaquelaursa.com" mas o e-mail não chega na caixa

O prenatal-ia está enviando o payload corretamente para o n8n; o problema está no **n8n** (execução do workflow) ou no **SMTP**. Conferir nesta ordem:

1. **Execuções do n8n**  
   Abra http://localhost:5678 → **Workflows** → **Prenatal Alert** → aba **Executions**. Veja a última execução: está **Success** (verde) ou **Error** (vermelho)? Clique nela e abra o nó **"Email para gestante"**. Se tiver falhado, o erro aparece ali (ex.: *Authentication failed*, *Connection refused*, *Invalid login*). Isso indica problema de **credencial SMTP** ou **rede**.

2. **Gmail: usar Senha de app**  
   Se o SMTP for Gmail (`smtp.gmail.com`), **SMTP_PASSWORD** tem de ser uma **Senha de app**, não a senha normal da conta. Senha normal não funciona para SMTP.  
   Como criar: [Conta Google](https://myaccount.google.com/) → **Segurança** → **Verificação em 2 etapas** (ativar se ainda não tiver) → **Senhas de app** → gerar uma senha para "Mail" e usar esse valor em **SMTP_PASSWORD**. Reinicie o n8n após alterar a variável (`docker compose up` no mesmo terminal onde as variáveis estão definidas).

3. **Spam**  
   Conferir a pasta **Spam** em https://tuamaeaquelaursa.com/maria.silva.

4. **Logs do container n8n**  
   Para ver erros de envio no próprio n8n:  
   `docker logs prenatal-n8n 2>&1 | tail -100`  
   (ou `docker compose logs n8n`) e procurar por mensagens de SMTP/email.

**Problema encontrado nos logs:** O entrypoint importava a credencial SMTP **enquanto as migrations do n8n ainda rodavam**. Com isso, o `n8n import:credentials` pode falhar ou não persistir, e o nó "Email para gestante" fica sem credencial (e-mail não é enviado, às vezes sem erro visível).  
**Correção aplicada:** o script agora espera **30 segundos** após o n8n responder (HTTP 200) antes de importar credenciais e workflow, para as migrations terminarem.  
**O que fazer:** recriar o container do n8n para aplicar o fix: `docker compose down` e `docker compose up --build`. Se ainda não receber e-mail, em http://localhost:5678 → **Credentials** verifique se existe a credencial **"smtp"**; se não existir, crie manualmente (Send Email, nome `smtp`, User/Password/SMTP) e confira em **Executions** do workflow "Prenatal Alert" se o nó "Email para gestante" falhou e qual erro aparece.
