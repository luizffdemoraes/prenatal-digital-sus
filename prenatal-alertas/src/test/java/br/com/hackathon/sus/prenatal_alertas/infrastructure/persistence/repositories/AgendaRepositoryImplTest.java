package br.com.hackathon.sus.prenatal_alertas.infrastructure.persistence.repositories;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import br.com.hackathon.sus.prenatal_alertas.domain.entities.AppointmentSummary;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AgendaRepositoryImpl")
class AgendaRepositoryImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AgendaRepositoryImpl repository;

    @Test
    @DisplayName("retorna lista vazia quando CPF é null")
    void retornaListaVaziaQuandoCpfNull() {
        List<AppointmentSummary> result = repository.findAppointmentsByCpf(null);

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, never()).query(anyString(), any(RowMapper.class), anyString());
    }

    @Test
    @DisplayName("retorna lista vazia quando CPF tem menos de 11 dígitos")
    void retornaListaVaziaQuandoCpfInvalido() {
        List<AppointmentSummary> result = repository.findAppointmentsByCpf("123");

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, never()).query(anyString(), any(RowMapper.class), anyString());
    }

    @Test
    @DisplayName("retorna lista vazia quando CPF tem mais de 11 dígitos")
    void retornaListaVaziaQuandoCpfMaisDe11Digitos() {
        List<AppointmentSummary> result = repository.findAppointmentsByCpf("123456789012");

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, never()).query(anyString(), any(RowMapper.class), anyString());
    }

    @Test
    @DisplayName("normaliza CPF com formatação e chama query com 11 dígitos")
    void normalizaCpfEChamaQueryCom11Digitos() {
        AppointmentSummary appointment = new AppointmentSummary(1L, LocalDate.now(), LocalTime.of(9, 0), "AGENDADA");
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString())).thenReturn(List.of(appointment));

        List<AppointmentSummary> result = repository.findAppointmentsByCpf("123.456.789-01");

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("AGENDADA", result.get(0).getStatus());

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), (RowMapper<?>) any(), argCaptor.capture());
        assertEquals("12345678901", argCaptor.getValue());
        assertTrue(sqlCaptor.getValue().contains("agenda.consulta"));
    }

    @Test
    @DisplayName("retorna lista retornada pelo JdbcTemplate quando CPF válido")
    void retornaListaDoJdbcTemplateQuandoCpfValido() {
        AppointmentSummary a1 = new AppointmentSummary(1L, LocalDate.now(), LocalTime.of(9, 0), "AGENDADA");
        AppointmentSummary a2 = new AppointmentSummary(2L, LocalDate.now().plusDays(1), LocalTime.of(10, 30), "AGENDADA");
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq("12345678901"))).thenReturn(List.of(a1, a2));

        List<AppointmentSummary> result = repository.findAppointmentsByCpf("12345678901");

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    @DisplayName("row mapper mapeia linha com data e horario nulos")
    void rowMapperMapeiaDataEHorarioNulos() throws Exception {
        ResultSet rs = org.mockito.Mockito.mock(ResultSet.class);
        when(rs.getLong("id")).thenReturn(99L);
        when(rs.getObject("data", Date.class)).thenReturn(null);
        when(rs.getObject("horario", Time.class)).thenReturn(null);
        when(rs.getString("status")).thenReturn("AGENDADA");

        doAnswer(inv -> {
            RowMapper<?> mapper = inv.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        }).when(jdbcTemplate).query(anyString(), any(RowMapper.class), eq("12345678901"));

        List<AppointmentSummary> result = repository.findAppointmentsByCpf("12345678901");

        assertEquals(1, result.size());
        assertEquals(99L, result.get(0).getId());
        assertNull(result.get(0).getDate());
        assertNull(result.get(0).getTime());
        assertEquals("AGENDADA", result.get(0).getStatus());
    }
}
