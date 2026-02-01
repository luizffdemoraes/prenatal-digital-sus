package br.com.hackathon.sus.prenatal_documento.application.usecases;

import br.com.hackathon.sus.prenatal_documento.domain.models.Vaccine;
import br.com.hackathon.sus.prenatal_documento.domain.repositories.VaccineRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do RegisterVaccineUseCaseImpl")
class RegisterVaccineUseCaseImplTest {

    @Mock
    private VaccineRepository vaccineRepository;

    @InjectMocks
    private RegisterVaccineUseCaseImpl useCase;

    private static final String PATIENT_CPF = "12345678900";
    private static final String VACCINE_TYPE = "DTpa";
    private static final LocalDate APPLICATION_DATE = LocalDate.of(2025, 1, 15);

    @Test
    @DisplayName("Deve registrar vacina com sucesso")
    void shouldRegisterVaccineSuccessfully() {
        Vaccine saved = new Vaccine(PATIENT_CPF, "DTPA", APPLICATION_DATE);
        saved.setId(UUID.randomUUID());
        when(vaccineRepository.save(any(Vaccine.class))).thenReturn(saved);

        Vaccine result = useCase.execute(PATIENT_CPF, VACCINE_TYPE, APPLICATION_DATE);

        assertNotNull(result);
        assertEquals(PATIENT_CPF, result.getPatientCpf());
        assertEquals("DTPA", result.getVaccineType());
        assertEquals(APPLICATION_DATE, result.getApplicationDate());
        verify(vaccineRepository).save(any(Vaccine.class));
    }

    @Test
    @DisplayName("Deve normalizar CPF e tipo da vacina")
    void shouldNormalizeCpfAndVaccineType() {
        Vaccine saved = new Vaccine(PATIENT_CPF, "DTPA", APPLICATION_DATE);
        when(vaccineRepository.save(any(Vaccine.class))).thenReturn(saved);

        useCase.execute("123.456.789-00", "  dtpa  ", APPLICATION_DATE);

        verify(vaccineRepository).save(argThat(v ->
                "12345678900".equals(v.getPatientCpf()) && "DTPA".equals(v.getVaccineType())));
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF é nulo")
    void shouldThrowWhenCpfIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(null, VACCINE_TYPE, APPLICATION_DATE),
                "CPF da paciente é obrigatório");
        verify(vaccineRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF é vazio")
    void shouldThrowWhenCpfIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute("  ", VACCINE_TYPE, APPLICATION_DATE));
        verify(vaccineRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando tipo da vacina é nulo")
    void shouldThrowWhenVaccineTypeIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(PATIENT_CPF, null, APPLICATION_DATE),
                "Tipo da vacina é obrigatório");
        verify(vaccineRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando data de aplicação é nula")
    void shouldThrowWhenApplicationDateIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(PATIENT_CPF, VACCINE_TYPE, null),
                "Data de aplicação é obrigatória");
        verify(vaccineRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF é inválido (menos de 11 dígitos)")
    void shouldThrowWhenCpfInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute("123456", VACCINE_TYPE, APPLICATION_DATE),
                "CPF inválido");
        verify(vaccineRepository, never()).save(any());
    }
}
