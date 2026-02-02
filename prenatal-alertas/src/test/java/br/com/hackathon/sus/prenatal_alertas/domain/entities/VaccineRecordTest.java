package br.com.hackathon.sus.prenatal_alertas.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do VaccineRecord")
class VaccineRecordTest {

    @Test
    @DisplayName("Construtor e getters")
    void construtorEGetters() {
        LocalDate data = LocalDate.of(2025, 1, 10);
        VaccineRecord record = new VaccineRecord("DTPA", data);

        assertEquals("DTPA", record.getType());
        assertEquals(data, record.getDate());
    }

    @Test
    @DisplayName("Setters")
    void setters() {
        VaccineRecord record = new VaccineRecord();
        record.setType("DT");
        record.setDate(LocalDate.of(2025, 2, 1));

        assertEquals("DT", record.getType());
        assertEquals(LocalDate.of(2025, 2, 1), record.getDate());
    }
}
