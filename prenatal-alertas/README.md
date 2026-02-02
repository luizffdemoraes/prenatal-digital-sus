# Prenatal Alertas

Serviço de análise de gestações e alertas (motor de regras clínicas). Notificações por **e-mail direto (SMTP)**.

## Subir com Docker Compose (comunicação com prontuário/agenda/documento)

Para o **prenatal-alertas** enxergar os dados já cadastrados (gestantes, prontuário), ele precisa usar o **mesmo banco** em que os outros serviços gravam. Por padrão, o container conecta no **Postgres do host na porta 5432** (onde roda o Postgres do projeto principal com prontuario/agenda/documento).

**Ordem recomendada:**

1. Subir o **Postgres e os serviços que criam os dados** (prontuário, agenda, documento) para que o banco em **localhost:5432** tenha as tabelas e os cadastros.
2. Na pasta **prenatal-alertas**:

```bash
docker compose up --build
```

Isso sobe o **prenatal-alertas** (porta 8083), conectando em **host.docker.internal:5432** (Postgres do host).

Por padrão o prenatal-alertas envia **e-mails diretamente por SMTP** (gestante e médico, quando cadastrados).

### Variáveis de ambiente (opcional)

- **SMTP_EMAIL** e **SMTP_PASSWORD** – para o envio de e-mails de alerta pelo próprio app. Devem estar definidas nas **variáveis de ambiente da máquina** (o Docker Compose repassa ao container). Sem elas o app não envia e-mail (apenas registra aviso no log).
- **DATASOURCE_URL** – por padrão o prenatal-alertas em Docker usa `jdbc:postgresql://host.docker.internal:5432/prenatal_digital_sus`. Para usar outro banco, defina `DATASOURCE_URL` (ex.: `jdbc:postgresql://postgres:5432/prenatal_digital_sus` se subir o Postgres deste compose com `docker compose --profile fullstack up`).

### Dados para o job de análise

O prenatal-alertas lê de **prontuario**, **agenda** e **documento**. O container usa o **Postgres do host (porta 5432)** onde esses serviços já criaram tabelas e cadastros. O job enxerga as gestantes e envia alertas por e-mail.

## Envio de e-mail (SMTP direto)

O prenatal-alertas envia e-mails **diretamente por SMTP** (Spring Mail), sem dependências externas de workflow.

- **Gestante:** recebe e-mail com assunto e corpo claros (“Pré-natal: pendências no seu acompanhamento”) e lista das **pendências identificadas** (ex.: ultrassom morfológico não encontrado, vacina pendente). Recomendação: procurar a unidade de saúde para agendar.
- **Médico:** quando **medico_email** está cadastrado no prontuário, recebe **alerta clínico** com as pendências que requerem atenção (target PROFESSIONAL), incluindo severidade e ID da gestante.

Defina **SMTP_EMAIL** e **SMTP_PASSWORD** (Gmail: use **senha de app**).

## Rodar a aplicação fora do Docker

```bash
# Terminal 1: sobe apenas Postgres (se precisar)
docker compose up postgres

# Terminal 2: roda a aplicação (usa localhost:5432)
mvn spring-boot:run
```

Se você rodar o Postgres deste compose (profile fullstack), ele fica em **localhost:5433**. Ajuste `application.properties` ou use `DATASOURCE_URL=jdbc:postgresql://localhost:5433/prenatal_digital_sus` ao rodar a aplicação no host.

## Endpoints

- **Health:** `GET http://localhost:8083/actuator/health`
- **Swagger UI:** `http://localhost:8083/swagger-ui.html`

## Por que não recebo e-mail?

Para **receber e-mails**, verifique:

1. **SMTP configurado.**  
   Defina **SMTP_EMAIL** e **SMTP_PASSWORD** nas variáveis de ambiente (ou no `application.properties` / Docker). Se estiverem vazios, o app não envia e o log mostrará *"Envio direto SMTP: SMTP_EMAIL não configurado"*.

2. **Dados que gerem alertas.**  
   A análise usa gestações ativas em `prontuario.prontuario` (CPF válido, idade gestacional 1–44), exames/vacinas em `documento` e consultas em `agenda`. Só há envio quando **há pelo menos um alerta** (ex.: ultrassom morfológico pendente, vacina pendente, consulta não agendada). Nos logs: *"Análise de gestações: X paciente(s) ativo(s)"* e *"E-mail enviado diretamente (SMTP) para ..."*.

3. **E-mail da gestante no prontuário.**  
   O app envia para **`prontuario.prontuario.email_paciente`** (o mesmo que o cadastro expõe como **emailPaciente**). O prenatal-alertas precisa usar o **mesmo banco** em que o serviço de prontuário gravou os dados. Para conferir o que está no banco: `scripts/definir-email-gestante-tuamaeaquelaursa.sql` (apenas SELECT).

4. **Gmail: usar Senha de app.**  
   Se o SMTP for Gmail (`smtp.gmail.com`), **SMTP_PASSWORD** deve ser uma **Senha de app**, não a senha normal da conta. [Conta Google](https://myaccount.google.com/) → Segurança → Verificação em 2 etapas → Senhas de app.

## Verificação: dados para alertas

O cadastro de gestante no serviço de prontuário **cria registros** em `prontuario.prontuario`. Para o prenatal-alertas considerar uma gestante, o registro precisa ter: **CPF com 11 dígitos** (apenas números) e **idade_gestacional_semanas entre 1 e 44**.

Para conferir no **mesmo banco** em que o prenatal-alertas conecta:

```bash
psql -U postgres -d prenatal_digital_sus -f scripts/verificar-dados-para-alertas.sql
```

Ou execute no DBeaver/pgAdmin a query em `scripts/verificar-dados-para-alertas.sql`. Se o resultado for **0** gestações ativas, não há envio de alerta.
