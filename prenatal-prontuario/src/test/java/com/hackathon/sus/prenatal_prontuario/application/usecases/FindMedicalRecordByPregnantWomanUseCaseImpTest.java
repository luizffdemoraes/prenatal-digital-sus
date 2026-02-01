package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.domain.entities.DeliveryType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.domain.entities.PregnancyType;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordGateway;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindMedicalRecordByPregnantWomanUseCaseImp")
class FindMedicalRecordByPregnantWomanUseCaseImpTest {

    @Mock
    private MedicalRecordGateway medicalRecordGateway;

    private FindMedicalRecordByPregnantWomanUseCaseImp useCase;

    private static final UUID GESTANTE_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new FindMedicalRecordByPregnantWomanUseCaseImp(medicalRecordGateway);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando prontuário não existe")
    void shouldReturnEmptyWhenNotFound() {
        when(medicalRecordGateway.findByPregnantWomanId(GESTANTE_ID)).thenReturn(Optional.empty());

        Optional<MedicalRecord> result = useCase.execute(GESTANTE_ID);

        assertTrue(result.isEmpty());
        verify(medicalRecordGateway).findByPregnantWomanId(GESTANTE_ID);
    }

    @Test
    @DisplayName("Deve retornar prontuário quando encontrado")
    void shouldReturnMedicalRecordWhenFound() {
        LocalDate dum = LocalDate.now().minusWeeks(20);
        MedicalRecord record = MedicalRecord.fromFirstAppointment(
                "12345678900", "Maria", null, dum,
                PregnancyType.SINGLETON, 0, 0, 0, false, null, List.of(),
                false, false, null, DeliveryType.UNDECIDED, null,
                null, null, null);
        record.setId(UUID.randomUUID());
        when(medicalRecordGateway.findByPregnantWomanId(GESTANTE_ID)).thenReturn(Optional.of(record));

        Optional<MedicalRecord> result = useCase.execute(GESTANTE_ID);

        assertTrue(result.isPresent());
        assertEquals(record.getId(), result.get().getId());
        assertEquals("Maria", result.get().getFullName());
        verify(medicalRecordGateway).findByPregnantWomanId(GESTANTE_ID);
    }
}
