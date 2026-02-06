package com.hackathon.sus.prenatal_prontuario.infrastructure.gateways;

import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecordHistory;
import com.hackathon.sus.prenatal_prontuario.infrastructure.config.mapper.MedicalRecordHistoryMapper;
import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.entity.MedicalRecordHistoryEntity;
import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.repository.MedicalRecordHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MedicalRecordHistoryGatewayImpl")
class MedicalRecordHistoryGatewayImplTest {

    @InjectMocks
    private MedicalRecordHistoryGatewayImpl gateway;

    @Mock
    private MedicalRecordHistoryRepository repository;

    private UUID medicalRecordId;
    private MedicalRecordHistory domainHistory;

    @BeforeEach
    void setUp() {
        medicalRecordId = UUID.randomUUID();
        domainHistory = new MedicalRecordHistory(medicalRecordId, "user-123", "Prontuário criado");
    }

    @Test
    @DisplayName("register deve gerar UUID quando id é null")
    void register_shouldGenerateUuidWhenIdIsNull() {
        MedicalRecordHistory withoutId = new MedicalRecordHistory(medicalRecordId, "user-1", "Alteração");
        when(repository.save(any(MedicalRecordHistoryEntity.class))).thenAnswer(inv -> {
            MedicalRecordHistoryEntity e = inv.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            return e;
        });

        MedicalRecordHistory saved = gateway.register(withoutId);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        ArgumentCaptor<MedicalRecordHistoryEntity> captor = ArgumentCaptor.forClass(MedicalRecordHistoryEntity.class);
        verify(repository).save(captor.capture());
        assertNotNull(captor.getValue().getId());
        assertEquals(medicalRecordId, captor.getValue().getMedicalRecordId());
        assertEquals("user-1", captor.getValue().getProfessionalUserId());
    }

    @Test
    @DisplayName("register deve persistir quando id já existe")
    void register_shouldPersistWhenIdExists() {
        UUID historyId = UUID.randomUUID();
        domainHistory.setId(historyId);
        when(repository.save(any(MedicalRecordHistoryEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        MedicalRecordHistory saved = gateway.register(domainHistory);

        assertNotNull(saved);
        assertEquals(historyId, saved.getId());
        verify(repository).save(argThat(e -> historyId.equals(e.getId())));
    }

    @Test
    @DisplayName("listByMedicalRecordId deve retornar lista vazia quando não há histórico")
    void listByMedicalRecordId_shouldReturnEmptyListWhenNoHistory() {
        when(repository.findByMedicalRecordIdOrderByOccurredAtDesc(medicalRecordId)).thenReturn(List.of());

        List<MedicalRecordHistory> result = gateway.listByMedicalRecordId(medicalRecordId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findByMedicalRecordIdOrderByOccurredAtDesc(medicalRecordId);
    }

    @Test
    @DisplayName("listByMedicalRecordId deve retornar lista ordenada por data decrescente")
    void listByMedicalRecordId_shouldReturnListOrderedByOccurredAtDesc() {
        MedicalRecordHistoryEntity e1 = new MedicalRecordHistoryEntity();
        e1.setId(UUID.randomUUID());
        e1.setMedicalRecordId(medicalRecordId);
        e1.setOccurredAt(LocalDateTime.now().minusDays(1));
        e1.setProfessionalUserId("user-1");
        e1.setDescription("Primeira alteração");

        MedicalRecordHistoryEntity e2 = new MedicalRecordHistoryEntity();
        e2.setId(UUID.randomUUID());
        e2.setMedicalRecordId(medicalRecordId);
        e2.setOccurredAt(LocalDateTime.now());
        e2.setProfessionalUserId("user-2");
        e2.setDescription("Segunda alteração");

        when(repository.findByMedicalRecordIdOrderByOccurredAtDesc(medicalRecordId))
                .thenReturn(List.of(e2, e1));

        List<MedicalRecordHistory> result = gateway.listByMedicalRecordId(medicalRecordId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Segunda alteração", result.get(0).getDescription());
        assertEquals("Primeira alteração", result.get(1).getDescription());
        verify(repository).findByMedicalRecordIdOrderByOccurredAtDesc(medicalRecordId);
    }
}
