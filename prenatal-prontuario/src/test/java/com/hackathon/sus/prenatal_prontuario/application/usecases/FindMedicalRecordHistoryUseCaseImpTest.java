package com.hackathon.sus.prenatal_prontuario.application.usecases;

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
class FindMedicalRecordHistoryUseCaseImpTest {

    @Mock
    private MedicalRecordHistoryGateway historyGateway;

    @Mock
    private MedicalRecordGateway medicalRecordGateway;

    @InjectMocks
    private FindMedicalRecordHistoryUseCaseImp useCase;

    private String cpf;
    private MedicalRecord medicalRecord;
    private List<MedicalRecordHistory> historyList;

    @BeforeEach
    void setUp() {
        cpf = "12345678901";
        UUID recordId = UUID.randomUUID();
        
        medicalRecord = new MedicalRecord(
                recordId,
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
                null
        );

        historyList = List.of(
                new MedicalRecordHistory(recordId, "user-1", "Prontuário criado na primeira consulta"),
                new MedicalRecordHistory(recordId, "user-2", "Dados clínicos atualizados"),
                new MedicalRecordHistory(recordId, "user-3", "Fatores de risco atualizados")
        );
    }

    @Test
    @DisplayName("Deve retornar histórico quando prontuário existe")
    void shouldReturnHistoryWhenRecordExists() {
        // Arrange
        when(medicalRecordGateway.findByCpf(cpf)).thenReturn(Optional.of(medicalRecord));
        when(historyGateway.listByMedicalRecordId(medicalRecord.getId())).thenReturn(historyList);

        // Act
        List<MedicalRecordHistory> result = useCase.execute(cpf);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(historyList, result);
        
        verify(medicalRecordGateway).findByCpf(cpf);
        verify(historyGateway).listByMedicalRecordId(medicalRecord.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando prontuário não encontrado")
    void shouldThrowExceptionWhenRecordNotFound() {
        // Arrange
        when(medicalRecordGateway.findByCpf(cpf)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> useCase.execute(cpf));
        
        assertEquals("Prontuário não encontrado para o CPF informado.", exception.getMessage());
        
        verify(medicalRecordGateway).findByCpf(cpf);
        verify(historyGateway, never()).listByMedicalRecordId(any());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaHistorico() {
        // Arrange
        when(medicalRecordGateway.findByCpf(cpf)).thenReturn(Optional.of(medicalRecord));
        when(historyGateway.listByMedicalRecordId(medicalRecord.getId())).thenReturn(List.of());

        // Act
        List<MedicalRecordHistory> result = useCase.execute(cpf);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve usar ID do prontuário para buscar histórico")
    void shouldUseRecordIdToFetchHistory() {
        // Arrange
        UUID differentId = UUID.randomUUID();
        MedicalRecord differentRecord = new MedicalRecord(
                differentId,
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
                null
        );
        
        when(medicalRecordGateway.findByCpf(cpf)).thenReturn(Optional.of(differentRecord));
        when(historyGateway.listByMedicalRecordId(differentId)).thenReturn(historyList);

        // Act
        useCase.execute(cpf);

        // Assert
        verify(historyGateway).listByMedicalRecordId(differentId);
        verify(historyGateway, never()).listByMedicalRecordId(medicalRecord.getId());
    }
}
