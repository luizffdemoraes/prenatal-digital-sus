# Collection - Prenatal Document Service

Esta pasta contém a collection do Postman para testar todos os endpoints do serviço de documentos.

## 📦 Arquivos

- `Prenatal-Document-Service.postman_collection.json` - Collection do Postman com todos os endpoints
- `Local-Development.postman_environment.json` - Environment de exemplo para desenvolvimento local
- `Get-Token.postman_collection.json` - Collection auxiliar para obter token JWT automaticamente

## 🚀 Como Usar

### 1. Importar no Postman

1. Abra o Postman
2. Clique em **Import**
3. Selecione os arquivos:
   - `Prenatal-Document-Service.postman_collection.json` (collection principal)
   - `Local-Development.postman_environment.json` (environment - opcional)
   - `Get-Token.postman_collection.json` (helper para obter token - opcional)
4. As collections e o environment serão importados

### 2. Configurar Variáveis de Ambiente

A collection possui variáveis que podem ser configuradas:

- `baseUrl`: URL base da API (padrão: `http://localhost:8080`)
- `jwtToken`: Token JWT para autenticação
- `documentId`: ID do documento (preenchido automaticamente após upload)
- `patientCpf`: CPF da paciente - apenas números (ex.: `12345678901`)

#### Opção 1: Usar variáveis da collection

As variáveis já estão definidas na collection. Você pode editá-las:
1. Clique com botão direito na collection
2. Selecione **Edit**
3. Vá na aba **Variables**
4. Atualize os valores conforme necessário

#### Opção 2: Usar o Environment Importado

1. Se você importou o `Local-Development.postman_environment.json`, ele já estará disponível
2. Selecione o environment **"Local Development"** no dropdown superior direito
3. Atualize as variáveis conforme necessário:
   - `jwtToken`: (seu token JWT - pode ser preenchido automaticamente usando a collection "Get Token Helper")
   - `username` e `password`: Credenciais para obter o token
   - `clientId` e `clientSecret`: Credenciais do cliente OAuth2
   - `basicAuth`: Base64 de `client_id:client_secret` (pode ser gerado automaticamente)

#### Opção 3: Criar um Environment Manualmente

1. Clique em **Environments** no menu lateral
2. Crie um novo environment (ex: "Local Development")
3. Adicione as variáveis:
   - `baseUrl`: `http://localhost:8080`
   - `jwtToken`: (seu token JWT)
   - `documentId`: (deixe vazio, será preenchido automaticamente)
   - `prenatalRecordId`: `1`
4. Selecione o environment criado no dropdown superior direito

### 3. Obter Token JWT

Antes de testar os endpoints, você precisa obter um token JWT do serviço de autenticação.

#### Opção 1: Usar a Collection Helper (Recomendado)

1. Se você importou a collection `Get-Token.postman_collection.json`:
   - Abra a requisição **"Get JWT Token"**
   - Configure as variáveis no environment:
     - `username`: Email do usuário
     - `password`: Senha do usuário
     - `basicAuth`: Base64 de `client_id:client_secret` (ex: `bXljbGllbnRpZDpteWNsaWVudHNlY3JldA==`)
   - Execute a requisição
   - O token será salvo automaticamente na variável `jwtToken` do environment

#### Opção 2: Obter Token Manualmente

Faça uma requisição para o serviço de autenticação:

```bash
POST http://localhost:8079/oauth2/token
Content-Type: application/x-www-form-urlencoded
Authorization: Basic {base64(client_id:client_secret)}

grant_type=password&username={email}&password={senha}
```

Copie o `access_token` da resposta e cole na variável `jwtToken` do environment.

### 4. Testar os Endpoints

#### Upload de Documento

1. Abra a requisição **Upload Document** na pasta **Documents**
2. Na aba **Body**, selecione um arquivo PDF no campo `file`
3. Defina o `documentType` como `EXAM` ou `ULTRASOUND`
4. Atualize o `:cpf` na URL com o CPF da paciente (apenas números)
5. Clique em **Send**

**Nota:** Após o upload bem-sucedido, o `documentId` será salvo automaticamente na variável da collection.

