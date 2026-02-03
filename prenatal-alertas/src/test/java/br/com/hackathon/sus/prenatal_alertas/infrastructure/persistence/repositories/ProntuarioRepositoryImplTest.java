package br.com.hackathon.sus.prenatal_alertas.infrastructure.persistence.repositories;

import java.util.Collections;
import java.util.List;

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
}
