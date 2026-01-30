package br.com.hackathon.sus.prenatal_documento.application.usecases;

import br.com.hackathon.sus.prenatal_documento.domain.enums.DocumentType;
import br.com.hackathon.sus.prenatal_documento.domain.exceptions.DocumentNotFoundException;
import br.com.hackathon.sus.prenatal_documento.domain.gateways.StorageGateway;
import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import br.com.hackathon.sus.prenatal_documento.domain.repositories.MedicalDocumentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do DeleteDocumentUseCaseImpl")
class DeleteDocumentUseCaseImplTest {

    @Mock
    private MedicalDocumentRepository repository;

    @Mock
    private StorageGateway storageGateway;

    @InjectMocks
    private DeleteDocumentUseCaseImpl deleteUseCase;

    private static final UUID DOCUMENT_ID = UUID.randomUUID();
    private static final String PATIENT_CPF = "12345678900";
    private static final String STORAGE_PATH = "prenatal-records/12345678900/exame.pdf";

    @Test
    @DisplayName("Deve deletar documento permanentemente com sucesso")
    void shouldDeleteDocumentPermanentlySuccessfully() {
        // Arrange
        MedicalDocument document = createDocument();
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        doNothing().when(storageGateway).delete(STORAGE_PATH);
        doNothing().when(repository).delete(document);

        // Act
        deleteUseCase.deletePermanently(DOCUMENT_ID);

        // Assert
        verify(repository, times(1)).findById(DOCUMENT_ID);
        verify(storageGateway, times(1)).delete(STORAGE_PATH);
        verify(repository, times(1)).delete(document);
    }

    @Test
    @DisplayName("Deve lançar exceção quando documento não existe")
    void shouldThrowExceptionWhenDocumentNotFound() {
        // Arrange
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        DocumentNotFoundException exception = assertThrows(
                DocumentNotFoundException.class,
                () -> deleteUseCase.deletePermanently(DOCUMENT_ID)
        );
        
        assertTrue(exception.getMessage().contains("Documento não encontrado"));
        verify(repository, times(1)).findById(DOCUMENT_ID);
        verify(storageGateway, never()).delete(anyString());
        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve deletar documento mesmo se estiver inativo")
    void shouldDeleteDocumentEvenIfInactive() {
        // Arrange
        MedicalDocument document = createDocument();
        document.setActive(false);
        
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        doNothing().when(storageGateway).delete(STORAGE_PATH);
        doNothing().when(repository).delete(document);

        // Act
        deleteUseCase.deletePermanently(DOCUMENT_ID);

        // Assert
        verify(repository, times(1)).findById(DOCUMENT_ID);
        verify(storageGateway, times(1)).delete(STORAGE_PATH);
        verify(repository, times(1)).delete(document);
    }

    @Test
    @DisplayName("Deve propagar exceção se falhar ao deletar do storage")
    void shouldPropagateExceptionIfStorageDeletionFails() {
        // Arrange
        MedicalDocument document = createDocument();
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        doThrow(new RuntimeException("Erro ao deletar do S3")).when(storageGateway).delete(STORAGE_PATH);

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> deleteUseCase.deletePermanently(DOCUMENT_ID)
        );
        
        assertEquals("Erro ao deletar do S3", exception.getMessage());
        verify(repository, times(1)).findById(DOCUMENT_ID);
        verify(storageGateway, times(1)).delete(STORAGE_PATH);
        verify(repository, never()).delete(any());
    }

    private MedicalDocument createDocument() {
        MedicalDocument document = new MedicalDocument(
                PATIENT_CPF,
                "exame.pdf",
                "exame.pdf",
                "application/pdf",
                1024L,
                DocumentType.EXAM,
                STORAGE_PATH
        );
        document.setId(DOCUMENT_ID);
        document.setActive(true);
        return document;
    }
}
