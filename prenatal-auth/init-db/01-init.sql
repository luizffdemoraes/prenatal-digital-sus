-- Criar schemas
CREATE SCHEMA IF NOT EXISTS auth;

-- ======================
-- Schema: auth
-- ======================

CREATE TABLE auth.roles
(
    id   SERIAL PRIMARY KEY,
    authority VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE auth.users
(
    id               SERIAL PRIMARY KEY,
    name             VARCHAR(255)        NOT NULL,
    email            VARCHAR(255) UNIQUE NOT NULL,
    login            VARCHAR(255) UNIQUE NOT NULL,
    password         VARCHAR(255)        NOT NULL,
    last_update_date TIMESTAMP           NOT NULL DEFAULT NOW(),
    street           VARCHAR(255)        NOT NULL,
    number           BIGINT              NOT NULL,
    city             VARCHAR(255)        NOT NULL,
    state            VARCHAR(255)        NOT NULL,
    zip_code         VARCHAR(255)        NOT NULL
);

CREATE TABLE auth.user_role
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES auth.users (id),
    FOREIGN KEY (role_id) REFERENCES auth.roles (id)
);

INSERT INTO auth.roles (authority) VALUES ('ROLE_DOCTOR'), ('ROLE_NURSE'), ('ROLE_PATIENT');
