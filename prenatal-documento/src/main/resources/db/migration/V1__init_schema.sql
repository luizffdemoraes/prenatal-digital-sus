-- Schema e tabelas do Documento Service (documentos médicos: exames, ultrassons)

CREATE SCHEMA IF NOT EXISTS documento;

-- Documento médico (exames e ultrassons por CPF da paciente)
CREATE TABLE documento.documento_medico
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cpf                     VARCHAR(14)      NOT NULL,
    nome_arquivo            VARCHAR(255)     NOT NULL,
    nome_original_arquivo   VARCHAR(255)     NOT NULL,
    tipo_conteudo           VARCHAR(100)     NOT NULL,
    tamanho_bytes           BIGINT          NOT NULL,
    tipo_documento          VARCHAR(50)      NOT NULL,
    caminho_armazenamento   VARCHAR(500)     NOT NULL,
    ativo                   BOOLEAN          NOT NULL DEFAULT TRUE,
    criado_em               TIMESTAMP        NOT NULL DEFAULT NOW(),
    atualizado_em            TIMESTAMP,
    excluido_em              TIMESTAMP,
    CONSTRAINT uq_documento_medico_caminho UNIQUE (caminho_armazenamento),
    CONSTRAINT chk_tipo_documento CHECK (tipo_documento IN ('EXAM', 'ULTRASOUND'))
);

COMMENT ON COLUMN documento.documento_medico.tipo_documento IS 'EXAM ou ULTRASOUND';
COMMENT ON COLUMN documento.documento_medico.caminho_armazenamento IS 'Caminho único no storage (ex.: S3)';
COMMENT ON COLUMN documento.documento_medico.ativo IS 'Soft delete: false quando inativado';

CREATE INDEX idx_documento_medico_cpf ON documento.documento_medico (cpf);
CREATE INDEX idx_documento_medico_ativo ON documento.documento_medico (ativo);
CREATE INDEX idx_documento_medico_caminho ON documento.documento_medico (caminho_armazenamento);
