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
class FindMedicalRecordByIdUseCaseImpTest {

    @Mock
    private MedicalRecordGateway medicalRecordGateway;

    @InjectMocks
    private FindMedicalRecordByIdUseCaseImp useCase;

    private UUID medicalRecordId;
    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        medicalRecordId = UUID.randomUUID();
        
        medicalRecord = new MedicalRecord(
                medicalRecordId,
                "12345678901",
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
        when(medicalRecordGateway.findById(medicalRecordId)).thenReturn(Optional.of(medicalRecord));

        // Act
        Optional<MedicalRecord> result = useCase.execute(medicalRecordId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(medicalRecord.getId(), result.get().getId());
        assertEquals(medicalRecord.getCpf(), result.get().getCpf());
        verify(medicalRecordGateway).findById(medicalRecordId);
    }

    @Test
    void deveRetornarEmptyQuandoProntuarioNaoEncontrado() {
        // Arrange
        when(medicalRecordGateway.findById(medicalRecordId)).thenReturn(Optional.empty());

        // Act
        Optional<MedicalRecord> result = useCase.execute(medicalRecordId);

        // Assert
        assertTrue(result.isEmpty());
        verify(medicalRecordGateway).findById(medicalRecordId);
    }

    @Test
    @DisplayName("Deve chamar gateway com ID correto")
    void shouldCallGatewayWithCorrectId() {
        // Arrange
        UUID testId = UUID.randomUUID();
        when(medicalRecordGateway.findById(testId)).thenReturn(Optional.empty());

        // Act
        useCase.execute(testId);

        // Assert
        verify(medicalRecordGateway).findById(testId);
        verify(medicalRecordGateway, never()).findById(medicalRecordId);
    }
}
