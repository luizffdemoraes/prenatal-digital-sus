package br.com.hackathon.sus.prenatal_documento.infrastructure.persistence.repositories;

import br.com.hackathon.sus.prenatal_documento.domain.enums.DocumentType;
import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import br.com.hackathon.sus.prenatal_documento.infrastructure.persistence.entities.MedicalDocumentEntity;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do MedicalDocumentRepositoryImpl")
class MedicalDocumentRepositoryImplTest {

    @Mock
    private JpaMedicalDocumentRepository jpaRepository;

    @InjectMocks
    private MedicalDocumentRepositoryImpl repository;

    private static final String PATIENT_CPF = "12345678900";
    private static final String FILE_NAME = "exame.pdf";
    private static final String STORAGE_PATH = "prenatal-records/12345678900/exame.pdf";

    private MedicalDocument domainDocument;
    private MedicalDocumentEntity entityDocument;

    @BeforeEach
    void setUp() {
        domainDocument = new MedicalDocument(
                PATIENT_CPF,
                FILE_NAME,
                FILE_NAME,
                "application/pdf",
                1024L,
                DocumentType.EXAM,
                null,
                STORAGE_PATH
        );

        entityDocument = new MedicalDocumentEntity();
        entityDocument.setId(UUID.randomUUID());
        entityDocument.setPatientCpf(PATIENT_CPF);
        entityDocument.setFileName(FILE_NAME);
        entityDocument.setOriginalFileName(FILE_NAME);
        entityDocument.setContentType("application/pdf");
        entityDocument.setFileSize(1024L);
        entityDocument.setDocumentType(DocumentType.EXAM);
        entityDocument.setStoragePath(STORAGE_PATH);
        entityDocument.setActive(true);
        entityDocument.setCreatedAt(LocalDateTime.now());
        entityDocument.setUpdatedAt(null);
        entityDocument.setDeletedAt(null);
    }

    @Test
    @DisplayName("Deve salvar novo documento e retornar com ID")
    void shouldSaveNewDocumentAndReturnWithId() {
        when(jpaRepository.save(any(MedicalDocumentEntity.class))).thenReturn(entityDocument);

        MedicalDocument result = repository.save(domainDocument);

        assertNotNull(result);
        assertEquals(entityDocument.getId(), result.getId());
        assertEquals(PATIENT_CPF, result.getPatientCpf());
        assertEquals(FILE_NAME, result.getFileName());
        assertEquals(DocumentType.EXAM, result.getDocumentType());
        assertTrue(result.getActive());

        ArgumentCaptor<MedicalDocumentEntity> captor = ArgumentCaptor.forClass(MedicalDocumentEntity.class);
        verify(jpaRepository, times(1)).save(captor.capture());
        MedicalDocumentEntity saved = captor.getValue();
        assertEquals(PATIENT_CPF, saved.getPatientCpf());
        assertEquals(FILE_NAME, saved.getFileName());
        assertNull(saved.getId());
    }

    @Test
    @DisplayName("Deve salvar documento existente preservando ID")
    void shouldSaveExistingDocumentPreservingId() {
        UUID existingId = UUID.randomUUID();
        domainDocument.setId(existingId);
        entityDocument.setId(existingId);
        when(jpaRepository.save(any(MedicalDocumentEntity.class))).thenReturn(entityDocument);

        MedicalDocument result = repository.save(domainDocument);

        assertEquals(existingId, result.getId());

        ArgumentCaptor<MedicalDocumentEntity> captor = ArgumentCaptor.forClass(MedicalDocumentEntity.class);
        verify(jpaRepository, times(1)).save(captor.capture());
        assertEquals(existingId, captor.getValue().getId());
    }

