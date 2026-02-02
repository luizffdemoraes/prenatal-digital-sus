-- Verificar e-mail da gestante (prontuário já pode ter emailPaciente cadastrado; não é necessário UPDATE).
-- O prenatal-alertas lê prontuario.prontuario.email_paciente. Se o cadastro foi feito com emailPaciente, este SELECT mostra o valor.
-- Executar no MESMO banco em que o prenatal-alertas conecta (ex.: psql -U postgres -d prenatal_digital_sus -f scripts/verificar-email-gestante.sql)

SELECT p.id, p.nome_completo, p.email_paciente, p.idade_gestacional_semanas
FROM prontuario.prontuario p
WHERE p.cpf IS NOT NULL
  AND LENGTH(REGEXP_REPLACE(p.cpf, '[^0-9]', '', 'g')) = 11
  AND p.idade_gestacional_semanas BETWEEN 1 AND 44
ORDER BY p.idade_gestacional_semanas;
