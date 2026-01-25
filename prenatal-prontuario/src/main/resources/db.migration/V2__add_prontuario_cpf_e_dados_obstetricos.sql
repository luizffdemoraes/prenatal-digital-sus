-- Novos campos: identificação por CPF e dados obstétricos da primeira consulta
-- gestante_id e consulta_id passam a ser opcionais (fluxo por CPF)

ALTER TABLE prontuario.prontuario
    ALTER COLUMN gestante_id DROP NOT NULL,
    ALTER COLUMN consulta_id DROP NOT NULL;

ALTER TABLE prontuario.prontuario
    ADD COLUMN cpf VARCHAR(11),
    ADD COLUMN nome_completo VARCHAR(255),
    ADD COLUMN data_nascimento DATE,
    ADD COLUMN data_ultima_menstruacao DATE,
    ADD COLUMN numero_gestacoes_anteriores INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN numero_partos INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN numero_abortos INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN gestacao_alto_risco BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN motivo_alto_risco TEXT;

-- Um prontuário por CPF (quando cpf preenchido)
CREATE UNIQUE INDEX idx_prontuario_cpf_unique ON prontuario.prontuario (cpf) WHERE cpf IS NOT NULL;