    @Test
    @DisplayName("Deve encontrar documento por ID")
    void shouldFindById() {
        when(jpaRepository.findById(entityDocument.getId())).thenReturn(Optional.of(entityDocument));

        Optional<MedicalDocument> result = repository.findById(entityDocument.getId());

        assertTrue(result.isPresent());
        assertEquals(entityDocument.getId(), result.get().getId());
        assertEquals(PATIENT_CPF, result.get().getPatientCpf());
        assertEquals(FILE_NAME, result.get().getFileName());
        assertEquals(DocumentType.EXAM, result.get().getDocumentType());
        verify(jpaRepository, times(1)).findById(entityDocument.getId());
    }

    @Test
    @DisplayName("Deve retornar vazio quando documento não existe")
    void shouldReturnEmptyWhenDocumentNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(jpaRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        Optional<MedicalDocument> result = repository.findById(nonExistentId);

        assertTrue(result.isEmpty());
        verify(jpaRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Deve listar documentos ativos por CPF do paciente")
    void shouldFindByPatientCpfAndActiveTrue() {
        MedicalDocumentEntity entity2 = new MedicalDocumentEntity();
        entity2.setId(UUID.randomUUID());
        entity2.setPatientCpf(PATIENT_CPF);
        entity2.setFileName("exame2.pdf");
        entity2.setOriginalFileName("exame2.pdf");
        entity2.setContentType("application/pdf");
        entity2.setFileSize(2048L);
        entity2.setDocumentType(DocumentType.ULTRASOUND);
        entity2.setStoragePath(STORAGE_PATH + "2");
        entity2.setActive(true);

        when(jpaRepository.findByPatientCpfAndActiveTrue(PATIENT_CPF))
                .thenReturn(List.of(entityDocument, entity2));

        List<MedicalDocument> result = repository.findByPatientCpfAndActiveTrue(PATIENT_CPF);

        assertEquals(2, result.size());
        assertEquals(entityDocument.getId(), result.get(0).getId());
        assertEquals(entity2.getId(), result.get(1).getId());
        assertEquals(DocumentType.ULTRASOUND, result.get(1).getDocumentType());
        verify(jpaRepository, times(1)).findByPatientCpfAndActiveTrue(PATIENT_CPF);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há documentos do paciente")
    void shouldReturnEmptyListWhenNoDocumentsForPatient() {
        when(jpaRepository.findByPatientCpfAndActiveTrue(PATIENT_CPF)).thenReturn(List.of());

        List<MedicalDocument> result = repository.findByPatientCpfAndActiveTrue(PATIENT_CPF);

        assertTrue(result.isEmpty());
        verify(jpaRepository, times(1)).findByPatientCpfAndActiveTrue(PATIENT_CPF);
    }

    @Test
    @DisplayName("Deve deletar documento")
    void shouldDeleteDocument() {
        domainDocument.setId(entityDocument.getId());
        domainDocument.setActive(false);

        repository.delete(domainDocument);

        ArgumentCaptor<MedicalDocumentEntity> captor = ArgumentCaptor.forClass(MedicalDocumentEntity.class);
        verify(jpaRepository, times(1)).delete(captor.capture());
        MedicalDocumentEntity deleted = captor.getValue();
        assertEquals(entityDocument.getId(), deleted.getId());
        assertFalse(deleted.getActive());
    }

    @Test
    @DisplayName("Deve mapear corretamente todos os campos na conversão entity -> domain")
    void shouldMapAllFieldsCorrectlyEntityToDomain() {
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 16, 11, 0);
        LocalDateTime deletedAt = LocalDateTime.of(2024, 1, 17, 12, 0);
        entityDocument.setCreatedAt(createdAt);
        entityDocument.setUpdatedAt(updatedAt);
        entityDocument.setDeletedAt(deletedAt);
        entityDocument.setActive(false);

        when(jpaRepository.findById(entityDocument.getId())).thenReturn(Optional.of(entityDocument));

        Optional<MedicalDocument> result = repository.findById(entityDocument.getId());

        assertTrue(result.isPresent());
        assertEquals(createdAt, result.get().getCreatedAt());
        assertEquals(updatedAt, result.get().getUpdatedAt());
        assertEquals(deletedAt, result.get().getDeletedAt());
        assertFalse(result.get().getActive());
    }
}
