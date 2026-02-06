package com.hackathon.sus.prenatal_prontuario.infrastructure.gateways;

import com.hackathon.sus.prenatal_prontuario.domain.entities.DeliveryType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.domain.entities.PregnancyType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.RiskFactor;
import com.hackathon.sus.prenatal_prontuario.infrastructure.config.mapper.MedicalRecordMapper;
import com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions.ResourceNotFoundException;
import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.entity.MedicalRecordEntity;
import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.repository.MedicalRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MedicalRecordGatewayImpl")
class MedicalRecordGatewayImplTest {

    @InjectMocks
    private MedicalRecordGatewayImpl gateway;

    @Mock
    private MedicalRecordRepository repository;

    private MedicalRecord domainRecord;
    private MedicalRecordEntity entity;
    private UUID recordId;

    @BeforeEach
    void setUp() {
        recordId = UUID.randomUUID();
        domainRecord = new MedicalRecord(
                recordId,
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
                List.of(RiskFactor.HYPERTENSION),
                true,
                false,
                "Observações",
                DeliveryType.NATURAL,
                LocalDateTime.now(),
                null,
                null,
                null
        );
        entity = MedicalRecordMapper.fromDomain(domainRecord);
    }

    @Test
    @DisplayName("save deve gerar UUID quando id é null")
    void save_shouldGenerateUuidWhenIdIsNull() {
        MedicalRecord withoutId = new MedicalRecord(
                null,
                "12345678901",
                "Maria",
                LocalDate.of(1990, 1, 1),
                null,
                null,
                LocalDate.now().minusWeeks(10),
                10,
                PregnancyType.SINGLETON,
                0,
                0,
                0,
                false,
                null,
                List.of(),
                false,
                false,
                null,
                DeliveryType.NATURAL,
                null,
                null,
                null,
                null
        );
        when(repository.save(any(MedicalRecordEntity.class))).thenAnswer(inv -> {
            MedicalRecordEntity e = inv.getArgument(0);
            if (e.getId() == null) e.setId(UUID.randomUUID());
            return e;
        });

        MedicalRecord saved = gateway.save(withoutId);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        ArgumentCaptor<MedicalRecordEntity> captor = ArgumentCaptor.forClass(MedicalRecordEntity.class);
        verify(repository).save(captor.capture());
        assertNotNull(captor.getValue().getId());
    }

