package br.com.hackathon.sus.prenatal_documento.domain.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Vaccine")
class VaccineTest {

    private static final String PATIENT_CPF = "12345678900";
    private static final String VACCINE_TYPE = "DTPA";
    private static final LocalDate APPLICATION_DATE = LocalDate.of(2025, 1, 15);

    @Test
    @DisplayName("Deve criar vacina com construtor completo")
    void shouldCreateVaccineWithFullConstructor() {
        Vaccine vaccine = new Vaccine(PATIENT_CPF, VACCINE_TYPE, APPLICATION_DATE);

        assertEquals(PATIENT_CPF, vaccine.getPatientCpf());
        assertEquals(VACCINE_TYPE, vaccine.getVaccineType());
        assertEquals(APPLICATION_DATE, vaccine.getApplicationDate());
        assertNull(vaccine.getId());
    }

    @Test
    @DisplayName("Deve configurar e obter ID")
    void shouldSetAndGetId() {
        Vaccine vaccine = new Vaccine(PATIENT_CPF, VACCINE_TYPE, APPLICATION_DATE);
        UUID id = UUID.randomUUID();

        vaccine.setId(id);

        assertEquals(id, vaccine.getId());
    }

    @Test
    @DisplayName("Getters e setters funcionam corretamente")
    void gettersAndSetters() {
        Vaccine vaccine = new Vaccine();
        vaccine.setId(UUID.randomUUID());
        vaccine.setPatientCpf(PATIENT_CPF);
        vaccine.setVaccineType(VACCINE_TYPE);
        vaccine.setApplicationDate(APPLICATION_DATE);

        assertNotNull(vaccine.getId());
        assertEquals(PATIENT_CPF, vaccine.getPatientCpf());
        assertEquals(VACCINE_TYPE, vaccine.getVaccineType());
        assertEquals(APPLICATION_DATE, vaccine.getApplicationDate());
    }
}
