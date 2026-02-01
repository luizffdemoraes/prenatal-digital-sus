package br.com.hackathon.sus.prenatal_documento.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_documento.application.dtos.responses.VaccineResponse;
import br.com.hackathon.sus.prenatal_documento.application.usecases.RegisterVaccineUseCase;
import br.com.hackathon.sus.prenatal_documento.domain.models.Vaccine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do VaccineController")
class VaccineControllerTest {

    @Mock
    private RegisterVaccineUseCase registerVaccineUseCase;

    @InjectMocks
    private VaccineController controller;

    private static final String PATIENT_CPF = "12345678900";
    private static final String VACCINE_TYPE = "DTpa";
    private static final LocalDate APPLICATION_DATE = LocalDate.of(2025, 1, 15);

    @Test
    @DisplayName("Deve registrar vacina e retornar VaccineResponse")
    void shouldRegisterVaccineAndReturnResponse() {
        Vaccine vaccine = new Vaccine(PATIENT_CPF, VACCINE_TYPE, APPLICATION_DATE);
        UUID vaccineId = UUID.randomUUID();
        vaccine.setId(vaccineId);
        when(registerVaccineUseCase.execute(eq(PATIENT_CPF), eq(VACCINE_TYPE), eq(APPLICATION_DATE)))
                .thenReturn(vaccine);

        VaccineResponse response = controller.register(PATIENT_CPF, VACCINE_TYPE, APPLICATION_DATE);

        assertNotNull(response);
        assertEquals(vaccineId, response.id());
        assertEquals(PATIENT_CPF, response.patientCpf());
        assertEquals(VACCINE_TYPE, response.vaccineType());
        assertEquals(APPLICATION_DATE, response.applicationDate());
        verify(registerVaccineUseCase).execute(PATIENT_CPF, VACCINE_TYPE, APPLICATION_DATE);
    }

    @Test
    @DisplayName("Deve propagar exceção quando use case lança")
    void shouldPropagateExceptionWhenUseCaseThrows() {
        when(registerVaccineUseCase.execute(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("CPF inválido"));

        assertThrows(IllegalArgumentException.class, () ->
                controller.register("invalid", VACCINE_TYPE, APPLICATION_DATE));
    }
}
