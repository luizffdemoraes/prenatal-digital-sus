package br.com.hackathon.sus.prenatal_ia.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do PregnantPatient")
class PregnantPatientTest {

    @Test
    @DisplayName("hasRiskFactor retorna false quando highRisk é false e riskFactors vazio")
    void hasRiskFactor_retornaFalse_quandoSemFatores() {
        PregnantPatient patient = new PregnantPatient(
                "1", "Maria", "12345678900", 20, "maria@email.com",
                false, List.of());

        assertFalse(patient.hasRiskFactor());
    }

    @Test
    @DisplayName("hasRiskFactor retorna false quando highRisk e riskFactors são null")
    void hasRiskFactor_retornaFalse_quandoNull() {
        PregnantPatient patient = new PregnantPatient(
                "1", "Maria", "12345678900", 20, null, null, null);

        assertFalse(patient.hasRiskFactor());
    }

    @Test
    @DisplayName("hasRiskFactor retorna true quando highRisk é true")
    void hasRiskFactor_retornaTrue_quandoAltoRisco() {
        PregnantPatient patient = new PregnantPatient(
                "1", "Maria", "12345678900", 20, "maria@email.com",
                true, List.of());

        assertTrue(patient.hasRiskFactor());
    }

    @Test
    @DisplayName("hasRiskFactor retorna true quando riskFactors não vazio")
    void hasRiskFactor_retornaTrue_quandoFatoresRisco() {
        PregnantPatient patient = new PregnantPatient(
                "1", "Maria", "12345678900", 20, "maria@email.com",
                false, List.of("HIPERTENSAO", "DIABETES"));

        assertTrue(patient.hasRiskFactor());
    }

    @Test
    @DisplayName("getters e setters funcionam corretamente")
    void gettersAndSetters() {
        PregnantPatient patient = new PregnantPatient();
        patient.setId("id-1");
        patient.setName("Maria");
        patient.setCpf("12345678900");
        patient.setGestationalWeeks(25);
        patient.setEmail("maria@email.com");
        patient.setHighRisk(true);
        patient.setRiskFactors(List.of("OBESIDADE"));

        assertEquals("id-1", patient.getId());
        assertEquals("Maria", patient.getName());
        assertEquals("12345678900", patient.getCpf());
        assertEquals(25, patient.getGestationalWeeks());
        assertEquals("maria@email.com", patient.getEmail());
        assertTrue(patient.getHighRisk());
        assertEquals(List.of("OBESIDADE"), patient.getRiskFactors());
    }

    @Test
    @DisplayName("construtor com todos os parâmetros")
    void construtorCompletos() {
        PregnantPatient patient = new PregnantPatient(
                "1", "Maria", "12345678900", 20, "maria@email.com",
                false, List.of("HIPERTENSAO"));

        assertEquals("1", patient.getId());
        assertEquals("Maria", patient.getName());
        assertEquals("12345678900", patient.getCpf());
        assertEquals(20, patient.getGestationalWeeks());
        assertEquals("maria@email.com", patient.getEmail());
        assertFalse(patient.getHighRisk());
        assertEquals(List.of("HIPERTENSAO"), patient.getRiskFactors());
        assertTrue(patient.hasRiskFactor());
    }
}
