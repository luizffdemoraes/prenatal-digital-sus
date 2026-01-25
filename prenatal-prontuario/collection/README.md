# Prenatal Prontuário API - Postman

## Uso

1. **Importar** no Postman:
   - `Prenatal Prontuario API.postman_collection.json`
   - `Prenatal-Prontuario-Local.postman_environment.json`

2. **Obter JWT** no **prenatal-auth** (porta 8079):
   - `POST /oauth2/token` com `grant_type=password`, `username`, `password`, `scope=read write`
   - Ou usar a collection do prenatal-auth para obter `access_token`

3. **Configurar o environment** "Prenatal Prontuário - Local":
   - `access_token`: colar o JWT obtido no passo 2
   - `cpf`: CPF da gestante (11 dígitos, ex: 12345678900) para GET/PUT/PATCH/histórico por CPF

4. **Ordem sugerida**: Health Check → Criar prontuário → Buscar por CPF / Atualizar / Histórico

## Roles

- **ROLE_GESTANTE**, **ROLE_PATIENT**: GET /cpf/{cpf} e GET /cpf/{cpf}/historico (apenas o próprio; JWT com claim `cpf`)
- **ROLE_ENFERMEIRO**, **ROLE_MEDICO**, **ROLE_NURSE**, **ROLE_DOCTOR**: POST, PUT /cpf/{cpf}, PATCH /cpf/{cpf}/fatores-risco e todos os GET
