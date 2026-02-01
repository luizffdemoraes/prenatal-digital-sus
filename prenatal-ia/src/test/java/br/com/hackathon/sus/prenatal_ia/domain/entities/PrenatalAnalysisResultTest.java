package br.com.hackathon.sus.prenatal_ia.domain.entities;

import br.com.hackathon.sus.prenatal_ia.domain.enums.AlertSeverity;
import br.com.hackathon.sus.prenatal_ia.domain.enums.AlertType;
import br.com.hackathon.sus.prenatal_ia.domain.enums.NotificationTarget;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do PrenatalAnalysisResult")
class PrenatalAnalysisResultTest {

    @Test
    @DisplayName("Construtor e getters")
    void construtorEGetters() {
        PrenatalAlert alert = new PrenatalAlert(AlertType.MISSING_EXAM, AlertSeverity.HIGH, "Teste", NotificationTarget.PATIENT);
        PrenatalAnalysisResult result = new PrenatalAnalysisResult("1", "Maria", "maria@email.com", 25, List.of(alert));

        assertEquals("1", result.getPatientId());
        assertEquals("Maria", result.getPatientName());
        assertEquals("maria@email.com", result.getPatientEmail());
        assertEquals(25, result.getGestationalWeeks());
        assertEquals(1, result.getAlerts().size());
        assertEquals(AlertType.MISSING_EXAM, result.getAlerts().get(0).getType());
    }

    @Test
    @DisplayName("Setters")
    void setters() {
        PrenatalAnalysisResult result = new PrenatalAnalysisResult();
        result.setPatientId("2");
        result.setPatientName("Joana");
        result.setPatientEmail("joana@email.com");
        result.setGestationalWeeks(30);
        result.setAlerts(List.of());

        assertEquals("2", result.getPatientId());
        assertEquals("Joana", result.getPatientName());
        assertEquals("joana@email.com", result.getPatientEmail());
        assertEquals(30, result.getGestationalWeeks());
        assertTrue(result.getAlerts().isEmpty());
    }
}
