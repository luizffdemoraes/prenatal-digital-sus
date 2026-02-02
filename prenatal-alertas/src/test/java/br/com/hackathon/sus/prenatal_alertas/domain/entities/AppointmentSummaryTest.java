package br.com.hackathon.sus.prenatal_alertas.domain.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes do AppointmentSummary")
class AppointmentSummaryTest {

    @Test
    @DisplayName("construtor com todos os campos preenche corretamente")
    void construtorPreencheCampos() {
        LocalDate data = LocalDate.of(2025, 3, 15);
        LocalTime horario = LocalTime.of(9, 30);
        AppointmentSummary summary = new AppointmentSummary(1L, data, horario, "AGENDADA");

        assertEquals(1L, summary.getId());
        assertEquals(data, summary.getDate());
        assertEquals(horario, summary.getTime());
        assertEquals("AGENDADA", summary.getStatus());
    }

    @Test
    @DisplayName("setters atualizam os valores")
    void settersAtualizamValores() {
        AppointmentSummary summary = new AppointmentSummary();
        LocalDate data = LocalDate.now();
        LocalTime horario = LocalTime.of(14, 0);

        summary.setId(2L);
        summary.setDate(data);
        summary.setTime(horario);
        summary.setStatus("CANCELADA");

        assertEquals(2L, summary.getId());
        assertEquals(data, summary.getDate());
        assertEquals(horario, summary.getTime());
        assertEquals("CANCELADA", summary.getStatus());
    }

    @Test
    @DisplayName("construtor vazio permite instanciação")
    void construtorVazioPermiteInstanciacao() {
        AppointmentSummary summary = new AppointmentSummary();
        assertNotNull(summary);
        assertNull(summary.getId());
        assertNull(summary.getDate());
        assertNull(summary.getTime());
        assertNull(summary.getStatus());
    }
}
