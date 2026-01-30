-- Garante o schema e cria a tabela de documentos medicos (exames e ultrassons) vinculados ao CPF da paciente
CREATE SCHEMA IF NOT EXISTS documento;

CREATE TABLE documento.medical_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_cpf VARCHAR(14) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    storage_path VARCHAR(500) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_medical_documents_patient_cpf ON documento.medical_documents(patient_cpf);
CREATE INDEX idx_medical_documents_active ON documento.medical_documents(active);
CREATE INDEX idx_medical_documents_storage_path ON documento.medical_documents(storage_path);
