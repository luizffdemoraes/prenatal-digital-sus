package br.com.hackathon.sus.prenatal_ia.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do ExamRecord")
class ExamRecordTest {

    @Test
    @DisplayName("Construtor e getters")
    void construtorEGetters() {
        LocalDate data = LocalDate.of(2025, 1, 15);
        ExamRecord record = new ExamRecord("MORPHOLOGICAL_ULTRASOUND", data);

        assertEquals("MORPHOLOGICAL_ULTRASOUND", record.getType());
        assertEquals(data, record.getDate());
    }

    @Test
    @DisplayName("Setters")
    void setters() {
        ExamRecord record = new ExamRecord();
        record.setType("NUCHAL_TRANSLUCENCY");
        record.setDate(LocalDate.of(2025, 2, 1));

        assertEquals("NUCHAL_TRANSLUCENCY", record.getType());
        assertEquals(LocalDate.of(2025, 2, 1), record.getDate());
    }
}
