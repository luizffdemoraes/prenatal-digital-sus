-- Schema e tabelas do Prontuário Service
CREATE SCHEMA IF NOT EXISTS prontuario;

-- Prontuário (um por gestação, criado na primeira consulta)
CREATE TABLE prontuario.prontuario
(
    id                         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    gestante_id                UUID         NOT NULL,
    consulta_id                UUID         NOT NULL,
    idade_gestacional_semanas  INTEGER      NOT NULL,
    tipo_gestacao              VARCHAR(20)  NOT NULL DEFAULT 'UNICA',
    uso_vitaminas              BOOLEAN      NOT NULL DEFAULT FALSE,
    uso_aas                    BOOLEAN      NOT NULL DEFAULT FALSE,
    observacoes                TEXT,
    criado_em                  TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_idade_gestacional CHECK (idade_gestacional_semanas >= 1 AND idade_gestacional_semanas <= 44),
    CONSTRAINT chk_tipo_gestacao CHECK (tipo_gestacao IN ('UNICA', 'GEMELAR'))
);

-- Fatores de risco (N-N com prontuário)
CREATE TABLE prontuario.prontuario_fatores_risco
(
    prontuario_id  UUID         NOT NULL,
    fator_risco    VARCHAR(50)  NOT NULL,
    PRIMARY KEY (prontuario_id, fator_risco),
    FOREIGN KEY (prontuario_id) REFERENCES prontuario.prontuario (id) ON DELETE CASCADE
);

-- Histórico de alterações (MVP: data, profissional, alteração)
CREATE TABLE prontuario.prontuario_historico
(
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    prontuario_id       UUID         NOT NULL,
    data                TIMESTAMP    NOT NULL DEFAULT NOW(),
    profissional_user_id VARCHAR(255) NOT NULL,
    alteracao           TEXT         NOT NULL,
    FOREIGN KEY (prontuario_id) REFERENCES prontuario.prontuario (id) ON DELETE CASCADE
);

CREATE INDEX idx_prontuario_gestante ON prontuario.prontuario (gestante_id);
CREATE INDEX idx_prontuario_consulta ON prontuario.prontuario (consulta_id);
CREATE INDEX idx_prontuario_historico_prontuario ON prontuario.prontuario_historico (prontuario_id);
