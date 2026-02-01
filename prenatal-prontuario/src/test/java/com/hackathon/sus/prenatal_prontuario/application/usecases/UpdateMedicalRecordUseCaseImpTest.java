package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.application.dtos.requests.UpdateMedicalRecordRequest;
import com.hackathon.sus.prenatal_prontuario.domain.entities.DeliveryType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecordHistory;
import com.hackathon.sus.prenatal_prontuario.domain.entities.PregnancyType;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordGateway;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordHistoryGateway;
import com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateMedicalRecordUseCaseImpTest {

    @Mock
    private MedicalRecordGateway medicalRecordGateway;

    @Mock
    private MedicalRecordHistoryGateway historyGateway;

    @InjectMocks
    private UpdateMedicalRecordUseCaseImp useCase;

    private String cpf;
    private MedicalRecord existingRecord;
    private UpdateMedicalRecordRequest request;
    private String professionalUserId;

    @BeforeEach
    void setUp() {
        cpf = "12345678901";
        professionalUserId = "user-123";
        
        existingRecord = new MedicalRecord(
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
                "Observações antigas",
                DeliveryType.NATURAL,
                null,
                null,
                null,
                null
        );

        request = new UpdateMedicalRecordRequest(
                true,
                true,
                "Observações atualizadas",
                DeliveryType.CESAREAN,
                null,
                null,
                null
        );
    }

    @Test
    void deveAtualizarProntuarioComSucesso() {
        // Arrange
        when(medicalRecordGateway.findByCpf(cpf)).thenReturn(Optional.of(existingRecord));
        when(medicalRecordGateway.update(any(MedicalRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        MedicalRecord result = useCase.execute(cpf, request, professionalUserId);

        // Assert
        assertNotNull(result);
        assertEquals(request.vitaminUse(), result.getVitaminUse());
        assertEquals(request.aspirinUse(), result.getAspirinUse());
        assertEquals(request.notes(), result.getNotes());
        assertEquals(request.deliveryType(), result.getDeliveryType());
        
        verify(medicalRecordGateway).findByCpf(cpf);
        verify(medicalRecordGateway).update(any(MedicalRecord.class));
        
        ArgumentCaptor<MedicalRecordHistory> historyCaptor = ArgumentCaptor.forClass(MedicalRecordHistory.class);
        verify(historyGateway).register(historyCaptor.capture());
        
        MedicalRecordHistory history = historyCaptor.getValue();
        assertEquals(existingRecord.getId(), history.getMedicalRecordId());
        assertEquals(professionalUserId, history.getProfessionalUserId());
        assertEquals("Dados clínicos atualizados", history.getDescription());
    }

    @Test
    @DisplayName("Deve lançar exceção quando prontuário não encontrado")
    void shouldThrowExceptionWhenRecordNotFound() {
        // Arrange
        when(medicalRecordGateway.findByCpf(cpf)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> useCase.execute(cpf, request, professionalUserId));
        
        assertEquals("Prontuário não encontrado para o CPF informado.", exception.getMessage());
        
        verify(medicalRecordGateway).findByCpf(cpf);
        verify(medicalRecordGateway, never()).update(any());
        verify(historyGateway, never()).register(any());
    }

    @Test
    void deveUsarSistemaComoUserIdQuandoProfessionalUserIdEhNull() {
        // Arrange
        when(medicalRecordGateway.findByCpf(cpf)).thenReturn(Optional.of(existingRecord));
        when(medicalRecordGateway.update(any(MedicalRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        useCase.execute(cpf, request, null);

        // Assert
        ArgumentCaptor<MedicalRecordHistory> historyCaptor = ArgumentCaptor.forClass(MedicalRecordHistory.class);
        verify(historyGateway).register(historyCaptor.capture());
        
        assertEquals("sistema", historyCaptor.getValue().getProfessionalUserId());
    }

    @Test
    @DisplayName("Deve atualizar apenas campos informados")
    void shouldUpdateOnlyInformedFields() {
        // Arrange
        UpdateMedicalRecordRequest partialRequest = new UpdateMedicalRecordRequest(
                true,
                null,
                null,
                null,
                null,
                null,
                null
        );
        when(medicalRecordGateway.findByCpf(cpf)).thenReturn(Optional.of(existingRecord));
        when(medicalRecordGateway.update(any(MedicalRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        MedicalRecord result = useCase.execute(cpf, partialRequest, professionalUserId);

        // Assert
        assertEquals(true, result.getVitaminUse());
        assertEquals(existingRecord.getAspirinUse(), result.getAspirinUse());
        assertEquals(existingRecord.getNotes(), result.getNotes());
        assertEquals(existingRecord.getDeliveryType(), result.getDeliveryType());
    }
}
