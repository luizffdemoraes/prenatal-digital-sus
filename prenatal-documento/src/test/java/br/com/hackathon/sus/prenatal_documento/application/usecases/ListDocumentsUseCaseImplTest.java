package br.com.hackathon.sus.prenatal_documento.application.usecases;

import br.com.hackathon.sus.prenatal_documento.domain.enums.DocumentType;
import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import br.com.hackathon.sus.prenatal_documento.domain.repositories.MedicalDocumentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ListDocumentsUseCaseImpl")
class ListDocumentsUseCaseImplTest {

    @Mock
    private MedicalDocumentRepository repository;

    @InjectMocks
    private ListDocumentsUseCaseImpl listUseCase;

    private static final String PATIENT_CPF = "12345678900";

    @Test
    @DisplayName("Deve listar documentos ativos do paciente")
    void shouldListActiveDocumentsByPatientCpf() {
        // Arrange
        MedicalDocument doc1 = createDocument("exame1.pdf", DocumentType.EXAM);
        MedicalDocument doc2 = createDocument("ultrassom1.pdf", DocumentType.ULTRASOUND);
        
        List<MedicalDocument> expectedDocuments = Arrays.asList(doc1, doc2);
        when(repository.findByPatientCpfAndActiveTrue(PATIENT_CPF)).thenReturn(expectedDocuments);

        // Act
        List<MedicalDocument> result = listUseCase.listActiveByPatientCpf(PATIENT_CPF);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(doc1.getId(), result.get(0).getId());
        assertEquals(doc2.getId(), result.get(1).getId());
        
        verify(repository, times(1)).findByPatientCpfAndActiveTrue(PATIENT_CPF);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há documentos")
    void shouldReturnEmptyListWhenNoDocuments() {
        // Arrange
        when(repository.findByPatientCpfAndActiveTrue(PATIENT_CPF)).thenReturn(List.of());

        // Act
        List<MedicalDocument> result = listUseCase.listActiveByPatientCpf(PATIENT_CPF);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(repository, times(1)).findByPatientCpfAndActiveTrue(PATIENT_CPF);
    }

    @Test
    @DisplayName("Deve retornar apenas documentos ativos")
    void shouldReturnOnlyActiveDocuments() {
        // Arrange
        MedicalDocument activeDoc = createDocument("exame1.pdf", DocumentType.EXAM);
        activeDoc.setActive(true);
        
        List<MedicalDocument> expectedDocuments = List.of(activeDoc);
        when(repository.findByPatientCpfAndActiveTrue(PATIENT_CPF)).thenReturn(expectedDocuments);

        // Act
        List<MedicalDocument> result = listUseCase.listActiveByPatientCpf(PATIENT_CPF);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getActive());
        
        verify(repository, times(1)).findByPatientCpfAndActiveTrue(PATIENT_CPF);
    }

    private MedicalDocument createDocument(String fileName, DocumentType type) {
        MedicalDocument document = new MedicalDocument(
                PATIENT_CPF,
                fileName,
                fileName,
                "application/pdf",
                1024L,
                type,
                "prenatal-records/" + PATIENT_CPF + "/" + fileName
        );
        document.setId(UUID.randomUUID());
        document.setActive(true);
        return document;
    }
}
