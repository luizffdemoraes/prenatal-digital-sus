package br.com.hackathon.sus.prenatal_alertas.infrastructure.persistence.repositories;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import br.com.hackathon.sus.prenatal_alertas.domain.entities.PregnantPatient;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ProntuarioRepositoryImpl")
@SuppressWarnings({"null", "unchecked"})
class ProntuarioRepositoryImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ProntuarioRepositoryImpl repository;

    @Test
    @DisplayName("findAllActivePregnancies retorna lista vazia quando query não retorna registros")
    void findAllActivePregnanciesRetornaListaVazia() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(Collections.emptyList());

        List<PregnantPatient> result = repository.findAllActivePregnancies();

        assertTrue(result.isEmpty());
        verify(jdbcTemplate).query(anyString(), any(RowMapper.class));
    }

    @Test
    @DisplayName("findAllActivePregnancies retorna lista de pacientes quando query retorna dados")
    void findAllActivePregnanciesRetornaListaDePacientes() {
        PregnantPatient paciente = new PregnantPatient("uuid-1", "Maria", "12345678900", 20, "maria@email.com", false, List.of(), "Dr. João", "dr@email.com");
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(List.of(paciente));

        List<PregnantPatient> result = repository.findAllActivePregnancies();

        assertEquals(1, result.size());
        assertEquals("uuid-1", result.get(0).getId());
        assertEquals("Maria", result.get(0).getName());
        assertEquals("12345678900", result.get(0).getCpf());
        assertEquals(20, result.get(0).getGestationalWeeks());
        assertEquals("maria@email.com", result.get(0).getEmail());
        assertEquals("Dr. João", result.get(0).getDoctorName());
        assertEquals("dr@email.com", result.get(0).getDoctorEmail());
    }

    @Test
    @DisplayName("findAllActivePregnancies chama query com SQL que referencia prontuario.prontuario")
    void findAllActivePregnanciesUsaSqlDoProntuario() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(Collections.emptyList());

        repository.findAllActivePregnancies();

        verify(jdbcTemplate).query(argThat((String sql) -> sql != null && sql.contains("prontuario.prontuario")), (RowMapper<?>) any());
    }

    @Test
    @DisplayName("findAllActivePregnancies row mapper com fatores_risco null retorna lista vazia de fatores")
    void rowMapperComFatoresRiscoNull() throws Exception {
        UUID id = UUID.randomUUID();
        ResultSet rs = org.mockito.Mockito.mock(ResultSet.class);
        when(rs.getObject("id", UUID.class)).thenReturn(id);
        when(rs.getString("cpf")).thenReturn("12345678901");
        when(rs.getString("nome_completo")).thenReturn("Maria");
        when(rs.getObject("idade_gestacional_semanas", Integer.class)).thenReturn(20);
        when(rs.getBoolean("gestacao_alto_risco")).thenReturn(false);
        when(rs.getString("email_paciente")).thenReturn("maria@email.com");
        when(rs.getString("medico_nome")).thenReturn("Dr. João");
        when(rs.getString("medico_email")).thenReturn("dr@email.com");
        when(rs.getArray("fatores_risco")).thenReturn(null);

        doAnswer(inv -> {
            RowMapper<?> mapper = inv.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        }).when(jdbcTemplate).query(anyString(), any(RowMapper.class));

        List<PregnantPatient> result = repository.findAllActivePregnancies();

        assertEquals(1, result.size());
        assertEquals(id.toString(), result.get(0).getId());
        assertEquals("12345678901", result.get(0).getCpf());
        assertEquals("Maria", result.get(0).getName());
        assertEquals(20, result.get(0).getGestationalWeeks());
        assertTrue(result.get(0).getRiskFactors().isEmpty());
    }

    @Test
    @DisplayName("findAllActivePregnancies row mapper com fatores_risco array retorna fatores")
    void rowMapperComFatoresRiscoArray() throws Exception {
        UUID id = UUID.randomUUID();
        ResultSet rs = org.mockito.Mockito.mock(ResultSet.class);
        Array array = org.mockito.Mockito.mock(Array.class);
        when(array.getArray()).thenReturn(new String[] { "HIPERTENSAO", "DIABETES" });
        when(rs.getObject("id", UUID.class)).thenReturn(id);
        when(rs.getString("cpf")).thenReturn("12345678901");
        when(rs.getString("nome_completo")).thenReturn("Maria");
        when(rs.getObject("idade_gestacional_semanas", Integer.class)).thenReturn(25);
        when(rs.getBoolean("gestacao_alto_risco")).thenReturn(true);
        when(rs.getString("email_paciente")).thenReturn("maria@email.com");
        when(rs.getString("medico_nome")).thenReturn("Dr. João");
        when(rs.getString("medico_email")).thenReturn("dr@email.com");
        when(rs.getArray("fatores_risco")).thenReturn(array);

        doAnswer(inv -> {
            RowMapper<?> mapper = inv.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        }).when(jdbcTemplate).query(anyString(), any(RowMapper.class));

        List<PregnantPatient> result = repository.findAllActivePregnancies();

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getRiskFactors().size());
        assertTrue(result.get(0).getRiskFactors().contains("HIPERTENSAO"));
        assertTrue(result.get(0).getRiskFactors().contains("DIABETES"));
    }
}
