package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.application.dtos.requests.CreateMedicalRecordRequest;
import com.hackathon.sus.prenatal_prontuario.domain.entities.DeliveryType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecordHistory;
import com.hackathon.sus.prenatal_prontuario.domain.entities.PregnancyType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.RiskFactor;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordGateway;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordHistoryGateway;
import com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions.BusinessException;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateMedicalRecordUseCaseImpTest {

    @Mock
    private MedicalRecordGateway medicalRecordGateway;

    @Mock
    private MedicalRecordHistoryGateway historyGateway;

    @InjectMocks
    private CreateMedicalRecordUseCaseImp useCase;

    private CreateMedicalRecordRequest request;
    private MedicalRecord savedRecord;
    private String professionalUserId;

    @BeforeEach
    void setUp() {
        professionalUserId = "user-123";
        
        LocalDate lastMenstrualPeriod = LocalDate.now().minusWeeks(20);
        
        request = new CreateMedicalRecordRequest(
                "12345678901",
                "Maria Silva",
                LocalDate.of(1990, 1, 1),
                lastMenstrualPeriod,
                PregnancyType.SINGLETON,
                1,
                1,
                0,
                false,
                null,
                List.of(RiskFactor.HYPERTENSION),
                true,
                false,
                "Observações iniciais",
                DeliveryType.NATURAL,
                null
        );

        savedRecord = new MedicalRecord(
                UUID.randomUUID(),
                request.cpf(),
                request.fullName(),
                request.dateOfBirth(),
                null,
                null,
                request.lastMenstrualPeriod(),
                20,
                request.pregnancyType(),
                request.previousPregnancies(),
                request.previousDeliveries(),
                request.previousAbortions(),
                request.highRiskPregnancy(),
                request.highRiskReason(),
                request.riskFactors(),
                request.vitaminUse(),
                request.aspirinUse(),
                request.notes(),
                request.deliveryType(),
                null
        );
    }

    @Test
    @DisplayName("Deve criar prontuário com sucesso")
    void shouldCreateRecordSuccessfully() {
        // Arrange
        when(medicalRecordGateway.existsByCpf(request.cpf())).thenReturn(false);
        when(medicalRecordGateway.save(any(MedicalRecord.class))).thenReturn(savedRecord);

        // Act
        MedicalRecord result = useCase.execute(request, professionalUserId);

        // Assert
        assertNotNull(result);
        assertEquals(savedRecord.getId(), result.getId());
        assertEquals(request.cpf(), result.getCpf());
        assertEquals(request.fullName(), result.getFullName());
        
        verify(medicalRecordGateway).existsByCpf(request.cpf());
        verify(medicalRecordGateway).save(any(MedicalRecord.class));
        
        ArgumentCaptor<MedicalRecordHistory> historyCaptor = ArgumentCaptor.forClass(MedicalRecordHistory.class);
        verify(historyGateway).register(historyCaptor.capture());
        
        MedicalRecordHistory history = historyCaptor.getValue();
        assertEquals(savedRecord.getId(), history.getMedicalRecordId());
        assertEquals(professionalUserId, history.getProfessionalUserId());
        assertEquals("Prontuário criado na primeira consulta", history.getDescription());
    }

    @Test
    @DisplayName("Deve lançar exceção quando CPF já existe")
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        // Arrange
        when(medicalRecordGateway.existsByCpf(request.cpf())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> useCase.execute(request, professionalUserId));
        
        assertEquals("Já existe prontuário para este CPF. Um prontuário por gestação.", exception.getMessage());
        
        verify(medicalRecordGateway).existsByCpf(request.cpf());
        verify(medicalRecordGateway, never()).save(any());
        verify(historyGateway, never()).register(any());
    }

    @Test
    void deveUsarSistemaComoUserIdQuandoProfessionalUserIdEhNull() {
        // Arrange
        when(medicalRecordGateway.existsByCpf(request.cpf())).thenReturn(false);
        when(medicalRecordGateway.save(any(MedicalRecord.class))).thenReturn(savedRecord);

        // Act
        useCase.execute(request, null);

        // Assert
        ArgumentCaptor<MedicalRecordHistory> historyCaptor = ArgumentCaptor.forClass(MedicalRecordHistory.class);
        verify(historyGateway).register(historyCaptor.capture());
        
        assertEquals("sistema", historyCaptor.getValue().getProfessionalUserId());
    }

    @Test
    @DisplayName("Deve usar sistema como userId quando professionalUserId é vazio")
    void shouldUseSystemAsUserIdWhenProfessionalUserIdIsEmpty() {
        // Arrange
        when(medicalRecordGateway.existsByCpf(request.cpf())).thenReturn(false);
        when(medicalRecordGateway.save(any(MedicalRecord.class))).thenReturn(savedRecord);

        // Act
        useCase.execute(request, "");

        // Assert
        ArgumentCaptor<MedicalRecordHistory> historyCaptor = ArgumentCaptor.forClass(MedicalRecordHistory.class);
        verify(historyGateway).register(historyCaptor.capture());
        
        assertEquals("sistema", historyCaptor.getValue().getProfessionalUserId());
    }
}
