package br.com.hackathon.sus.prenatal_agenda.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Weekday - Enum")
class WeekdayTest {

    @Test
    @DisplayName("fromDayOfWeek mapeia corretamente cada dia")
    void fromDayOfWeekMapeiaCorretamente() {
        assertEquals(Weekday.SEGUNDA, Weekday.fromDayOfWeek(DayOfWeek.MONDAY));
        assertEquals(Weekday.TERCA, Weekday.fromDayOfWeek(DayOfWeek.TUESDAY));
        assertEquals(Weekday.QUARTA, Weekday.fromDayOfWeek(DayOfWeek.WEDNESDAY));
        assertEquals(Weekday.QUINTA, Weekday.fromDayOfWeek(DayOfWeek.THURSDAY));
        assertEquals(Weekday.SEXTA, Weekday.fromDayOfWeek(DayOfWeek.FRIDAY));
        assertEquals(Weekday.SABADO, Weekday.fromDayOfWeek(DayOfWeek.SATURDAY));
        assertEquals(Weekday.DOMINGO, Weekday.fromDayOfWeek(DayOfWeek.SUNDAY));
    }

    @Test
    @DisplayName("getDayOfWeek retorna o DayOfWeek correspondente")
    void getDayOfWeekRetornaCorrespondente() {
        assertEquals(DayOfWeek.MONDAY, Weekday.SEGUNDA.getDayOfWeek());
        assertEquals(DayOfWeek.SUNDAY, Weekday.DOMINGO.getDayOfWeek());
    }

    @Test
    @DisplayName("getDescricao retorna texto legível")
    void getDescricaoRetornaTexto() {
        assertNotNull(Weekday.SEGUNDA.getDescricao());
        assertFalse(Weekday.SEGUNDA.getDescricao().isBlank());
    }
}
