# Prenatal Document Service

Serviço de gerenciamento de documentos médicos (exames e ultrassons em PDF) para a plataforma de pré-natal do SUS.

## 🏗️ Arquitetura

O projeto segue os princípios de **Clean Architecture** (Arquitetura Hexagonal), com separação clara de responsabilidades:

- **Domain**: Modelos de negócio, interfaces (ports) e regras de domínio
- **Application**: Casos de uso, serviços de aplicação, DTOs e mappers
- **Infrastructure**: Implementações técnicas (controllers, gateways, persistência)
- **Config**: Configurações do Spring (S3, Security, etc.)

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3.5.9**
- **PostgreSQL** (banco de dados)
- **AWS S3 SDK** (armazenamento de arquivos)
- **LocalStack** (emulação do S3 para desenvolvimento)
- **Flyway** (migrações de banco de dados)
- **Spring Security + OAuth2 Resource Server** (autenticação JWT)

## 📋 Pré-requisitos

- Java 21
- Maven 3.6+
- Docker e Docker Compose

## 🔧 Configuração e Execução

### 1. Iniciar serviços com Docker Compose

Certifique-se de que o **outro docker-compose** (com o PostgreSQL compartilhado) já está em execução. Depois, neste projeto:

```bash
docker-compose up -d
```

Isso iniciará apenas os serviços de armazenamento (o banco de dados é compartilhado por outro docker-compose):
- **LocalStack** na porta `4566` (S3)
- **s3-init**: criação do bucket `prenatal-documents` no S3 (vinculado aos documentos do prontuário)

### 2. Configurar variáveis de ambiente (opcional)

As configurações padrão estão em `application.properties`. Para desenvolvimento local, você pode criar um `application-local.properties`:

```properties
# Database (compartilhado – use a URL do outro docker-compose)
spring.datasource.url=jdbc:postgresql://localhost:5432/prenatal_digital_sus
spring.datasource.username=postgres
spring.datasource.password=password

# AWS S3 (LocalStack)
aws.s3.endpoint-url=http://localhost:4566
aws.s3.region=us-east-1
aws.s3.access-key=test
aws.s3.secret-key=test
aws.s3.bucket-name=prenatal-documents

# Security
security.jwt.jwks-uri=http://localhost:8079/oauth2/jwks
```

### 3. Executar a aplicação

```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8081`

## 📡 Endpoints

### Upload de Documento
```
POST /api/prenatal-records/{id}/documents
Content-Type: multipart/form-data

Body:
- file: arquivo PDF
- documentType: EXAM ou ULTRASOUND

Headers:
Authorization: Bearer {JWT_TOKEN}
```

**Roles permitidas:** `PATIENT`, `NURSE`, `DOCTOR`

### Listar Documentos Ativos
```
GET /api/prenatal-records/{id}/documents

Headers:
Authorization: Bearer {JWT_TOKEN}
```

**Roles permitidas:** `PATIENT`, `NURSE`, `DOCTOR`

### Download de Documento
```
GET /api/documents/{id}/download

Headers:
Authorization: Bearer {JWT_TOKEN}
```

**Roles permitidas:** `PATIENT`, `NURSE`, `DOCTOR`

### Inativar Documento (Soft Delete)
```
PATCH /api/documents/{id}/inactivate

Headers:
Authorization: Bearer {JWT_TOKEN}
```

**Roles permitidas:** `NURSE`, `DOCTOR`

### Excluir Permanentemente (Hard Delete)
```
DELETE /api/documents/{id}/permanent

Headers:
Authorization: Bearer {JWT_TOKEN}
```

**Roles permitidas:** `NURSE`, `DOCTOR`

### Solicitar Exclusão (para pacientes)
```
POST /api/documents/{id}/request-delete

Headers:
Authorization: Bearer {JWT_TOKEN}
```

**Roles permitidas:** `PATIENT`

## 🔐 Autenticação

