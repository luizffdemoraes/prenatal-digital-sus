package br.com.hackathon.sus.prenatal_ia.domain.entities;

import br.com.hackathon.sus.prenatal_ia.domain.enums.AlertSeverity;
import br.com.hackathon.sus.prenatal_ia.domain.enums.AlertType;
import br.com.hackathon.sus.prenatal_ia.domain.enums.NotificationTarget;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do PrenatalAlert")
class PrenatalAlertTest {

    @Test
    @DisplayName("construtor e getters")
    void construtorEGetters() {
        PrenatalAlert alert = new PrenatalAlert(
                AlertType.MISSING_EXAM,
                AlertSeverity.HIGH,
                "Ultrassom morfológico não encontrado",
                NotificationTarget.PATIENT);

        assertEquals(AlertType.MISSING_EXAM, alert.getType());
        assertEquals(AlertSeverity.HIGH, alert.getSeverity());
        assertEquals("Ultrassom morfológico não encontrado", alert.getMessage());
        assertEquals(NotificationTarget.PATIENT, alert.getTarget());
    }

    @Test
    @DisplayName("setters funcionam corretamente")
    void setters() {
        PrenatalAlert alert = new PrenatalAlert();
        alert.setType(AlertType.PENDING_VACCINE);
        alert.setSeverity(AlertSeverity.MEDIUM);
        alert.setMessage("Vacina pendente");
        alert.setTarget(NotificationTarget.PROFESSIONAL);

        assertEquals(AlertType.PENDING_VACCINE, alert.getType());
        assertEquals(AlertSeverity.MEDIUM, alert.getSeverity());
        assertEquals("Vacina pendente", alert.getMessage());
        assertEquals(NotificationTarget.PROFESSIONAL, alert.getTarget());
    }
}