    @Test
    @DisplayName("save deve persistir quando id já existe")
    void save_shouldPersistWhenIdExists() {
        when(repository.save(any(MedicalRecordEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        MedicalRecord saved = gateway.save(domainRecord);

        assertNotNull(saved);
        assertEquals(recordId, saved.getId());
        verify(repository).save(argThat(e -> recordId.equals(e.getId())));
    }

    @Test
    @DisplayName("findById deve retornar Optional vazio quando não encontrado")
    void findById_shouldReturnEmptyWhenNotFound() {
        when(repository.findById(recordId)).thenReturn(Optional.empty());

        Optional<MedicalRecord> result = gateway.findById(recordId);

        assertTrue(result.isEmpty());
        verify(repository).findById(recordId);
    }

    @Test
    @DisplayName("findById deve retornar prontuário quando encontrado")
    void findById_shouldReturnRecordWhenFound() {
        when(repository.findById(recordId)).thenReturn(Optional.of(entity));

        Optional<MedicalRecord> result = gateway.findById(recordId);

        assertTrue(result.isPresent());
        assertEquals(recordId, result.get().getId());
        assertEquals("12345678901", result.get().getCpf());
        verify(repository).findById(recordId);
    }

    @Test
    @DisplayName("findByPregnantWomanId deve retornar Optional vazio quando não encontrado")
    void findByPregnantWomanId_shouldReturnEmptyWhenNotFound() {
        UUID womanId = UUID.randomUUID();
        when(repository.findByPregnantWomanId(womanId)).thenReturn(Optional.empty());

        Optional<MedicalRecord> result = gateway.findByPregnantWomanId(womanId);

        assertTrue(result.isEmpty());
        verify(repository).findByPregnantWomanId(womanId);
    }

    @Test
    @DisplayName("findByPregnantWomanId deve retornar prontuário quando encontrado")
    void findByPregnantWomanId_shouldReturnRecordWhenFound() {
        UUID womanId = UUID.randomUUID();
        entity.setPregnantWomanId(womanId);
        when(repository.findByPregnantWomanId(womanId)).thenReturn(Optional.of(entity));

        Optional<MedicalRecord> result = gateway.findByPregnantWomanId(womanId);

        assertTrue(result.isPresent());
        assertEquals(womanId, result.get().getPregnantWomanId());
        verify(repository).findByPregnantWomanId(womanId);
    }

    @Test
    @DisplayName("findByCpf deve retornar Optional vazio quando não encontrado")
    void findByCpf_shouldReturnEmptyWhenNotFound() {
        when(repository.findByCpf("99999999999")).thenReturn(Optional.empty());

        Optional<MedicalRecord> result = gateway.findByCpf("99999999999");

        assertTrue(result.isEmpty());
        verify(repository).findByCpf("99999999999");
    }

    @Test
    @DisplayName("findByCpf deve retornar prontuário quando encontrado")
    void findByCpf_shouldReturnRecordWhenFound() {
        when(repository.findByCpf("12345678901")).thenReturn(Optional.of(entity));

        Optional<MedicalRecord> result = gateway.findByCpf("12345678901");

        assertTrue(result.isPresent());
        assertEquals("12345678901", result.get().getCpf());
        verify(repository).findByCpf("12345678901");
    }

    @Test
    @DisplayName("existsByPregnantWomanId deve retornar true quando existe")
    void existsByPregnantWomanId_shouldReturnTrueWhenExists() {
        UUID womanId = UUID.randomUUID();
        when(repository.existsByPregnantWomanId(womanId)).thenReturn(true);

        assertTrue(gateway.existsByPregnantWomanId(womanId));
        verify(repository).existsByPregnantWomanId(womanId);
    }

    @Test
    @DisplayName("existsByPregnantWomanId deve retornar false quando não existe")
    void existsByPregnantWomanId_shouldReturnFalseWhenNotExists() {
        UUID womanId = UUID.randomUUID();
        when(repository.existsByPregnantWomanId(womanId)).thenReturn(false);

        assertFalse(gateway.existsByPregnantWomanId(womanId));
        verify(repository).existsByPregnantWomanId(womanId);
    }

    @Test
    @DisplayName("existsByCpf deve retornar true quando existe")
    void existsByCpf_shouldReturnTrueWhenExists() {
        when(repository.existsByCpf("12345678901")).thenReturn(true);

        assertTrue(gateway.existsByCpf("12345678901"));
        verify(repository).existsByCpf("12345678901");
    }

    @Test
    @DisplayName("existsByCpf deve retornar false quando não existe")
    void existsByCpf_shouldReturnFalseWhenNotExists() {
        when(repository.existsByCpf("12345678901")).thenReturn(false);

        assertFalse(gateway.existsByCpf("12345678901"));
        verify(repository).existsByCpf("12345678901");
    }

    @Test
    @DisplayName("update deve lançar ResourceNotFoundException quando prontuário não existe")
    void update_shouldThrowWhenRecordNotFound() {
        when(repository.findById(recordId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gateway.update(domainRecord));
        verify(repository).findById(recordId);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("update deve aplicar alterações e salvar quando encontrado")
    void update_shouldApplyAndSaveWhenFound() {
        when(repository.findById(recordId)).thenReturn(Optional.of(entity));
        when(repository.save(any(MedicalRecordEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        domainRecord.updateClinicalData(true, true, "Notas atualizadas", DeliveryType.CESAREAN);

        MedicalRecord updated = gateway.update(domainRecord);

        assertNotNull(updated);
        verify(repository).findById(recordId);
        verify(repository).save(argThat(e -> e.getId().equals(recordId)));
    }
}