O serviço utiliza **JWT** (JSON Web Token) com OAuth2 Resource Server. O token deve ser enviado no header:

```
Authorization: Bearer {token}
```

### Estrutura do Token JWT

O token deve conter os seguintes claims:

- `iss`: `http://localhost:8079`
- `sub`: client_id
- `aud`: client_id
- `exp`: timestamp de expiração
- `iat`: timestamp de emissão
- `scope`: `read write`
- `authorities`: array de roles (ex: `["ROLE_PATIENT"]`)
- `username`: email do usuário
- `user_id`: ID numérico do usuário
- `cpf`: CPF do usuário

A chave pública para validação está disponível em: `http://localhost:8079/oauth2/jwks`

## 📁 Estrutura do Projeto

```
src/main/java/com/hackathon/sus/prenatal_documento/
├── application/
│   ├── dtos/
│   │   ├── requests/
│   │   └── responses/
│   ├── mappers/
│   └── services/
├── config/
├── domain/
│   ├── enums/
│   ├── exceptions/
│   ├── models/
│   ├── ports/
│   │   ├── inbound/
│   │   └── outbound/
│   └── repositories/
└── infrastructure/
    ├── controllers/
    ├── exceptions/
    ├── gateways/
    └── persistence/
        ├── entities/
        └── repositories/
```

## 🗄️ Banco de Dados

O banco de dados armazena apenas **metadados** dos documentos:
- ID do documento
- ID do registro pré-natal
- Nome do arquivo
- Nome original
- Tipo de conteúdo (MIME type)
- Tamanho do arquivo
- Tipo de documento (EXAM ou ULTRASOUND)
- Caminho de armazenamento no S3 (`storagePath`)
- Status ativo/inativo
- Timestamps (criação, atualização, exclusão)

Os arquivos PDF são armazenados no **S3** (LocalStack em desenvolvimento).

## 🧪 Testes

```bash
./mvnw test
```

## 📝 Migrações

As migrações do Flyway estão em `src/main/resources/db/migration/`. A primeira migração cria a tabela `medical_documents`.

## 🔍 Validações

- Apenas arquivos **PDF** são aceitos
- Tamanho máximo: **10MB**
- Tipos de documento: `EXAM` ou `ULTRASOUND`

## 🐳 Docker Compose

O `docker-compose.yml` deste projeto inclui **apenas**:

- **LocalStack**: Emulação do AWS S3 para armazenar arquivos vinculados ao prontuário
- **s3-init**: Serviço que cria o bucket `prenatal-documents` assim que o LocalStack fica saudável

O **banco de dados** é compartilhado e fornecido por outro docker-compose (não faz parte deste arquivo).

Para parar os serviços:

```bash
docker-compose down
```

Para remover volumes (dados):

```bash
docker-compose down -v
```

## 📚 Dependências Principais

- `spring-boot-starter-web`: API REST
- `spring-boot-starter-data-jpa`: Persistência
- `spring-boot-starter-security`: Segurança
- `spring-boot-starter-oauth2-resource-server`: OAuth2/JWT
- `software.amazon.awssdk:s3`: Cliente S3
- `flyway-core`: Migrações de banco

## 🚨 Troubleshooting

### Erro ao conectar no LocalStack
Certifique-se de que o LocalStack está rodando:
```bash
docker ps | grep localstack
```

### Erro ao criar bucket
O bucket `prenatal-documents` é criado pelo serviço `s3-init` no Docker Compose (após o LocalStack subir). A aplicação também tenta criá-lo na subida. Se falhar, verifique: `docker-compose logs s3-init` e os logs da aplicação.

### Erro de autenticação
Verifique se o token JWT está válido e contém as roles necessárias (`ROLE_PATIENT`, `ROLE_NURSE` ou `ROLE_DOCTOR`).

## 📄 Licença

Este projeto faz parte do hackathon SUS - Plataforma de Pré-natal Digital.
