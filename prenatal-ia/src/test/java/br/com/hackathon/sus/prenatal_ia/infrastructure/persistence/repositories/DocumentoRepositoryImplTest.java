package br.com.hackathon.sus.prenatal_ia.infrastructure.persistence.repositories;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import br.com.hackathon.sus.prenatal_ia.domain.entities.ExamRecord;
import br.com.hackathon.sus.prenatal_ia.domain.entities.VaccineRecord;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do DocumentoRepositoryImpl")
class DocumentoRepositoryImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private DocumentoRepositoryImpl repository;

    @Test
    @DisplayName("findExamsByCpf retorna lista vazia quando CPF é null")
    void findExamsByCpfRetornaVaziaQuandoCpfNull() {
        List<ExamRecord> result = repository.findExamsByCpf(null);

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, never()).query(anyString(), any(RowMapper.class), anyString());
    }

    @Test
    @DisplayName("findExamsByCpf retorna lista vazia quando CPF tem menos de 11 dígitos")
    void findExamsByCpfRetornaVaziaQuandoCpfInvalido() {
        List<ExamRecord> result = repository.findExamsByCpf("12345");

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, never()).query(anyString(), any(RowMapper.class), anyString());
    }

    @Test
    @DisplayName("findExamsByCpf retorna lista do JdbcTemplate quando CPF válido")
    void findExamsByCpfRetornaListaQuandoCpfValido() {
        ExamRecord exame = new ExamRecord("MORPHOLOGICAL_ULTRASOUND", LocalDate.now());
        doReturn(List.of(exame)).when(jdbcTemplate).query(anyString(), (RowMapper<ExamRecord>) any(), eq("12345678901"));

        List<ExamRecord> result = repository.findExamsByCpf("12345678901");

        assertEquals(1, result.size());
        assertEquals("MORPHOLOGICAL_ULTRASOUND", result.get(0).getType());
    }

    @Test
    @DisplayName("findExamsByCpf normaliza CPF com formatação")
    void findExamsByCpfNormalizaCpf() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString())).thenReturn(Collections.emptyList());

        repository.findExamsByCpf("123.456.789-01");

        verify(jdbcTemplate).query(anyString(), (RowMapper<?>) any(), eq("12345678901"));
    }

    @Test
    @DisplayName("findVaccinesByCpf retorna lista vazia quando CPF é null")
    void findVaccinesByCpfRetornaVaziaQuandoCpfNull() {
        List<VaccineRecord> result = repository.findVaccinesByCpf(null);

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, never()).query(anyString(), any(RowMapper.class), anyString());
    }

    @Test
    @DisplayName("findVaccinesByCpf retorna lista vazia quando CPF inválido")
    void findVaccinesByCpfRetornaVaziaQuandoCpfInvalido() {
        List<VaccineRecord> result = repository.findVaccinesByCpf("12");

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, never()).query(anyString(), any(RowMapper.class), anyString());
    }

    @Test
    @DisplayName("findVaccinesByCpf retorna lista do JdbcTemplate quando CPF válido")
    void findVaccinesByCpfRetornaListaQuandoCpfValido() {
        VaccineRecord vacina = new VaccineRecord("DTPA", LocalDate.now());
        doReturn(List.of(vacina)).when(jdbcTemplate).query(anyString(), (RowMapper<VaccineRecord>) any(), eq("12345678901"));

        List<VaccineRecord> result = repository.findVaccinesByCpf("12345678901");

        assertEquals(1, result.size());
        assertEquals("DTPA", result.get(0).getType());
    }
}
