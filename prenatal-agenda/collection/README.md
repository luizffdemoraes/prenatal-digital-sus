# Prenatal Agenda API - Collection Postman

Esta collection contém todas as rotas da API de Agenda e Consultas do sistema Prenatal Digital SUS.

## 📋 Pré-requisitos

1. **Postman instalado** (versão 8.0 ou superior)
2. **Serviço de autenticação rodando** (`prenatal-auth` na porta 8079)
3. **Serviço de agenda rodando** (`prenatal-agenda` na porta 8080)
4. **Token JWT válido** obtido do serviço de autenticação

## 🚀 Como usar

### 1. Importar a Collection

1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo `Prenatal-Agenda.postman_collection.json`
4. Selecione o arquivo `Prenatal-Agenda-Local.postman_environment.json`

### 2. Configurar o Ambiente

1. Selecione o ambiente **"Prenatal Agenda - Local"** no dropdown superior direito
2. Configure as variáveis conforme necessário:
   - `base_url`: URL base da API (padrão: `http://localhost:8080`)
   - `access_token`: Token JWT obtido do serviço de autenticação
   - `medico_id`: ID do médico para testes
   - `gestante_id`: ID da gestante para testes
   - `data_consulta`: Data no formato `yyyy-MM-dd`

### 3. Obter Token de Autenticação

Antes de usar as rotas protegidas, você precisa obter um token JWT:

1. Use a collection do módulo `prenatal-auth` para obter o token
2. Ou faça uma requisição manual:
   ```bash
   POST http://localhost:8079/oauth2/token
   Content-Type: application/x-www-form-urlencoded
   Authorization: Basic <base64(client_id:client_secret)>
   
   grant_type=password&username=<email>&password=<senha>&scope=read write
   ```
3. Copie o `access_token` da resposta e cole na variável `access_token` do ambiente

## 📚 Endpoints Disponíveis

### Health Check
- **GET** `/actuator/health` - Verifica status da API (público)

### Agenda Médico
- **POST** `/api/agendas/medico` - Criar agenda do médico (ROLE_DOCTOR)
- **GET** `/api/agendas/medico/{medicoId}` - Buscar agenda por médico (ROLE_DOCTOR, ROLE_NURSE)

### Consultas
- **POST** `/api/consultas/agendar` - Agendar consulta (ROLE_PATIENT, ROLE_NURSE)
- **DELETE** `/api/consultas/{id}/cancelar?motivo={ENUM}` - Cancelar consulta (ROLE_PATIENT, ROLE_NURSE, ROLE_DOCTOR)

### Gestantes
- **GET** `/api/gestantes/{id}/consultas` - Listar consultas da gestante (ROLE_PATIENT, ROLE_NURSE, ROLE_DOCTOR)

### Disponibilidade
- **GET** `/api/disponibilidade?medicoId={id}&data={yyyy-MM-dd}` - Consultar horários disponíveis (autenticado)

## 🔐 Permissões por Endpoint

| Endpoint | Método | Permissões Necessárias |
|----------|--------|------------------------|
| `/actuator/health` | GET | Público |
| `/api/agendas/medico` | POST | `ROLE_DOCTOR` |
| `/api/agendas/medico/**` | GET | `ROLE_DOCTOR`, `ROLE_NURSE` |
| `/api/consultas/agendar` | POST | `ROLE_PATIENT`, `ROLE_NURSE` |
| `/api/disponibilidade` | GET | Qualquer autenticado |
| `/api/gestantes/**/consultas` | GET | `ROLE_PATIENT`, `ROLE_NURSE`, `ROLE_DOCTOR` |
| `/api/consultas/**/cancelar` | DELETE | `ROLE_PATIENT`, `ROLE_NURSE`, `ROLE_DOCTOR` |

## 📝 Exemplos de Uso

### 1. Criar Agenda do Médico

```json
POST /api/agendas/medico
Authorization: Bearer <token>

{
    "medicoId": 1,
    "unidadeId": 1,
    "diasAtendimento": ["SEGUNDA", "TERCA", "QUARTA", "QUINTA", "SEXTA"],
    "horarioInicio": "08:00",
    "horarioFim": "17:00",
    "duracaoConsultaMinutos": 30
}
```

### 2. Agendar Consulta

```json
POST /api/consultas/agendar
Authorization: Bearer <token>

{
    "gestanteId": 1,
    "medicoId": 1,
    "unidadeId": 1,
    "data": "2024-12-20",
    "horario": "09:00"
}
```

### 3. Consultar Disponibilidade

```
GET /api/disponibilidade?medicoId=1&data=2024-12-20
Authorization: Bearer <token>
```

### 4. Cancelar Consulta

```
DELETE /api/consultas/1/cancelar?motivo=GESTANTE_DESISTIU
Authorization: Bearer <token>
```

## 🎯 Valores de Enum

### DiaSemana
- `SEGUNDA`
- `TERCA`
- `QUARTA`
- `QUINTA`
- `SEXTA`
- `SABADO`
- `DOMINGO`

### MotivoCancelamento
- `GESTANTE_DESISTIU`
- `MEDICO_INDISPONIVEL`
- `EMERGENCIA`
- `OUTRO`

## ⚠️ Observações Importantes

1. **Token JWT**: Todas as rotas (exceto health check) requerem um token JWT válido no header `Authorization: Bearer <token>`

2. **Validações de Agendamento**:
   - Data não pode ser no passado
   - Horário deve estar dentro da agenda do médico
   - Médico deve atender no dia da semana da consulta
   - Horário não pode estar ocupado

3. **Variáveis Automáticas**: 
   - O `consulta_id` é preenchido automaticamente após agendar uma consulta (via script de teste)

4. **Formato de Data**: Sempre use o formato `yyyy-MM-dd` (ex: `2024-12-20`)

5. **Formato de Horário**: Sempre use o formato `HH:mm` (ex: `09:00`, `14:30`)

## 🔧 Troubleshooting

### Erro 401 (Unauthorized)
- Verifique se o token JWT está válido e não expirou
- Certifique-se de que o token está no formato correto: `Bearer <token>`

### Erro 403 (Forbidden)
- Verifique se o usuário tem a role necessária para acessar o endpoint
- Consulte a tabela de permissões acima

### Erro 400 (Bad Request)
- Verifique o formato dos dados enviados
- Confira se os enums estão com valores válidos
- Verifique se a data não está no passado

### Erro 404 (Not Found)
- Verifique se os IDs (médico, gestante, consulta) existem no banco
- Confirme se a URL está correta

## 📞 Suporte

Para mais informações, consulte a documentação do projeto ou entre em contato com a equipe de desenvolvimento.
