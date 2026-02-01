-- Schema e tabelas do Prontuário Service (unificado: init + CPF/dados obstétricos + tipo de parto)

CREATE SCHEMA IF NOT EXISTS prontuario;

-- Prontuário (um por gestação, criado na primeira consulta; identificação por CPF ou gestante_id/consulta_id)
CREATE TABLE prontuario.prontuario
(
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cpf                         VARCHAR(11),
    nome_completo               VARCHAR(255),
    data_nascimento             DATE,
    gestante_id                 UUID,
    consulta_id                 UUID,
    data_ultima_menstruacao     DATE,
    idade_gestacional_semanas   INTEGER      NOT NULL,
    tipo_gestacao               VARCHAR(20)  NOT NULL DEFAULT 'UNICA',
    numero_gestacoes_anteriores INTEGER      NOT NULL DEFAULT 0,
    numero_partos               INTEGER      NOT NULL DEFAULT 0,
    numero_abortos              INTEGER      NOT NULL DEFAULT 0,
    gestacao_alto_risco         BOOLEAN      NOT NULL DEFAULT FALSE,
    motivo_alto_risco           TEXT,
    uso_vitaminas               BOOLEAN      NOT NULL DEFAULT FALSE,
    uso_aas                     BOOLEAN      NOT NULL DEFAULT FALSE,
    observacoes                 TEXT,
    tipo_parto                  VARCHAR(20),
    email_paciente              VARCHAR(255),
    medico_nome                 VARCHAR(255),
    medico_email                VARCHAR(255),
    criado_em                   TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_idade_gestacional CHECK (idade_gestacional_semanas >= 1 AND idade_gestacional_semanas <= 44),
    CONSTRAINT chk_tipo_gestacao CHECK (tipo_gestacao IN ('UNICA', 'GEMELAR'))
);

COMMENT ON COLUMN prontuario.prontuario.tipo_parto IS 'PARTO_NATURAL, CESARIANA ou NAO_DEFINIDO';
COMMENT ON COLUMN prontuario.prontuario.email_paciente IS 'E-mail da gestante para notificações';
COMMENT ON COLUMN prontuario.prontuario.medico_nome IS 'Nome do médico responsável pelo acompanhamento';
COMMENT ON COLUMN prontuario.prontuario.medico_email IS 'E-mail do médico responsável para alertas';

-- Um prontuário por CPF (quando cpf preenchido)
CREATE UNIQUE INDEX idx_prontuario_cpf_unique ON prontuario.prontuario (cpf) WHERE cpf IS NOT NULL;

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
