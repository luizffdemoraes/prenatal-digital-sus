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

- **Gestante:** recebe e-mail com assunto e corpo claros (“Pré-natal: pendências no seu acompanhamento”) e lista das **pendências identificadas** (ex.: ultrassom morfológico não encontrado, vacinas pendentes). Recomendação: procurar a unidade de saúde para agendar. Canal de suporte: Disque 136 – OuvSUS.
- **Médico:** quando **medico_email** está cadastrado no prontuário, recebe **alerta clínico** com as pendências que requerem atenção (target PROFESSIONAL), incluindo severidade e ID da gestante. Inclui vacinas pendentes (antitetânica, Hepatite B, Influenza) para orientação na consulta.

Os e-mails utilizam assinatura institucional "Pré-natal Digital – SUS Digital – Ministério da Saúde" e From com nome amigável "Pré-natal Digital SUS".

Defina **SMTP_EMAIL** e **SMTP_PASSWORD** (Gmail: use **senha de app**).

---

## Lógica de alertas

### Duração da gestação (referência clínica)

- **Ideal**: até 40 semanas (nove meses)
- **A termo (segura)**: entre 37 e 42 semanas
- O acompanhamento considera gestações de 1 a 44 semanas (pré-termo, termo e pós-termo)

### Regras por semana gestacional

| Exame/Vacina | Janela gestacional | Alvo e-mail | Responsabilidade |
|--------------|--------------------|-------------|------------------|
| **Translucência nucal** | 12ª–14ª semana | Gestante | Agendar ultrassom na UBS |
| **Ultrassom morfológico** | ≥ 20ª semana | Gestante | Agendar ultrassom morfológico |
| **Vacina antitetânica (dT/dTpa)** | ≥ 20ª semana até o final | Gestante + Médico | Gestante: procurar UBS; Médico: orientar e administrar |
| **Curva glicêmica** | ≥ 28ª semana | Gestante | Realizar exame de rastreamento de DMG |
| **Hepatite B** | Qualquer fase | Gestante + Médico | Esquema de 3 doses; orientar na consulta |
| **Influenza (gripe)** | Qualquer fase | Gestante + Médico | 1 dose anual; orientar e administrar |
| **Consulta agendada** | Contínuo | Médico | Garantir próxima consulta |
| **Alto risco** | Conforme exames | Médico | Atenção clínica prioritária |
| **Ecocardiograma fetal** | 20ª–24ª semana | Médico | Solicitar – avaliação cardíaca fetal |
| **Exames de sangue** | A partir da 8ª semana | Médico | Solicitar hemograma, tipagem, glicemia, sorologias |
| **Exames de urina** | A partir da 8ª semana | Médico | Solicitar EAS, urocultura |

### Vacinas (Calendário Nacional – PNI)

- **dTpa**: a partir da 20ª semana (quinto mês) até o final da gravidez; 1 dose por gestação
- **Hepatite B**: qualquer fase; 3 doses (intervalos: 1 mês entre 1ª e 2ª; 6 meses entre 1ª e 3ª)
- **Influenza**: qualquer fase; 1 dose anual da vacina da temporada

### Diferenciação de e-mails

| Destinatário | Mensagem | Responsabilidade |
|--------------|----------|------------------|
| **Gestante** | Linguagem clara e orientadora | Procurar UBS, agendar exames, tomar vacinas |
| **Médico** | Linguagem clínica com severidade e ID | Verificar prontuário, orientar, administrar vacinas, contatar gestante |

### Exames que o médico deve solicitar (se ausentes)

- **Ultrassom morfológico** (≥ 20ª semana)
- **Ecocardiograma fetal** (20ª–24ª semana – avaliação cardíaca)
- **Exames de sangue** (hemograma, tipagem, glicemia, sorologias) – a partir da 8ª semana
- **Exames de urina** (EAS, urocultura) – a partir da 8ª semana

### Tipos de vacina aceitos (registro em `documento.vacina`)

| Vacina | Valores aceitos |
|--------|-----------------|
| Antitetânica | DTPA, DTAP, DT, DUPLA_ADULTO |
| Hepatite B | HEPATITE_B, HEPATITEB, HB |
| Influenza | INFLUENZA, GRIPE, FLU |

### Tipos de exame aceitos (registro em `documento.documento_medico`)

| Categoria | Exemplos |
|-----------|----------|
| Sangue | HEMOGRAMA, TIPAGEM_SANGUINEA, GLICEMIA, VDRL, SOROLOGIA |
| Urina | EAS, UROCULTURA, URINA |
| Ecocardiograma | ECOCARDIOGRAMA, ECO_CARDIACA, ECOCARDIOGRAMA_FETAL |

Documentação completa: [docs/LOGICA-ALERTAS-E-EMAILS.md](docs/LOGICA-ALERTAS-E-EMAILS.md)

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
   O app envia para **`prontuario.prontuario.email_paciente`** (o mesmo que o cadastro expõe como **emailPaciente**). O prenatal-alertas precisa usar o **mesmo banco** em que o serviço de prontuário gravou os dados. Para conferir o que está no banco: `scripts/verificar-email-gestante.sql` (apenas SELECT).

4. **Gmail: usar Senha de app.**  
   Se o SMTP for Gmail (`smtp.gmail.com`), **SMTP_PASSWORD** deve ser uma **Senha de app**, não a senha normal da conta. [Conta Google](https://myaccount.google.com/) → Segurança → Verificação em 2 etapas → Senhas de app.

## Verificação: dados para alertas

O cadastro de gestante no serviço de prontuário **cria registros** em `prontuario.prontuario`. Para o prenatal-alertas considerar uma gestante, o registro precisa ter: **CPF com 11 dígitos** (apenas números) e **idade_gestacional_semanas entre 1 e 44** (gravidez ideal 40 sem; termo 37–42 sem).

Para conferir no **mesmo banco** em que o prenatal-alertas conecta:

```bash
psql -U postgres -d prenatal_digital_sus -f scripts/verificar-dados-para-alertas.sql
```

Ou execute no DBeaver/pgAdmin a query em `scripts/verificar-dados-para-alertas.sql`. Se o resultado for **0** gestações ativas, não há envio de alerta.
