package br.com.hackathon.sus.prenatal_ia.infrastructure.persistence.repositories;

import br.com.hackathon.sus.prenatal_ia.domain.entities.ExamRecord;
import br.com.hackathon.sus.prenatal_ia.domain.entities.VaccineRecord;
import br.com.hackathon.sus.prenatal_ia.domain.repositories.DocumentoRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Repository
public class DocumentoRepositoryImpl implements DocumentoRepository {

    private final JdbcTemplate jdbcTemplate;

    public DocumentoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ExamRecord> findExamsByCpf(String cpf) {
        String digits = cpf != null ? cpf.replaceAll("\\D", "") : "";
        if (digits.length() != 11) return Collections.emptyList();

        String sql = """
            SELECT COALESCE(tipo_exame, tipo_documento) AS tipo, criado_em
            FROM documento.documento_medico
            WHERE REPLACE(REPLACE(REPLACE(cpf, '.', ''), '-', ''), ' ') = ? AND ativo = TRUE
              AND (tipo_documento = 'EXAM' OR tipo_documento = 'ULTRASOUND')
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String tipo = rs.getString("tipo");
            LocalDate data = null;
            Timestamp ts = rs.getTimestamp("criado_em");
            if (ts != null) data = ts.toLocalDateTime().toLocalDate();
            return new ExamRecord(tipo, data);
        }, digits);
    }

    @Override
    public List<VaccineRecord> findVaccinesByCpf(String cpf) {
        String digits = cpf != null ? cpf.replaceAll("\\D", "") : "";
        if (digits.length() != 11) return Collections.emptyList();

        String sql = """
            SELECT tipo_vacina, data_aplicacao
            FROM documento.vacina
            WHERE REPLACE(REPLACE(REPLACE(cpf, '.', ''), '-', ''), ' ') = ?
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String tipo = rs.getString("tipo_vacina");
            LocalDate data = rs.getObject("data_aplicacao", LocalDate.class);
            return new VaccineRecord(tipo, data);
        }, digits);
    }
}
