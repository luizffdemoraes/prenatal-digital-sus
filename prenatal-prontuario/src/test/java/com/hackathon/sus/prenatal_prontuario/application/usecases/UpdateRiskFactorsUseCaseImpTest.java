package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.application.dtos.requests.UpdateRiskFactorsRequest;
import com.hackathon.sus.prenatal_prontuario.domain.entities.DeliveryType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecordHistory;
import com.hackathon.sus.prenatal_prontuario.domain.entities.PregnancyType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.RiskFactor;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateRiskFactorsUseCaseImpTest {

    @Mock
    private MedicalRecordGateway medicalRecordGateway;

    @Mock
    private MedicalRecordHistoryGateway historyGateway;

    @InjectMocks
    private UpdateRiskFactorsUseCaseImp useCase;

    private String cpf;
    private MedicalRecord existingRecord;
    private UpdateRiskFactorsRequest request;
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
                List.of(RiskFactor.HYPERTENSION),
                false,
                false,
                "Observações",
                DeliveryType.NATURAL,
                null
        );

        request = new UpdateRiskFactorsRequest(
                List.of(RiskFactor.HYPERTENSION, RiskFactor.GESTATIONAL_DIABETES, RiskFactor.OBESITY)
        );
    }

    @Test
    @DisplayName("Deve atualizar fatores de risco com sucesso")
    void shouldUpdateRiskFactorsSuccessfully() {
        // Arrange
        when(medicalRecordGateway.findByCpf(cpf)).thenReturn(Optional.of(existingRecord));
        when(medicalRecordGateway.update(any(MedicalRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        MedicalRecord result = useCase.execute(cpf, request, professionalUserId);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getRiskFactors().size());
        assertTrue(result.getRiskFactors().contains(RiskFactor.HYPERTENSION));
        assertTrue(result.getRiskFactors().contains(RiskFactor.GESTATIONAL_DIABETES));
        assertTrue(result.getRiskFactors().contains(RiskFactor.OBESITY));
        
        verify(medicalRecordGateway).findByCpf(cpf);
        verify(medicalRecordGateway).update(any(MedicalRecord.class));
        
        ArgumentCaptor<MedicalRecordHistory> historyCaptor = ArgumentCaptor.forClass(MedicalRecordHistory.class);
        verify(historyGateway).register(historyCaptor.capture());
        
        MedicalRecordHistory history = historyCaptor.getValue();
        assertEquals(existingRecord.getId(), history.getMedicalRecordId());
        assertEquals(professionalUserId, history.getProfessionalUserId());
        assertEquals("Fatores de risco atualizados", history.getDescription());
    }

    @Test
    void deveLancarExcecaoQuandoProntuarioNaoEncontrado() {
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
    @DisplayName("Deve usar sistema como userId quando professionalUserId é null")
    void shouldUseSystemAsUserIdWhenProfessionalUserIdIsNull() {
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
    @DisplayName("Deve limpar fatores de risco quando lista vazia")
    void shouldClearRiskFactorsWhenListIsEmpty() {
        // Arrange
        UpdateRiskFactorsRequest emptyRequest = new UpdateRiskFactorsRequest(List.of());
        when(medicalRecordGateway.findByCpf(cpf)).thenReturn(Optional.of(existingRecord));
        when(medicalRecordGateway.update(any(MedicalRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        MedicalRecord result = useCase.execute(cpf, emptyRequest, professionalUserId);

        // Assert
        assertTrue(result.getRiskFactors().isEmpty());
    }

    @Test
    @DisplayName("Deve substituir fatores de risco anteriores")
    void shouldReplacePreviousRiskFactors() {
        // Arrange
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
                List.of(RiskFactor.ADVANCED_AGE, RiskFactor.TWIN_PREGNANCY),
                false,
                false,
                "Observações",
                DeliveryType.NATURAL,
                null
        );
        
        when(medicalRecordGateway.findByCpf(cpf)).thenReturn(Optional.of(existingRecord));
        when(medicalRecordGateway.update(any(MedicalRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        MedicalRecord result = useCase.execute(cpf, request, professionalUserId);

        // Assert
        assertEquals(3, result.getRiskFactors().size());
        assertFalse(result.getRiskFactors().contains(RiskFactor.ADVANCED_AGE));
        assertFalse(result.getRiskFactors().contains(RiskFactor.TWIN_PREGNANCY));
    }
}
