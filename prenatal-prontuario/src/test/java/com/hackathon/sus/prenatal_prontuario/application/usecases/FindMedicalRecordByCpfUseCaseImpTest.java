package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.domain.entities.DeliveryType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.domain.entities.PregnancyType;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindMedicalRecordByCpfUseCaseImpTest {

    @Mock
    private MedicalRecordGateway medicalRecordGateway;

    @InjectMocks
    private FindMedicalRecordByCpfUseCaseImp useCase;

    private String cpf;
    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        cpf = "12345678901";
        
        medicalRecord = new MedicalRecord(
                UUID.randomUUID(),
                cpf,
                "Maria Silva",
                LocalDate.of(1990, 1, 1),
                null,
                null,
                LocalDate.now().minusWeeks(20),
                20,
                PregnancyType.SINGLETON,
                1,
                1,
                0,
                false,
                null,
                null,
                false,
                false,
                "Observações",
                DeliveryType.NATURAL,
                null,
                null,
                null,
                null
        );
    }

    @Test
    @DisplayName("Deve retornar prontuário quando encontrado")
    void shouldReturnRecordWhenFound() {
        // Arrange
        when(medicalRecordGateway.findByCpf(cpf)).thenReturn(Optional.of(medicalRecord));

        // Act
        Optional<MedicalRecord> result = useCase.execute(cpf);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(medicalRecord.getId(), result.get().getId());
        assertEquals(cpf, result.get().getCpf());
        verify(medicalRecordGateway).findByCpf(cpf);
    }

    @Test
    @DisplayName("Deve retornar vazio quando prontuário não encontrado")
    void shouldReturnEmptyWhenRecordNotFound() {
        // Arrange
        when(medicalRecordGateway.findByCpf(cpf)).thenReturn(Optional.empty());

        // Act
        Optional<MedicalRecord> result = useCase.execute(cpf);

        // Assert
        assertTrue(result.isEmpty());
        verify(medicalRecordGateway).findByCpf(cpf);
    }

    @Test
    @DisplayName("Deve chamar gateway com CPF correto")
    void shouldCallGatewayWithCorrectCpf() {
        // Arrange
        String testCpf = "98765432100";
        when(medicalRecordGateway.findByCpf(testCpf)).thenReturn(Optional.empty());

        // Act
        useCase.execute(testCpf);

        // Assert
        verify(medicalRecordGateway).findByCpf(testCpf);
        verify(medicalRecordGateway, never()).findByCpf(cpf);
    }
}
