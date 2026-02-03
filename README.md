# 🏥 Prenatal Digital SUS

## Tech Challenge - Sistema de Pré-Natal Digital

---

## 📑 ÍNDICE

- [Descrição do Projeto](#-descrição-do-projeto)
- [Funcionalidades e Endpoints](#-funcionalidades-e-endpoints)
  - [Auth Service](#-auth-service)
  - [Agenda Service](#-agenda-service)
  - [Prontuário Service](#-prontuário-service)
  - [Documento Service](#-documento-service)
  - [Alertas Service](#-alertas-service)
- [Tecnologias Utilizadas](#️-tecnologias-utilizadas)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Clean Architecture](#-clean-architecture)
- [Diagrama de Arquitetura](#-diagrama-de-arquitetura)
- [Fluxogramas do Projeto](#-fluxogramas-do-projeto)
- [Diagrama do Banco de Dados](#diagrama-do-banco-de-dados)
- [Requisitos](#-requisitos)
- [Como Rodar o Projeto](#-como-rodar-o-projeto)
- [Documentação da API](#-documentação-da-api)
- [Cobertura de Código](#-cobertura-de-código)
- [Collection e Environment Postman](#-collection-e-environment-postman)

---

## 📌 Descrição do Projeto

O **Prenatal Digital SUS** é uma API modular e containerizada que oferece:

- **Autenticação e geração de token JWT** via OAuth2.
- **Agendamento de consultas pré-natais** com base na disponibilidade dos profissionais.
- **Prontuário eletrônico** para gestantes com histórico clínico.
- **Upload e gestão de documentos** (exames, vacinas) com armazenamento em S3.
- **Motor de alertas** que analisa as gestações e envia notificações por e-mail.

O sistema utiliza um banco PostgreSQL compartilhado com schemas isolados por serviço e comunicação síncrona via REST, com validação JWT em todos os endpoints protegidos.

---

## ⚙️ Funcionalidades e Endpoints

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

### 📖 Prontuário Service (porta 8082)

| Operação | Descrição | Acesso |
|----------|-----------|--------|
| POST   /api/v1/prontuarios | Cria prontuário | Autenticado |
| GET    /api/v1/prontuarios/cpf/{cpf} | Obtém prontuário por CPF | Autenticado |
| PUT    /api/v1/prontuarios/cpf/{cpf} | Atualiza prontuário | Autenticado |
| PATCH  /api/v1/prontuarios/cpf/{cpf}/fatores-risco | Atualiza fatores de risco | Autenticado |
| GET    /api/v1/prontuarios/cpf/{cpf}/historico | Obtém histórico do prontuário | Autenticado |

---

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

### 📨 Alertas Service (porta 8084)

| Operação | Descrição | Acesso |
|----------|-----------|--------|
| Job agendado | Analisa gestações, gera alertas e envia e-mails | Interno |

O serviço de alertas executa periodicamente, consultando os dados de prontuário, agenda e documentos para identificar situações que exigem atenção (exames pendentes, vacinas em atraso, consultas) e notifica gestantes e médicos por e-mail.

**Obs.:** Para envio de e-mails, configure as variáveis de ambiente `SMTP_EMAIL` e `SMTP_PASSWORD` na máquina ou no arquivo `.env`.

---

## 🛡️ Segurança

| Validação | Descrição | Implementação |
|-----------|-----------|---------------|
| Autenticação JWT | Token obrigatório nas requisições protegidas | Spring Security OAuth2 Resource Server |
| JWKS | Validação de assinatura via endpoint /oauth2/jwks | prenatal-auth expõe chaves públicas |
| CORS | Origens permitidas configuráveis | application.properties |

---

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

## 📂 Estrutura do Projeto

```
prenatal-digital-sus/
├── prenatal-auth/          # Microsserviço de autenticação (OAuth2 + JWT)
├── prenatal-agenda/        # Microsserviço de agendamento de consultas
├── prenatal-prontuario/    # Microsserviço de prontuário eletrônico
├── prenatal-documento/     # Microsserviço de documentos e vacinas
├── prenatal-alertas/       # Motor de alertas e notificações por e-mail
├── docker-compose.yml      # Orquestração unificada dos serviços
├── docker/
│   └── postgres-init/
│       └── 01-create-schemas.sql   # Criação dos schemas
├── collection/             # Collections e environments do Postman
├── .env.example            # Variáveis de ambiente de exemplo
└── DOCKER.md               # Instruções detalhadas do Docker
```

---

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
│   │   └── gateways/        (NotificationOrchestratorGateway)
│   │   └── repositories/    (ProntuarioRepository, AgendaRepository, DocumentoRepository)
│   └── infrastructure/
│       ├── gateways/        (DirectSmtpNotificationGatewayImpl)
│       ├── persistence/     (ProntuarioRepositoryImpl, AgendaRepositoryImpl, DocumentoRepositoryImpl)
│       └── scheduler/       (PrenatalAnalysisScheduler)
├── Dockerfile
└── pom.xml
```

---

## 📡 Diagrama de Arquitetura

### Visão Geral dos Serviços

```mermaid
flowchart TB
    subgraph Infraestrutura
        PG[(PostgreSQL<br/>prenatal_digital_sus)]
        S3[("LocalStack S3<br/>prenatal-documents")]
    end

    subgraph Serviços
        AUTH[prenatal-auth<br/>:8079<br/>OAuth2 / JWT]
        AGENDA[prenatal-agenda<br/>:8080]
        PRONT[prenatal-prontuario<br/>:8082]
        DOC[prenatal-documento<br/>:8081]
        ALERTAS[prenatal-alertas<br/>:8084]
    end

    subgraph Cliente
        USER[Cliente / Postman]
    end

    USER -->|POST /oauth2/token| AUTH
    USER -->|Bearer JWT| AGENDA
    USER -->|Bearer JWT| PRONT
    USER -->|Bearer JWT| DOC

    AUTH -->|valida JWT| AGENDA
    AUTH -->|valida JWT| PRONT
    AUTH -->|valida JWT| DOC
    AUTH --> PG

    AGENDA --> PG
    PRONT --> PG
    DOC --> PG
    DOC --> S3
    ALERTAS --> PG
    ALERTAS -->|SMTP| EMAIL[📧 E-mail]
```

### Fluxo de Comunicação e Dependências

```mermaid
flowchart LR
    subgraph Docker["prenatal-network"]
        PG[(PostgreSQL<br/>:5432)]
        LS[(LocalStack<br/>:4566)]
        AUTH[Auth<br/>:8079]
        AGENDA[Agenda<br/>:8080]
        PRONT[Prontuário<br/>:8082]
        DOC[Documento<br/>:8081]
        ALERTAS[Alertas<br/>:8084]
    end

    PG --> AUTH
    PG --> AGENDA
    PG --> PRONT
    PG --> DOC
    PG --> ALERTAS

    LS --> DOC

    AUTH -->|JWKS / Token| AGENDA
    AUTH -->|JWKS / Token| PRONT
    AUTH -->|JWKS / Token| DOC

    PRONT --> ALERTAS
    AGENDA --> ALERTAS
    DOC --> ALERTAS
```

### Fluxo de Autenticação

```mermaid
sequenceDiagram
    participant C as Cliente
    participant A as prenatal-auth
    participant S as Serviço (Agenda/Prontuário/Documento)

    C->>A: POST /oauth2/token (grant_type=password)
    A->>A: Valida credenciais
    A->>C: access_token (JWT)

    C->>S: GET/POST ... (Authorization: Bearer JWT)
    S->>A: GET /oauth2/jwks (valida assinatura)
    A->>S: Chaves públicas
    S->>S: Valida JWT
    S->>C: Resposta
```

---

## 📊 Fluxogramas do Projeto

### Fluxograma 1: Onboarding e Cadastro Inicial

```mermaid
flowchart TD
    START([Início]) --> A1[Criar usuário<br/>POST /v1/usuarios]
    A1 --> A2{Usuário<br/>criado?}
    A2 -->|Não| ERR1[Erro de validação]
    A2 -->|Sim| A3[Obter token JWT<br/>POST /oauth2/token]
    A3 --> A4{Token<br/>válido?}
    A4 -->|Não| ERR2[Credenciais inválidas]
    A4 -->|Sim| A5[Criar prontuário<br/>POST /api/v1/prontuarios]
    A5 --> A6{Prontuário<br/>criado?}
    A6 -->|Não| ERR3[Erro ao criar prontuário]
    A6 -->|Sim| A7[Médico cadastra agenda<br/>POST /api/agendas/medico]
    A7 --> END([Cadastro concluído])
    ERR1 --> END
    ERR2 --> END
    ERR3 --> END
```

### Fluxograma 2: Agendamento de Consulta

```mermaid
flowchart TD
    START([Solicitar agendamento]) --> B1[Consultar disponibilidade<br/>GET /api/disponibilidade]
    B1 --> B2[Enviar requisição<br/>POST /api/consultas/agendar]
    B2 --> B3{CPF da gestante<br/>válido?}
    B3 -->|Não| ERR1[Erro: Gestante não encontrada]
    B3 -->|Sim| B4{Médico<br/>encontrado?}
    B4 -->|Não| ERR2[Erro: Médico não encontrado]
    B4 -->|Sim| B5{Médico atende<br/>no dia da semana?}
    B5 -->|Não| ERR3[Erro: Médico não atende neste dia]
    B5 -->|Sim| B6{Horário dentro<br/>do expediente?}
    B6 -->|Não| ERR4[Erro: Horário fora do expediente]
    B6 -->|Sim| B7{Horário<br/>disponível?}
    B7 -->|Não| ERR5[Erro: Horário já ocupado]
    B7 -->|Sim| B8[Consulta agendada com sucesso]
    B8 --> END([Sucesso])
    ERR1 --> END
    ERR2 --> END
    ERR3 --> END
    ERR4 --> END
    ERR5 --> END
```

### Fluxograma 3: Upload e Gestão de Documentos

```mermaid
flowchart TD
    subgraph Upload["Upload de Exame"]
        U1([Início]) --> U2[Upload documento<br/>POST /api/prenatal-records/cpf/documents]
        U2 --> U3{Arquivo<br/>válido?}
        U3 -->|Não| U4[Erro: arquivo inválido]
        U3 -->|Sim| U5[Salvar no S3]
        U5 --> U6[Registrar no banco]
        U6 --> U7([Documento registrado])
    end

    subgraph Vacina["Registro de Vacina"]
        V1([Início]) --> V2[Registrar vacina<br/>POST /api/prenatal-records/cpf/vacinas]
        V2 --> V3{Dados<br/>válidos?}
        V3 -->|Não| V4[Erro de validação]
        V3 -->|Sim| V5[Salvar no banco]
        V5 --> V6([Vacina registrada])
    end

    subgraph Download["Download"]
        D1([Solicitar]) --> D2[GET /api/documents/id/download]
        D2 --> D3[Buscar no S3]
        D3 --> D4([Arquivo retornado])
    end
```

### Fluxograma 4: Motor de Alertas (prenatal-alertas)

```mermaid
flowchart TD
    START([Scheduler dispara]) --> A1[Buscar gestantes ativas<br/>no prontuário]
    A1 --> A2{Tem<br/>gestantes?}
    A2 -->|Não| END([Fim])
    A2 -->|Sim| A3[Para cada gestante]
    A3 --> A4[Buscar exames por CPF]
    A4 --> A5[Buscar vacinas por CPF]
    A5 --> A6[Buscar consultas por CPF]
    A6 --> A7[Aplicar regras de negócio]

    A7 --> R1[Verificar ultrassom morfológico<br/>>= 20 semanas]
    R1 --> R2[Verificar translucência nucal<br/>12-14 semanas]
    R2 --> R3[Verificar curva glicêmica<br/>>= 28 semanas]
    R3 --> R4[Verificar vacina antitetânica]
    R4 --> R5[Verificar consulta agendada]
    R5 --> R6[Verificar gestante de risco<br/>+ exame crítico pendente]

    R6 --> A8{Tem<br/>alertas?}
    A8 -->|Sim| A9[Enviar notificações<br/>por e-mail]
    A9 --> A10{Próxima<br/>gestante?}
    A8 -->|Não| A10
    A10 -->|Sim| A3
    A10 -->|Não| END
```

### Fluxograma 5: Ciclo Completo do Pré-Natal

```mermaid
flowchart LR
    subgraph Cadastro["1. Cadastro"]
        C1[Usuário] --> C2[Prontuário]
        C2 --> C3[Agenda médico]
    end

    subgraph Consultas["2. Consultas"]
        CO1[Disponibilidade] --> CO2[Agendar]
        CO2 --> CO3[Realizar consulta]
        CO3 --> CO4{Cancelar?}
        CO4 -->|Sim| CO5[Cancelar consulta]
        CO4 -->|Não| CO3
    end

    subgraph Documentos["3. Documentos"]
        D1[Upload exame] --> D2[Registrar vacina]
    end

    subgraph Alertas["4. Monitoramento"]
        AL1[Scheduler] --> AL2[Analisar gestações]
        AL2 --> AL3[Regras clínicas]
        AL3 --> AL4[Notificar gestante/médico]
    end

    Cadastro --> Consultas
    Consultas --> Documentos
    Documentos --> Alertas
    Alertas -.->|Retroalimenta| Consultas
```

### Fluxograma 6: Cancelamento de Consulta

```mermaid
flowchart TD
    START([Solicitar cancelamento]) --> C1[DELETE /api/consultas/id/cancelar]
    C1 --> C2{Consulta<br/>existe?}
    C2 -->|Não| ERR1[Consulta não encontrada]
    C2 -->|Sim| C3{Status =<br/>AGENDADA?}
    C3 -->|Não| ERR2[Já cancelada ou realizada]
    C3 -->|Sim| C4[Informar motivo do cancelamento]
    C4 --> C5[Atualizar status = CANCELADA]
    C5 --> C6[Registrar data de cancelamento]
    C6 --> END([Cancelamento concluído])
    ERR1 --> END
    ERR2 --> END
```

---

## 🗄️ Diagrama do Banco de Dados

### Visão Geral da Arquitetura de Dados

```mermaid
flowchart TB
    subgraph Infra["Infraestrutura de Dados"]
        PG[(PostgreSQL 16<br/>prenatal_digital_sus)]
        S3[("LocalStack S3<br/>Bucket: prenatal-documents")]
    end

    subgraph Auth["Schema: auth"]
        users[(auth.users)]
        roles[(auth.roles)]
        user_role[(auth.user_role)]
    end

    subgraph Agenda["Schema: agenda"]
        agenda_medico[(agenda.agenda_medico)]
        agenda_dias[(agenda.agenda_dias_atendimento)]
        consulta[(agenda.consulta)]
    end

    subgraph Pront["Schema: prontuario"]
        prontuario[(prontuario.prontuario)]
        fatores[(prontuario.prontuario_fatores_risco)]
        historico[(prontuario.prontuario_historico)]
    end

    subgraph Doc["Schema: documento"]
        doc_medico[(documento.documento_medico)]
        vacina[(documento.vacina)]
    end

    PG --> Auth
    PG --> Agenda
    PG --> Pront
    PG --> Doc
    Doc -->|caminho_armazenamento| S3
```

### Diagrama Entidade-Relacionamento

```mermaid
erDiagram
    auth_users ||--o{ auth_user_role : "possui"
    auth_roles ||--o{ auth_user_role : "atribuído"
    auth_users {
        bigint id PK
        varchar name
        varchar email UK
        varchar login UK
        varchar cpf UK
        varchar password
        varchar street
        bigint number
        varchar city
        varchar state
        varchar zip_code
    }
    auth_roles {
        bigint id PK
        varchar authority UK
    }
    auth_user_role {
        bigint user_id PK,FK
        bigint role_id PK,FK
    }

    agenda_medico ||--o{ agenda_dias_atendimento : "dias_semana"
    agenda_medico {
        bigint id PK
        bigint medico_id
        bigint unidade_id
        time horario_inicio
        time horario_fim
        int duracao_consulta_minutos
    }
    agenda_dias_atendimento {
        bigint agenda_id PK,FK
        varchar dia_semana PK
    }
    agenda_consulta {
        bigint id PK
        bigint gestante_id
        varchar cpf
        bigint medico_id
        bigint unidade_id
        date data
        time horario
        varchar status
    }

    prontuario ||--o{ prontuario_fatores_risco : "fatores"
    prontuario ||--o{ prontuario_historico : "historico"
    prontuario {
        uuid id PK
        varchar cpf
        varchar nome_completo
        int idade_gestacional_semanas
        varchar email_paciente
        varchar medico_email
    }
    prontuario_fatores_risco {
        uuid prontuario_id PK,FK
        varchar fator_risco PK
    }
    prontuario_historico {
        uuid id PK
        uuid prontuario_id FK
        timestamp data
        text alteracao
    }

    documento_medico {
        uuid id PK
        varchar cpf
        varchar tipo_documento
        varchar tipo_exame
        varchar caminho_armazenamento
        boolean ativo
    }
    documento_vacina {
        uuid id PK
        varchar cpf
        varchar tipo_vacina
        date data_aplicacao
    }
```

### Tabelas por Schema

```mermaid
flowchart LR
    subgraph auth["🔐 auth"]
        direction TB
        U[auth.users]
        R[auth.roles]
        UR[auth.user_role]
    end

    subgraph agenda["📅 agenda"]
        direction TB
        AM[agenda_medico]
        AD[agenda_dias_atendimento]
        C[consulta]
    end

    subgraph prontuario["📖 prontuario"]
        direction TB
        P[prontuario]
        PF[prontuario_fatores_risco]
        PH[prontuario_historico]
    end

    subgraph documento["📄 documento"]
        direction TB
        DM[documento_medico]
        V[vacina]
    end
```

### Relacionamentos e Chaves de Ligação

```mermaid
flowchart TB
    subgraph auth_schema["auth"]
        auth_users(auth.users)
    end

    subgraph agenda_schema["agenda"]
        tbl_agenda_medico(agenda_medico)
        tbl_consulta(consulta)
    end

    subgraph pront_schema["prontuario"]
        tbl_prontuario(prontuario)
    end

    subgraph doc_schema["documento"]
        tbl_doc(documento_medico)
        tbl_vacina(vacina)
    end

    auth_users -.->|medico_id| tbl_agenda_medico
    auth_users -.->|gestante_id| tbl_consulta
    tbl_consulta -.->|cpf| tbl_prontuario
    tbl_consulta -.->|cpf| tbl_doc
    tbl_consulta -.->|cpf| tbl_vacina
```

### Schemas e Serviços

```mermaid
flowchart TB
    subgraph DB["PostgreSQL - prenatal_digital_sus"]
        direction TB
        subgraph auth_schema["Schema: auth"]
            users[(users)]
            roles[(roles)]
            user_role[(user_role)]
        end
        subgraph prontuario_schema["Schema: prontuario"]
            prontuario[(prontuario)]
            fatores[(prontuario_fatores_risco)]
            historico[(prontuario_historico)]
        end
        subgraph agenda_schema["Schema: agenda"]
            agenda_medico[(agenda_medico)]
            agenda_dias[(agenda_dias_atendimento)]
            consulta[(consulta)]
        end
        subgraph documento_schema["Schema: documento"]
            documento_medico[(documento_medico)]
            vacina[(vacina)]
        end
    end

    prenatal_auth[prenatal-auth] --> auth_schema
    prenatal_agenda[prenatal-agenda] --> agenda_schema
    prenatal_prontuario[prenatal-prontuario] --> prontuario_schema
    prenatal_documento[prenatal-documento] --> documento_schema
    prenatal_alertas[prenatal-alertas] --> auth_schema
    prenatal_alertas --> prontuario_schema
    prenatal_alertas --> agenda_schema
    prenatal_alertas --> documento_schema
```

---

## 🗄️ Banco de Dados

- **PostgreSQL 16** com um único banco `prenatal_digital_sus`
- **Schemas isolados:** `auth`, `prontuario`, `agenda`, `documento`
- **Flyway** em cada serviço para migrações
- Script de init em `docker/postgres-init/01-create-schemas.sql`

---

## 📋 Requisitos

- Java 21
- Maven 3.9+
- Docker e Docker Compose
- Postman (para testes de API)

---

## ▶️ Como Rodar o Projeto

### Via Docker Compose (recomendado)

Na raiz do projeto:

```bash
# Subir todos os serviços
docker compose up --build -d

# Acompanhar logs
docker compose logs -f

# Parar
docker compose down
```

### Variáveis de ambiente

As variáveis `SMTP_EMAIL` e `SMTP_PASSWORD` são lidas do ambiente da máquina ou do arquivo `.env`. Para configurar:

```bash
cp .env.example .env
# Edite .env com suas credenciais SMTP (opcional)
```

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

## 📚 Documentação da API

Cada serviço expõe **Swagger UI** em:

- Auth: http://localhost:8079/swagger-ui.html
- Agenda: http://localhost:8080/swagger-ui.html
- Documento: http://localhost:8081/swagger-ui.html
- Prontuário: http://localhost:8082/swagger-ui.html
- Alertas: http://localhost:8084/swagger-ui.html

---

## 📊 Cobertura de Código

Gerada com **JaCoCo** em cada módulo:

```bash
cd prenatal-auth   # ou agenda, prontuario, documento, alertas
mvn clean test
mvn jacoco:report
```

Relatório em: `target/site/jacoco/index.html`

---

## 🧪 Collection e Environment Postman

- **Collection:** `collection/Prenatal-Digital-SUS-API-Unificada.postman_collection.json`
- **Environment:** `collection/Prenatal-Digital-SUS-Local.postman_environment.json`

Importe ambos no Postman e configure o token JWT obtido em `POST /oauth2/token` do prenatal-auth.

---

## 🔒 Fluxo de Autenticação

1. Obter token: `POST http://localhost:8079/oauth2/token` com `grant_type=password`, `username`, `password`, `client_id`, `client_secret`
2. Usar o `access_token` no header: `Authorization: Bearer {token}`
3. Acessar os demais serviços (agenda, prontuário, documento) com o mesmo token
