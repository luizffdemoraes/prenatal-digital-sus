-- Script para verificar se há dados na base que gerariam alertas e envio de e-mail.
-- Executar no banco prenatal_digital_sus (ex.: psql -U postgres -d prenatal_digital_sus -f verificar-dados-para-alertas.sql)
-- Ou no DBeaver/pgAdmin conectado ao mesmo banco.

-- 1) Gestações ativas (mesmo critério do prenatal-alertas: cpf preenchido, 1 a 44 semanas)
SELECT 'Gestações ativas (prontuario.prontuario)' AS verificacao,
       COUNT(*) AS quantidade
FROM prontuario.prontuario p
WHERE p.cpf IS NOT NULL
  AND LENGTH(REGEXP_REPLACE(p.cpf, '[^0-9]', '', 'g')) = 11
  AND p.idade_gestacional_semanas BETWEEN 1 AND 44;

-- 2) Lista resumida (id, nome, semanas, email) para conferência
SELECT p.id, p.nome_completo, p.idade_gestacional_semanas, p.email_paciente, p.cpf
FROM prontuario.prontuario p
WHERE p.cpf IS NOT NULL
  AND LENGTH(REGEXP_REPLACE(p.cpf, '[^0-9]', '', 'g')) = 11
  AND p.idade_gestacional_semanas BETWEEN 1 AND 44
ORDER BY p.idade_gestacional_semanas;

-- Se quantidade = 0 acima: o job não encontra ninguém e não envia e-mail.
-- Se quantidade > 0: o job processa cada gestante; alertas dependem de exames/vacinas/consultas (documento + agenda).
