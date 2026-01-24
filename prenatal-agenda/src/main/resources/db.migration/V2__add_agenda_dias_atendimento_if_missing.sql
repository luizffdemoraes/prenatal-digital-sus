-- Migração para corrigir bancos onde V1 foi aplicada antes de incluir agenda_dias_atendimento.
-- Cria a tabela apenas se não existir (idempotente).

CREATE TABLE IF NOT EXISTS agenda.agenda_dias_atendimento
(
    agenda_id  BIGINT       NOT NULL,
    dia_semana VARCHAR(20)  NOT NULL,
    PRIMARY KEY (agenda_id, dia_semana),
    FOREIGN KEY (agenda_id) REFERENCES agenda.agenda_medico (id) ON DELETE CASCADE,
    CONSTRAINT check_dia_semana_valido CHECK (dia_semana IN ('SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA', 'SABADO', 'DOMINGO'))
);
