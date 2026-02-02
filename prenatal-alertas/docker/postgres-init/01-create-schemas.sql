-- Schemas usados pelo prenatal-alertas (e pelos outros serviços do Prenatal Digital SUS).
-- As tabelas são criadas pelos Flyway de cada serviço (auth, prontuario, agenda, documento).
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS prontuario;
CREATE SCHEMA IF NOT EXISTS agenda;
CREATE SCHEMA IF NOT EXISTS documento;
