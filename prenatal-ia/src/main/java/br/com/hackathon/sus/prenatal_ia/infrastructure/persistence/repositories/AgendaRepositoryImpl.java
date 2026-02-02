package br.com.hackathon.sus.prenatal_ia.infrastructure.persistence.repositories;

import br.com.hackathon.sus.prenatal_ia.domain.entities.AppointmentSummary;
import br.com.hackathon.sus.prenatal_ia.domain.repositories.AgendaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Repository
public class AgendaRepositoryImpl implements AgendaRepository {

    private final JdbcTemplate jdbcTemplate;

    public AgendaRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<AppointmentSummary> findAppointmentsByCpf(String cpf) {
        String digits = cpf != null ? cpf.replaceAll("\\D", "") : "";
        if (digits.length() != 11) return Collections.emptyList();

        String sql = """
            SELECT c.id, c.data, c.horario, c.status
            FROM agenda.consulta c
            WHERE REPLACE(REPLACE(REPLACE(COALESCE(c.cpf, ''::text), '.'::text, ''::text), '-'::text, ''::text), ' '::text, ''::text) = ?
              AND c.status = 'AGENDADA'
              AND c.data >= CURRENT_DATE
            ORDER BY c.data, c.horario
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            LocalDate data = rs.getObject("data", Date.class) != null ? rs.getDate("data").toLocalDate() : null;
            LocalTime horario = rs.getObject("horario", Time.class) != null ? rs.getTime("horario").toLocalTime() : null;
            String status = rs.getString("status");
            return new AppointmentSummary(id, data, horario, status);
        }, digits);
    }
}
