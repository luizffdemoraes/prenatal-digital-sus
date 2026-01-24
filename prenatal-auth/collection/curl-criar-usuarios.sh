#!/bin/bash
# Curls para criação de usuários no prenatal-auth (http://localhost:8079)
# Substitua <SEU_TOKEN> no header Authorization por um token válido, se a rota exigir.

BASE_URL="http://localhost:8079/v1/usuarios"
# TOKEN="<SEU_TOKEN>"   # descomente e preencha se precisar de autenticação

# ============================================================
#  1. CRIAR PACIENTE (gestante) - ROLE_PATIENT
# ============================================================
curl --location "$BASE_URL" \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer '"$TOKEN"'' \
--data-raw '{
    "nome": "Maria Santos",
    "email": "maria.santos@example.com",
    "login": "maria.santos",
    "senha": "senha123",
    "perfil": "ROLE_PATIENT",
    "endereco": {
        "rua": "Rua das Flores",
        "numero": 123,
        "cidade": "São Paulo",
        "estado": "SP",
        "cep": "01234-567"
    }
}'

# ============================================================
#  2. CRIAR ENFERMEIRO - ROLE_NURSE
# ============================================================
curl --location "$BASE_URL" \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer '"$TOKEN"'' \
--data-raw '{
    "nome": "Ana Oliveira",
    "email": "ana.oliveira@example.com",
    "login": "ana.oliveira",
    "senha": "senha123",
    "perfil": "ROLE_NURSE",
    "endereco": {
        "rua": "Av. Saúde",
        "numero": 456,
        "cidade": "São Paulo",
        "estado": "SP",
        "cep": "01345-678"
    }
}'

# ============================================================
#  3. CRIAR MÉDICO - ROLE_DOCTOR
# ============================================================
curl --location "$BASE_URL" \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer '"$TOKEN"'' \
--data-raw '{
    "nome": "Dr. Carlos Mendes",
    "email": "carlos.mendes@example.com",
    "login": "carlos.mendes",
    "senha": "senha123",
    "perfil": "ROLE_DOCTOR",
    "endereco": {
        "rua": "Rua do Hospital",
        "numero": 789,
        "cidade": "São Paulo",
        "estado": "SP",
        "cep": "01456-789"
    }
}'
