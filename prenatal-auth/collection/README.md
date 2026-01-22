# Collection Postman - Prenatal Auth API

Esta pasta contém a collection do Postman para testar a API de autenticação do sistema Prenatal Digital SUS.

## 📁 Arquivos

- `Prenatal-Auth.postman_collection.json` - Collection principal com todos os endpoints
- `Prenatal-Auth-Local.postman_environment.json` - Environment para ambiente local
- `README.md` - Este arquivo com instruções de uso

## 🚀 Como usar

### 1. Importar a Collection e Environment no Postman

1. Abra o Postman
2. Clique em **Import** (canto superior esquerdo)
3. Arraste os arquivos `Prenatal-Auth.postman_collection.json` e `Prenatal-Auth-Local.postman_environment.json` para a janela de importação
4. Clique em **Import**

### 2. Configurar o Environment

1. No canto superior direito do Postman, selecione o environment **"Prenatal Auth - Local"**
2. Configure as variáveis conforme necessário:
   - `base_url`: URL base da API (padrão: `http://localhost:8079`)
   - `client_id`: ID do cliente OAuth2 (padrão: `myclientid`)
   - `client_secret`: Secret do cliente OAuth2 (padrão: `myclientsecret`)
   - `username`: Login do usuário para autenticação
   - `password`: Senha do usuário para autenticação
   - `user_id`: ID do usuário para testes (será preenchido após criar um usuário)

### 3. Configurar Basic Auth para OAuth2

O endpoint de autenticação OAuth2 requer Basic Authentication usando `client_id:client_secret`.

**Opção 1: Configurar manualmente**
1. Vá em **Environments** → **Prenatal Auth - Local**
2. Para `basic_auth_token`, você pode usar uma ferramenta online para converter `myclientid:myclientsecret` para Base64, ou usar o script abaixo no Pre-request Script do endpoint "Obter Token OAuth2"

**Opção 2: Usar Pre-request Script (Recomendado)**
Adicione este script no Pre-request Script do endpoint "Obter Token OAuth2":

```javascript
const clientId = pm.environment.get("client_id");
const clientSecret = pm.environment.get("client_secret");
const basicAuth = btoa(clientId + ":" + clientSecret);
pm.environment.set("basic_auth_token", basicAuth);
```

### 4. Fluxo de Teste Recomendado

#### Passo 1: Criar um Usuário
1. Execute a requisição **"Criar Usuário"** na pasta **Usuários**
2. Copie o `id` retornado na resposta
3. Atualize a variável `user_id` no environment com esse valor

#### Passo 2: Obter Token de Acesso
1. Configure `username` e `password` no environment com as credenciais do usuário criado
2. Execute a requisição **"Obter Token OAuth2"** na pasta **Autenticação**
3. O token será automaticamente salvo na variável `access_token` (script de teste automático)

#### Passo 3: Testar Endpoints Protegidos
Agora você pode testar os outros endpoints:
- **Buscar Usuário por ID**: Busca um usuário específico
- **Atualizar Usuário**: Atualiza os dados do usuário
- **Atualizar Senha**: Altera a senha do usuário

## 📋 Endpoints Disponíveis

### Autenticação
- **POST** `/oauth2/token` - Obter token OAuth2

### Usuários
- **POST** `/v1/usuarios` - Criar novo usuário
- **GET** `/v1/usuarios/{id}` - Buscar usuário por ID
- **PUT** `/v1/usuarios/{id}` - Atualizar usuário
- **PATCH** `/v1/usuarios/{id}/senha` - Atualizar senha

## 🔐 Perfis Disponíveis

Ao criar ou atualizar um usuário, você pode usar os seguintes perfis:
- `ROLE_DOCTOR` - Médico
- `ROLE_NURSE` - Enfermeiro
- `ROLE_PATIENT` - Paciente

## 📝 Exemplo de Request Body

### Criar Usuário
```json
{
    "nome": "João Silva",
    "email": "joao.silva@example.com",
    "login": "joao.silva",
    "senha": "senha123",
    "perfil": "ROLE_PATIENT",
    "endereco": {
        "rua": "Rua das Flores",
        "numero": 123,
        "cidade": "São Paulo",
        "estado": "SP",
        "cep": "01234-567"
    }
}
```

### Atualizar Senha
```json
{
    "senha": "novaSenhaSegura123"
}
```

## ⚙️ Configurações da Aplicação

- **Porta padrão**: 8079
- **Client ID padrão**: myclientid
- **Client Secret padrão**: myclientsecret
- **Issuer**: http://localhost:8079

## 🔧 Troubleshooting

### Erro 401 Unauthorized
- Verifique se o token foi obtido corretamente
- Confirme que o `access_token` está sendo enviado no header Authorization
- Verifique se o token não expirou (duração padrão: 86400 segundos = 24 horas)

### Erro 400 Bad Request
- Verifique se todos os campos obrigatórios estão preenchidos
- Confirme que o formato do JSON está correto
- Verifique se o perfil é um dos valores válidos: ROLE_DOCTOR, ROLE_NURSE, ROLE_PATIENT

### Erro 404 Not Found
- Verifique se a aplicação está rodando na porta 8079
- Confirme que o `base_url` está correto no environment

## 📚 Recursos Adicionais

- Documentação Spring OAuth2: https://docs.spring.io/spring-authorization-server/reference/
- Postman Learning Center: https://learning.postman.com/