#### Listar Documentos

1. Abra a requisição **List Active Documents**
2. Atualize o `:id` na URL com o ID do registro pré-natal
3. Clique em **Send**

#### Download de Documento

1. Abra a requisição **Download Document**
2. O `:id` já está configurado para usar a variável `{{documentId}}`
3. Clique em **Send**
4. O arquivo PDF será baixado

#### Inativar Documento

1. Abra a requisição **Inactivate Document**
2. Certifique-se de que o token JWT tem role `NURSE` ou `DOCTOR`
3. Clique em **Send**

#### Excluir Permanentemente

1. Abra a requisição **Delete Document Permanently**
2. Certifique-se de que o token JWT tem role `NURSE` ou `DOCTOR`
3. **⚠️ ATENÇÃO:** Esta ação é irreversível!
4. Clique em **Send**

#### Solicitar Exclusão

1. Abra a requisição **Request Document Deletion**
2. Certifique-se de que o token JWT tem role `PATIENT`
3. Clique em **Send**

## 📋 Estrutura da Collection

```
Prenatal Document Service
├── Documents
│   ├── Upload Document
│   ├── List Active Documents
│   ├── Download Document
│   ├── Inactivate Document
│   ├── Delete Document Permanently
│   └── Request Document Deletion
└── Health Check
    └── Health
```

## 🔐 Autenticação

Todos os endpoints (exceto Health Check) requerem autenticação via JWT:

```
Authorization: Bearer {jwtToken}
```

### Roles Necessárias

- **PATIENT**: Pode fazer upload, listar, baixar e solicitar exclusão
- **NURSE**: Pode fazer upload, listar, baixar, inativar e excluir permanentemente
- **DOCTOR**: Pode fazer upload, listar, baixar, inativar e excluir permanentemente

## 📝 Exemplos de Respostas

### Upload Document (201 Created)

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "patientCpf": "12345678901",
  "fileName": "abc123.pdf",
  "originalFileName": "exame-sangue.pdf",
  "contentType": "application/pdf",
  "fileSize": 245678,
  "documentType": "EXAM",
  "active": true,
  "createdAt": "2026-01-28T10:30:00",
  "updatedAt": null
}
```

### List Active Documents (200 OK)

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "patientCpf": "12345678901",
    "fileName": "abc123.pdf",
    "originalFileName": "exame-sangue.pdf",
    "contentType": "application/pdf",
    "fileSize": 245678,
    "documentType": "EXAM",
    "active": true,
    "createdAt": "2026-01-28T10:30:00",
    "updatedAt": null
  }
]
```

### Erro de Validação (400 Bad Request)

```json
{
  "error": "Erro de validação",
  "fields": {
    "documentType": "Tipo de documento é obrigatório"
  }
}
```

### Erro de Autenticação (403 Forbidden)

```json
{
  "error": "Acesso negado",
  "message": "Você não tem permissão para realizar esta ação"
}
```

## 🧪 Testes Automatizados

A collection inclui alguns testes automáticos:

- **Upload Document**: Salva automaticamente o `documentId` e `prenatalRecordId` nas variáveis após upload bem-sucedido

Você pode adicionar mais testes clicando na aba **Tests** de cada requisição.

## 🔧 Troubleshooting

### Erro 401 Unauthorized
- Verifique se o token JWT está válido e não expirou
- Certifique-se de que o token está na variável `jwtToken`

### Erro 403 Forbidden
- Verifique se o token JWT contém a role necessária (`ROLE_PATIENT`, `ROLE_NURSE` ou `ROLE_DOCTOR`)

### Erro 400 Bad Request no Upload
- Verifique se o arquivo é PDF
- Verifique se o tamanho não excede 10MB
- Verifique se o `documentType` é `EXAM` ou `ULTRASOUND`

### Erro 404 Not Found
- Verifique se o `documentId` ou `patientCpf` estão corretos
- Verifique se a aplicação está rodando em `http://localhost:8080`

## 📚 Referências

- [Documentação da API](../README.md)
- [Postman Documentation](https://learning.postman.com/docs/getting-started/introduction/)
