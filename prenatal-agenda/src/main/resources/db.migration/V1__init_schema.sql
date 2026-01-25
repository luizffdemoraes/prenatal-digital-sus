-- Schema unificado (init do zero). Executar após reset da base.
CREATE SCHEMA IF NOT EXISTS agenda;

-- ======================
-- Schema: agenda
-- ======================

-- Tabela de agenda do médico
CREATE TABLE agenda.agenda_medico
(
    id                        BIGSERIAL PRIMARY KEY,
    medico_id                 BIGINT       NOT NULL,
    unidade_id                BIGINT       NOT NULL,
    horario_inicio            TIME         NOT NULL,
    horario_fim               TIME         NOT NULL,
    duracao_consulta_minutos  INTEGER      NOT NULL,
    CONSTRAINT check_horario_valido CHECK (horario_inicio < horario_fim),
    CONSTRAINT check_duracao_positiva CHECK (duracao_consulta_minutos > 0)
);

-- Tabela de relacionamento para dias da semana de atendimento
CREATE TABLE agenda.agenda_dias_atendimento
(
    agenda_id  BIGINT       NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    PRIMARY KEY (agenda_id, dia_semana),
    FOREIGN KEY (agenda_id) REFERENCES agenda.agenda_medico (id) ON DELETE CASCADE,
    CONSTRAINT check_dia_semana_valido CHECK (dia_semana IN ('SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO', 'DOMINGO'))
);

-- Tabela de consultas
CREATE TABLE agenda.consulta
(
    id                    BIGSERIAL PRIMARY KEY,
    gestante_id           BIGINT       NOT NULL,
    medico_id             BIGINT       NOT NULL,
    unidade_id            BIGINT       NOT NULL,
    data                  DATE         NOT NULL,
    horario               TIME         NOT NULL,
    status                VARCHAR(20)  NOT NULL DEFAULT 'AGENDADA',
    motivo_cancelamento   VARCHAR(50),
    data_agendamento      TIMESTAMP    NOT NULL DEFAULT NOW(),
    data_cancelamento     TIMESTAMP,
    CONSTRAINT check_status_valido CHECK (status IN ('AGENDADA', 'CANCELADA', 'REALIZADA')),
    CONSTRAINT check_motivo_cancelamento CHECK (
        (status = 'CANCELADA' AND motivo_cancelamento IS NOT NULL) OR
        (status != 'CANCELADA' AND motivo_cancelamento IS NULL)
    ),
    CONSTRAINT check_data_nao_passada CHECK (data >= CURRENT_DATE)
);

-- Índices para melhor performance
CREATE INDEX idx_agenda_medico_medico_id ON agenda.agenda_medico (medico_id);
CREATE INDEX idx_agenda_medico_unidade_id ON agenda.agenda_medico (unidade_id);
CREATE INDEX idx_consulta_gestante_id ON agenda.consulta (gestante_id);
CREATE INDEX idx_consulta_medico_id ON agenda.consulta (medico_id);
CREATE INDEX idx_consulta_data ON agenda.consulta (data);
CREATE INDEX idx_consulta_medico_data_status ON agenda.consulta (medico_id, data, status);
CREATE INDEX idx_consulta_medico_data_horario_status ON agenda.consulta (medico_id, data, horario, status);
