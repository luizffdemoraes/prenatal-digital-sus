package br.com.hackathon.sus.prenatal_documento.infrastructure.persistence.repositories;

import br.com.hackathon.sus.prenatal_documento.domain.models.Vaccine;
import br.com.hackathon.sus.prenatal_documento.infrastructure.persistence.entities.VaccineEntity;
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
@DisplayName("Testes do VaccineRepositoryImpl")
class VaccineRepositoryImplTest {

    @Mock
    private JpaVaccineRepository jpaRepository;

    @InjectMocks
    private VaccineRepositoryImpl repository;

    private static final String PATIENT_CPF = "12345678900";
    private static final String VACCINE_TYPE = "DTPA";
    private static final LocalDate APPLICATION_DATE = LocalDate.of(2025, 1, 15);

    private Vaccine domainVaccine;
    private VaccineEntity entityVaccine;

    @BeforeEach
    void setUp() {
        domainVaccine = new Vaccine(PATIENT_CPF, VACCINE_TYPE, APPLICATION_DATE);
        entityVaccine = new VaccineEntity();
        entityVaccine.setId(UUID.randomUUID());
        entityVaccine.setPatientCpf(PATIENT_CPF);
        entityVaccine.setVaccineType(VACCINE_TYPE);
        entityVaccine.setApplicationDate(APPLICATION_DATE);
    }

    @Test
    @DisplayName("Deve salvar vacina e retornar domínio com ID")
    void shouldSaveVaccineAndReturnWithId() {
        when(jpaRepository.save(any(VaccineEntity.class))).thenReturn(entityVaccine);

        Vaccine result = repository.save(domainVaccine);

        assertNotNull(result);
        assertEquals(entityVaccine.getId(), result.getId());
        assertEquals(PATIENT_CPF, result.getPatientCpf());
        assertEquals(VACCINE_TYPE, result.getVaccineType());
        assertEquals(APPLICATION_DATE, result.getApplicationDate());

        ArgumentCaptor<VaccineEntity> captor = ArgumentCaptor.forClass(VaccineEntity.class);
        verify(jpaRepository, times(1)).save(captor.capture());
        VaccineEntity saved = captor.getValue();
        assertEquals(PATIENT_CPF, saved.getPatientCpf());
        assertEquals(VACCINE_TYPE, saved.getVaccineType());
        assertEquals(APPLICATION_DATE, saved.getApplicationDate());
    }

    @Test
    @DisplayName("Deve salvar vacina existente preservando ID")
    void shouldSaveExistingVaccinePreservingId() {
        UUID existingId = UUID.randomUUID();
        domainVaccine.setId(existingId);
        entityVaccine.setId(existingId);
        when(jpaRepository.save(any(VaccineEntity.class))).thenReturn(entityVaccine);

        Vaccine result = repository.save(domainVaccine);

        assertEquals(existingId, result.getId());
        ArgumentCaptor<VaccineEntity> captor = ArgumentCaptor.forClass(VaccineEntity.class);
        verify(jpaRepository).save(captor.capture());
        assertEquals(existingId, captor.getValue().getId());
    }

    @Test
    @DisplayName("Deve listar vacinas por CPF ordenadas por data decrescente")
    void shouldFindByPatientCpfOrderByApplicationDateDesc() {
        VaccineEntity entity2 = new VaccineEntity();
        entity2.setId(UUID.randomUUID());
        entity2.setPatientCpf(PATIENT_CPF);
        entity2.setVaccineType("INFLUENZA");
        entity2.setApplicationDate(LocalDate.of(2025, 2, 1));

        when(jpaRepository.findByPatientCpfOrderByApplicationDateDesc(PATIENT_CPF))
                .thenReturn(List.of(entity2, entityVaccine));

        List<Vaccine> result = repository.findByPatientCpf(PATIENT_CPF);

        assertEquals(2, result.size());
        assertEquals(entity2.getId(), result.get(0).getId());
        assertEquals("INFLUENZA", result.get(0).getVaccineType());
        assertEquals(entityVaccine.getId(), result.get(1).getId());
        assertEquals(VACCINE_TYPE, result.get(1).getVaccineType());
        verify(jpaRepository, times(1)).findByPatientCpfOrderByApplicationDateDesc(PATIENT_CPF);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há vacinas do paciente")
    void shouldReturnEmptyListWhenNoVaccinesForPatient() {
        when(jpaRepository.findByPatientCpfOrderByApplicationDateDesc(PATIENT_CPF)).thenReturn(List.of());

        List<Vaccine> result = repository.findByPatientCpf(PATIENT_CPF);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(jpaRepository, times(1)).findByPatientCpfOrderByApplicationDateDesc(PATIENT_CPF);
    }

    @Test
    @DisplayName("Deve mapear corretamente entity para domain")
    void shouldMapEntityToDomainCorrectly() {
        when(jpaRepository.findByPatientCpfOrderByApplicationDateDesc(PATIENT_CPF))
                .thenReturn(List.of(entityVaccine));

        List<Vaccine> result = repository.findByPatientCpf(PATIENT_CPF);

        assertEquals(1, result.size());
        Vaccine v = result.get(0);
        assertEquals(entityVaccine.getId(), v.getId());
        assertEquals(entityVaccine.getPatientCpf(), v.getPatientCpf());
        assertEquals(entityVaccine.getVaccineType(), v.getVaccineType());
        assertEquals(entityVaccine.getApplicationDate(), v.getApplicationDate());
    }
}
