# 🏥 Prenatal Digital SUS

## Tech Challenge - Sistema de Pré-Natal Digital

---

## 📑 ÍNDICE

- [Descrição do Projeto](#descrição-do-projeto)
- [Funcionalidades e Endpoints](#funcionalidades-e-endpoints)
  - [Auth Service](#auth-service-porta-8079)
  - [Agenda Service](#agenda-service-porta-8080)
  - [Prontuário Service](#prontuário-service-porta-8082)
  - [Documento Service](#documento-service-porta-8081)
  - [Alertas Service](#alertas-service-porta-8084)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Segurança](#segurança)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Clean Architecture](#clean-architecture)
- [Diagrama de Arquitetura](#diagrama-de-arquitetura)
- [Fluxogramas do Projeto](#fluxogramas-do-projeto)
- [Diagrama do Banco de Dados](#diagrama-do-banco-de-dados)
- [Requisitos](#requisitos)
- [Como Rodar o Projeto](#como-rodar-o-projeto)
- [Documentação da API](#documentação-da-api)
- [Cobertura de Código](#cobertura-de-código)
- [Collection e Environment Postman](#collection-e-environment-postman)
- [Fluxo de Autenticação](#fluxo-de-autenticação)

---

<a id="descrição-do-projeto"></a>
## 📌 Descrição do Projeto

O **Prenatal Digital SUS** é uma API modular e containerizada que oferece:

- **Autenticação e geração de token JWT** via OAuth2.
- **Agendamento de consultas pré-natais** com base na disponibilidade dos profissionais.
- **Prontuário eletrônico** para gestantes com histórico clínico.
- **Upload e gestão de documentos** (exames, vacinas) com armazenamento em S3.
- **Motor de alertas** que analisa as gestações e envia notificações por e-mail.

O sistema utiliza um banco PostgreSQL compartilhado com schemas isolados por serviço e comunicação síncrona via REST, com validação JWT em todos os endpoints protegidos.

---

<a id="funcionalidades-e-endpoints"></a>
## ⚙️ Funcionalidades e Endpoints

<a id="auth-service-porta-8079"></a>
### 🔐 Auth Service (porta 8079)

| Operação | Descrição | Acesso |
|----------|-----------|--------|
| POST   /oauth2/token | Gera token JWT (grant_type=password) | Público |
| POST   /v1/usuarios | Cria novo usuário | Autenticado |
| GET    /v1/usuarios/cpf/{cpf} | Obtém usuário por CPF | Autenticado |
| GET    /v1/usuarios/{id} | Obtém usuário por ID | Autenticado |
| PUT    /v1/usuarios/{id} | Atualiza usuário | Autenticado |
| PATCH  /v1/usuarios/{id}/senha | Altera senha | Autenticado |

---

<a id="agenda-service-porta-8080"></a>
### 📅 Agenda Service (porta 8080)

| Operação | Descrição | Acesso |
|----------|-----------|--------|
| POST   /api/consultas/agendar | Agenda nova consulta | Autenticado |
| DELETE /api/consultas/{id}/cancelar | Cancela consulta | Autenticado |
| GET    /api/disponibilidade | Lista horários disponíveis (médico, data) | Autenticado |
| GET    /api/gestantes/consultas | Lista consultas por CPF | Autenticado |
| GET    /api/gestantes/{id}/consultas | Lista consultas da gestante | Autenticado |
| POST   /api/agendas/medico | Cadastra agenda do médico | Autenticado |
| GET    /api/agendas/medico/{crm} | Obtém agenda do médico | Autenticado |
| PUT    /api/agendas/medico/{crm} | Atualiza agenda | Autenticado |
| DELETE /api/agendas/medico/{crm} | Remove agenda | Autenticado |

---

<a id="prontuário-service-porta-8082"></a>
### 📖 Prontuário Service (porta 8082)

| Operação | Descrição | Acesso |
|----------|-----------|--------|
| POST   /api/v1/prontuarios | Cria prontuário | Autenticado |
| GET    /api/v1/prontuarios/cpf/{cpf} | Obtém prontuário por CPF | Autenticado |
| PUT    /api/v1/prontuarios/cpf/{cpf} | Atualiza prontuário | Autenticado |
| PATCH  /api/v1/prontuarios/cpf/{cpf}/fatores-risco | Atualiza fatores de risco | Autenticado |
| GET    /api/v1/prontuarios/cpf/{cpf}/historico | Obtém histórico do prontuário | Autenticado |

---

<a id="documento-service-porta-8081"></a>
### 📄 Documento Service (porta 8081)

| Operação | Descrição | Acesso |
|----------|-----------|--------|
| POST   /api/prenatal-records/{cpf}/documents | Upload de documento (exame) | Autenticado |
| GET    /api/prenatal-records/{cpf}/documents | Lista documentos do paciente | Autenticado |
| GET    /api/documents/{id}/download | Download de documento | Autenticado |
| PATCH  /api/documents/{id}/inactivate | Inativa documento | Autenticado |
| DELETE /api/documents/{id}/permanent | Remove documento permanentemente | Autenticado |
| POST   /api/documents/{id}/request-delete | Solicita exclusão | Autenticado |
| POST   /api/prenatal-records/{cpf}/vacinas | Registra vacina | Autenticado |

---

<a id="alertas-service-porta-8084"></a>
### 📨 Alertas Service (porta 8084)

| Operação | Descrição | Acesso |
|----------|-----------|--------|
| Job agendado | Analisa gestações, gera alertas e envia e-mails | Interno |

O serviço de alertas executa periodicamente, consultando os dados de prontuário, agenda e documentos para identificar situações que exigem atenção (exames pendentes, vacinas em atraso, consultas) e notifica gestantes e médicos por e-mail.

**Obs.:** Para envio de e-mails, configure as variáveis de ambiente `SMTP_EMAIL` e `SMTP_PASSWORD` **nas variáveis de ambiente da máquina**. Ao subir com `docker compose up`, o Docker Compose obtém essas variáveis do ambiente da máquina e repassa aos containers.

---

<a id="segurança"></a>
## 🛡️ Segurança

| Validação | Descrição | Implementação |
|-----------|-----------|---------------|
| Autenticação JWT | Token obrigatório nas requisições protegidas | Spring Security OAuth2 Resource Server |
| JWKS | Validação de assinatura via endpoint /oauth2/jwks | prenatal-auth expõe chaves públicas |
| CORS | Origens permitidas configuráveis | application.properties |

---

<a id="tecnologias-utilizadas"></a>
## 🛠️ Tecnologias Utilizadas

| Stack | Tecnologia |
|-------|------------|
| Linguagem | Java 21 |
| Build | Maven |
| Framework | Spring Boot 3.x / 4.x |
| Banco de Dados | PostgreSQL 16 |
| Armazenamento | LocalStack (S3) |
| Autenticação | OAuth2 Authorization Server + JWT |
| Documentação | SpringDoc OpenAPI 3 (Swagger) |
| Containerização | Docker / Docker Compose |

---

<a id="estrutura-do-projeto"></a>
## 📂 Estrutura do Projeto

```
prenatal-digital-sus/
├── README.md                        # Documentação principal do projeto
├── CONFIGURACAO-VARIAVEIS-AMBIENTE.md   # Guia de variáveis de ambiente (SMTP, Gmail, Windows)
├── prenatal-auth/           # Microsserviço de autenticação (OAuth2 + JWT)
├── prenatal-agenda/         # Microsserviço de agendamento de consultas
├── prenatal-prontuario/     # Microsserviço de prontuário eletrônico
├── prenatal-documento/      # Microsserviço de documentos e vacinas
├── prenatal-alertas/        # Motor de alertas e notificações por e-mail
├── docker-compose.yml       # Orquestração unificada dos serviços
├── docker/
│   └── postgres-init/
│       └── 01-create-schemas.sql   # Criação dos schemas
├── collection/              # Collections e environments do Postman
└── DOCKER.md                # Instruções detalhadas do Docker
```

---

<a id="clean-architecture"></a>
## 🧹 Clean Architecture

Cada microsserviço segue os princípios da **Clean Architecture**, com camadas bem definidas:

- **application** – DTOs, use cases (regras de negócio)
- **domain** – Entidades, gateways (interfaces)
- **infrastructure** – Controllers, persistência, gateways (implementações)

### prenatal-auth

```
prenatal-auth/
├── src/main/java/br/com/hackathon/sus/prenatal_auth/
│   ├── application/
│   │   ├── dtos/
│   │   │   ├── requests/     (UserRequest, AddressRequest, PasswordRequest)
│   │   │   └── responses/    (UserResponse, AddressResponse)
│   │   └── usecases/
│   │       ├── CreateUserUseCase, CreateUserUseCaseImp
│   │       ├── FindUserByCpfUseCase, FindUserByCpfUseCaseImp
│   │       ├── FindUserByIdUseCase, FindUserByIdUseCaseImp
│   │       ├── UpdateUserUseCase, UpdateUserUseCaseImp
│   │       └── UpdatePasswordUseCase, UpdatePasswordUseCaseImp
│   ├── domain/
│   │   ├── entities/        (User, Address, Role)
│   │   └── gateways/        (UserGateway, RoleGateway)
│   └── infrastructure/
│       ├── config/          (AuthorizationServer, Security, DependencyInjection)
│       ├── controllers/     (UserController)
│       ├── exceptions/      (GlobalExceptionHandler)
│       ├── gateways/        (UserGatewayImpl, RoleGatewayImpl)
│       └── persistence/     (UserEntity, UserRepository, RoleRepository)
├── Dockerfile
└── pom.xml
```

### prenatal-agenda

```
prenatal-agenda/
├── src/main/java/br/com/hackathon/sus/prenatal_agenda/
│   ├── application/
│   │   ├── dtos/            (CreateAppointmentRequest, AppointmentResponse, etc.)
│   │   └── usecases/
│   │       ├── CreateAppointmentUseCase, CancelAppointmentUseCase
│   │       ├── CreateDoctorScheduleUseCase, ListAvailabilityUseCase
│   │       └── FindAppointmentsByPatientUseCase, etc.
│   ├── domain/
│   │   ├── entities/        (Appointment, DoctorSchedule, Weekday)
│   │   └── gateways/        (AppointmentGateway, DoctorScheduleGateway)
│   └── infrastructure/
│       ├── config/          (Flyway, Security, DependencyInjection)
│       ├── controllers/     (AppointmentController, AvailabilityController, etc.)
│       ├── gateways/        (AppointmentGatewayImpl, DoctorScheduleGatewayImpl)
│       └── persistence/     (AppointmentEntity, DoctorScheduleEntity)
├── Dockerfile
└── pom.xml
```

### prenatal-prontuario

```
prenatal-prontuario/
├── src/main/java/com/hackathon/sus/prenatal_prontuario/
│   ├── application/
│   │   ├── dtos/            (CreateMedicalRecordRequest, MedicalRecordResponse, etc.)
│   │   └── usecases/
│   │       ├── CreateMedicalRecordUseCase, UpdateMedicalRecordUseCase
│   │       ├── FindMedicalRecordByCpfUseCase, FindMedicalRecordHistoryUseCase
│   │       └── UpdateRiskFactorsUseCase, etc.
│   ├── domain/
│   │   ├── entities/        (MedicalRecord, MedicalRecordHistory, RiskFactor)
│   │   └── gateways/        (MedicalRecordGateway, MedicalRecordHistoryGateway)
│   └── infrastructure/
│       ├── config/          (Flyway, Security, DependencyInjection)
│       ├── controllers/     (MedicalRecordController)
│       ├── gateways/        (MedicalRecordGatewayImpl)
│       └── persistence/     (MedicalRecordEntity, MedicalRecordRepository)
├── Dockerfile
└── pom.xml
```

### prenatal-documento

```
prenatal-documento/
├── src/main/java/br/com/hackathon/sus/prenatal_documento/
│   ├── application/
│   │   ├── dtos/            (UploadDocumentRequest, DocumentResponse, VaccineResponse)
│   │   └── usecases/
│   │       ├── UploadDocumentUseCase, DownloadDocumentUseCase
│   │       ├── ListDocumentsUseCase, RegisterVaccineUseCase
│   │       ├── DeleteDocumentUseCase, InactivateDocumentUseCase
│   │       └── RequestDeleteDocumentUseCase
│   ├── domain/
│   │   ├── models/          (MedicalDocument, Vaccine)
│   │   ├── repositories/    (MedicalDocumentRepository, VaccineRepository)
│   │   └── gateways/        (StorageGateway)
│   └── infrastructure/
│       ├── config/          (S3Config, Flyway, Security)
│       ├── controllers/     (DocumentController, VaccineController)
│       ├── gateways/        (S3StorageGateway)
│       └── persistence/     (MedicalDocumentEntity, VaccineEntity)
├── Dockerfile
└── pom.xml
```

### prenatal-alertas

```
prenatal-alertas/
├── src/main/java/br/com/hackathon/sus/prenatal_alertas/
│   ├── application/
│   │   └── usecases/        (AnalyzeAllPregnanciesUseCase, AnalyzeAllPregnanciesUseCaseImpl)
│   ├── domain/
│   │   ├── entities/        (PregnantPatient, PrenatalAlert, ExamRecord, VaccineRecord)
│   │   ├── enums/           (AlertType, AlertSeverity, NotificationTarget)
│   │   ├── gateways/        (NotificationOrchestratorGateway)
│   │   └── repositories/    (ProntuarioRepository, AgendaRepository, DocumentoRepository)
│   └── infrastructure/
│       ├── gateways/        (DirectSmtpNotificationGatewayImpl)
│       ├── persistence/     (ProntuarioRepositoryImpl, AgendaRepositoryImpl, DocumentoRepositoryImpl)
│       └── scheduler/       (PrenatalAnalysisScheduler)
├── Dockerfile
└── pom.xml
```

---

<a id="diagrama-de-arquitetura"></a>
## 📡 Diagrama de Arquitetura

Os diagramas principais são exibidos em PNG para melhor visualização. Código-fonte em [docs/diagrams/](docs/diagrams/) (arquivos `.mmd`).

### Visão Geral dos Serviços

![Visão geral dos serviços e infraestrutura](docs/diagrams/arquitetura-visao-geral.png)

### Fluxo de Comunicação e Dependências

![Fluxo de comunicação e dependências](docs/diagrams/fluxo-comunicacao.png)

### Fluxo de Autenticação

![Fluxo de autenticação OAuth2/JWT](docs/diagrams/fluxo-autenticacao.png)

---

<a id="fluxogramas-do-projeto"></a>
## 📊 Fluxogramas do Projeto

### Fluxograma 1: Onboarding e Cadastro Inicial

![Onboarding e cadastro inicial](docs/diagrams/fluxo-onboarding.png)

### Fluxograma 2: Agendamento de Consulta

![Agendamento de consulta](docs/diagrams/fluxo-agendamento.png)

### Fluxograma 3: Upload e Gestão de Documentos

![Upload e gestão de documentos](docs/diagrams/fluxo-documentos.png)

### Fluxograma 4: Motor de Alertas (prenatal-alertas)

![Motor de alertas](docs/diagrams/fluxo-motor-alertas.png)

### Fluxograma 5: Ciclo Completo do Pré-Natal

![Ciclo completo do pré-natal](docs/diagrams/fluxo-ciclo-completo.png)

### Fluxograma 6: Cancelamento de Consulta

![Cancelamento de consulta](docs/diagrams/fluxo-cancelamento.png)

---

<a id="diagrama-do-banco-de-dados"></a>
## 🗄️ Diagrama do Banco de Dados

### Visão Geral da Arquitetura de Dados

![Visão geral da arquitetura de dados](docs/diagrams/banco-dados-visao.png)

### Diagrama Entidade-Relacionamento

![Diagrama entidade-relacionamento](docs/diagrams/banco-er.png)

### Schemas e Serviços

![Schemas e serviços](docs/diagrams/banco-schemas-servicos.png)

---

## 🗄️ Banco de Dados

- **PostgreSQL 16** com um único banco `prenatal_digital_sus`
- **Schemas isolados:** `auth`, `prontuario`, `agenda`, `documento`
- **Flyway** em cada serviço para migrações
- Script de init em `docker/postgres-init/01-create-schemas.sql`

---

<a id="requisitos"></a>
## 📋 Requisitos

- Java 21
- Maven 3.9+
- Docker e Docker Compose
- Postman (para testes de API)

---

<a id="como-rodar-o-projeto"></a>
## ▶️ Como Rodar o Projeto

### Via Docker Compose (recomendado)

Na raiz do projeto:

```bash
# Subir todos os serviços
docker compose up --build -d

# Acompanhar logs
docker compose logs -f

# Parar (sempre use isso antes de subir de novo, para evitar conflito de nomes)
docker compose down

# Parar e remover TUDO, inclusive dados do banco (PostgreSQL e LocalStack)
docker compose down -v
```

**Se aparecer erro de conflito de nome** (ex.: `The container name "/prenatal-postgres" is already in use`): pare e remova os containers da stack, depois suba de novo na raiz do projeto: `docker compose down` e em seguida `docker compose up -d`.

### Variáveis de ambiente (envio de e‑mail via Gmail)

As variáveis de ambiente abaixo são usadas pelo serviço de alertas para enviar e‑mails:

- **`SMTP_EMAIL`** → seu e‑mail do Gmail (ex: `seu-email@gmail.com`)
- **`SMTP_PASSWORD`** → senha de app gerada no Gmail (NÃO é a senha normal da conta)

**Regra do projeto:** essas variáveis **devem estar definidas nas variáveis de ambiente da máquina**. Ao executar `docker compose up`, o Docker Compose **obtém** os valores das variáveis de ambiente definidas na máquina e repassa aos containers. Não é usado arquivo `.env` no projeto.

**Guia completo:** [CONFIGURACAO-VARIAVEIS-AMBIENTE.md](CONFIGURACAO-VARIAVEIS-AMBIENTE.md) (na raiz do projeto).

---

### 1️⃣ Etapa obrigatória no Gmail: gerar senha de app

Para usar Gmail como servidor SMTP, você precisa **obrigatoriamente** gerar uma **senha de app**.  
Resumo do processo (feito uma única vez por conta):

- **Passo 1 – Ativar verificação em duas etapas**
  1. Acesse `https://myaccount.google.com/security`
  2. Em **“Como fazer login no Google”**, clique em **“Verificação em duas etapas”**
  3. Siga o passo a passo (confirmação por SMS, etc.) até ficar **ATIVADA**

- **Passo 2 – Gerar a senha de app**
  1. Acesse `https://myaccount.google.com/apppasswords`
  2. Faça login se for pedido
  3. Em **“Selecionar app”**, escolha **“Mail”**
  4. Em **“Selecionar dispositivo”**, escolha **“Outro (nome personalizado)”**
  5. Digite um nome (ex.: `Prenatal Digital SUS`) e clique em **“Gerar”**
  6. O Google vai mostrar uma senha de **16 caracteres** (ex.: `abcd efgh ijkl mnop`)
  7. **Copie essa senha** e remova os espaços, ficando assim: `abcdefghijklmnop`

> Essa senha de 16 caracteres (sem espaços) é o valor que será usado em `SMTP_PASSWORD`.

---

### 2️⃣ Criar as variáveis de ambiente no Windows (manual)

Configuração **manual**, direto nas variáveis de ambiente do Windows:

1. Pressione `Win + R`, digite `sysdm.cpl` e pressione **Enter**
2. Na janela **“Propriedades do Sistema”**, vá na aba **“Avançado”**
3. Clique no botão **“Variáveis de Ambiente...”**
4. Na seção **“Variáveis de usuário”**, clique em **“Novo...”**
   - **Nome da variável:** `SMTP_EMAIL`  
   - **Valor da variável:** seu e‑mail Gmail completo (ex.: `seu-email@gmail.com`)  
   - Clique em **OK**
5. Ainda em **“Variáveis de usuário”**, clique de novo em **“Novo...”**
   - **Nome da variável:** `SMTP_PASSWORD`  
   - **Valor da variável:** a senha de app de 16 caracteres gerada na etapa do Gmail (sem espaços), ex.: `abcdefghijklmnop`  
   - Clique em **OK**
6. Clique em **OK** na janela de **“Variáveis de Ambiente”**
7. Clique em **OK** na janela de **“Propriedades do Sistema”**
8. **Feche completamente** o PowerShell / CMD que estiver aberto
9. Abra um **novo** PowerShell / CMD

---

### 3️⃣ Conferir se as variáveis foram criadas

No **novo** PowerShell, digite:

```powershell
echo $env:SMTP_EMAIL
echo $env:SMTP_PASSWORD
```

- Se aparecerem os valores configurados, está correto.
- Se vier em branco, revise os passos de criação das variáveis no Windows.

Depois disso, você já pode subir o projeto com:

```powershell
docker compose up --build -d
```

e acompanhar os logs do serviço de alertas:

```powershell
docker compose logs -f prenatal-alertas
```

Se as variáveis estiverem corretas, o serviço conseguirá autenticar no Gmail e enviar os e‑mails.

### Portas e URLs

| Serviço          | Porta | URL Base                |
|------------------|-------|-------------------------|
| prenatal-auth    | 8079  | http://localhost:8079   |
| prenatal-agenda  | 8080  | http://localhost:8080   |
| prenatal-documento | 8081| http://localhost:8081   |
| prenatal-prontuario | 8082| http://localhost:8082   |
| prenatal-alertas | 8084  | http://localhost:8084   |
| PostgreSQL       | 5432  | localhost:5432          |
| LocalStack (S3)  | 4566  | http://localhost:4566   |

---

<a id="documentação-da-api"></a>
## 📚 Documentação da API

Cada serviço expõe **Swagger UI** em:

- Auth: http://localhost:8079/swagger-ui.html
- Agenda: http://localhost:8080/swagger-ui.html
- Documento: http://localhost:8081/swagger-ui.html
- Prontuário: http://localhost:8082/swagger-ui.html
- Alertas: http://localhost:8084/swagger-ui.html

---

<a id="cobertura-de-código"></a>
## 📊 Cobertura de Código

Todas as aplicações usam **JaCoCo** para cobertura de testes. O relatório é gerado automaticamente ao rodar os testes. Instruções detalhadas e comandos: [scripts/COBERTURA-CODIGO.md](scripts/COBERTURA-CODIGO.md).

Relatórios visuais da cobertura por projeto estão na pasta [docs/coverage](docs/coverage/):

| Projeto | Imagem |
|---------|--------|
| prenatal-auth | [prenatal-auth.png](docs/coverage/prenatal-auth.png) |
| prenatal-agenda | [prenatal-agenda.png](docs/coverage/prenatal-agenda.png) |
| prenatal-prontuario | [prenatal-prontuario.png](docs/coverage/prenatal-prontuario.png) |
| prenatal-documento | [prenatal-documento.png](docs/coverage/prenatal-documento.png) |
| prenatal-alertas | [prenatal-alertas.png](docs/coverage/prenatal-alertas.png) |

### Cobertura por projeto (visão geral)

#### prenatal-auth
![Cobertura de código - prenatal-auth](docs/coverage/prenatal-auth.png)

#### prenatal-agenda
![Cobertura de código - prenatal-agenda](docs/coverage/prenatal-agenda.png)

#### prenatal-prontuario
![Cobertura de código - prenatal-prontuario](docs/coverage/prenatal-prontuario.png)

#### prenatal-documento
![Cobertura de código - prenatal-documento](docs/coverage/prenatal-documento.png)

#### prenatal-alertas
![Cobertura de código - prenatal-alertas](docs/coverage/prenatal-alertas.png)

### Rodar cobertura em todas as aplicações

Na **raiz do projeto**:

**PowerShell:**
```powershell
.\scripts\coverage.ps1
```

**CMD:**
```cmd
scripts\coverage.cmd
```

O script executa `mvn clean test` em cada módulo (prenatal-auth, prenatal-agenda, prenatal-prontuario, prenatal-documento, prenatal-alertas). Ao final, os relatórios ficam em cada pasta do módulo.

### Rodar cobertura em um único módulo

```bash
cd prenatal-auth   # ou prenatal-agenda, prenatal-prontuario, prenatal-documento, prenatal-alertas
mvn clean test
```

### Onde ver o relatório

| Módulo            | Caminho do relatório (HTML)                    |
|-------------------|-------------------------------------------------|
| prenatal-auth     | `prenatal-auth/target/site/jacoco/index.html`   |
| prenatal-agenda   | `prenatal-agenda/target/site/jacoco/index.html` |
| prenatal-prontuario | `prenatal-prontuario/target/site/jacoco/index.html` |
| prenatal-documento | `prenatal-documento/target/site/jacoco/index.html` |
| prenatal-alertas  | `prenatal-alertas/target/site/jacoco/index.html`  |

Abra o `index.html` no navegador para ver cobertura de instruções, ramos e linhas por pacote. Configuração do JaCoCo (exclusões de config, DTOs, entities) está no `pom.xml` de cada módulo.

---

<a id="collection-e-environment-postman"></a>
## 🧪 Collection e Environment Postman

- **Collection:** `collection/Prenatal-Digital-SUS-API-Unificada.postman_collection.json`
- **Environment:** `collection/Prenatal-Digital-SUS-Local.postman_environment.json`

Importe ambos no Postman e configure o token JWT obtido em `POST /oauth2/token` do prenatal-auth.

**Ordem de uso:** A collection está numerada (01, 02, 03…) na ordem obrigatória de execução: **01 - Prenatal Autenticação** (criar usuários → obter token) → **02 - Prenatal Agenda** (criar agenda do médico → agendar consulta) → **03 - Prenatal Prontuário** (criar prontuário) → **04 - Prenatal Documento** (upload documento). Pastas e requisições estão em português; as obrigatórias estão indicadas no nome.

---

<a id="fluxo-de-autenticação"></a>
## 🔒 Fluxo de Autenticação

1. Obter token: `POST http://localhost:8079/oauth2/token` com `grant_type=password`, `username`, `password`, `client_id`, `client_secret`
2. Usar o `access_token` no header: `Authorization: Bearer {token}`
3. Acessar os demais serviços (agenda, prontuário, documento) com o mesmo token
