package br.com.hackathon.sus.prenatal_ia.infrastructure.persistence.repositories;

import br.com.hackathon.sus.prenatal_ia.domain.entities.PregnantPatient;
import br.com.hackathon.sus.prenatal_ia.domain.repositories.ProntuarioRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ProntuarioRepositoryImpl implements ProntuarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProntuarioRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<PregnantPatient> findAllActivePregnancies() {
        String sql = """
            SELECT p.id, p.cpf, p.nome_completo, p.idade_gestacional_semanas, p.gestacao_alto_risco, p.email_paciente,
                   COALESCE((SELECT array_agg(f.fator_risco) FROM prontuario.prontuario_fatores_risco f WHERE f.prontuario_id = p.id), '{}') AS fatores_risco
            FROM prontuario.prontuario p
            WHERE p.cpf IS NOT NULL AND p.idade_gestacional_semanas BETWEEN 1 AND 44
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String id = rs.getObject("id", UUID.class) != null ? rs.getObject("id", UUID.class).toString() : null;
            String cpf = rs.getString("cpf");
            String nome = rs.getString("nome_completo");
            Integer semanas = rs.getObject("idade_gestacional_semanas", Integer.class);
            Boolean altoRisco = rs.getBoolean("gestacao_alto_risco");
            String email = rs.getString("email_paciente");
            List<String> fatores = parseFatoresRisco(rs.getArray("fatores_risco"));
            return new PregnantPatient(id, nome, cpf, semanas, email, altoRisco, fatores);
        });
    }

    private List<String> parseFatoresRisco(java.sql.Array array) {
        if (array == null) return List.of();
        try {
            String[] arr = (String[]) array.getArray();
            return arr != null ? List.of(arr) : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }
}
