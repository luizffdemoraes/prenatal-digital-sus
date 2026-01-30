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
@DisplayName("Testes do DownloadDocumentUseCaseImpl")
class DownloadDocumentUseCaseImplTest {

    @Mock
    private MedicalDocumentRepository repository;

    @Mock
    private StorageGateway storageGateway;

    @InjectMocks
    private DownloadDocumentUseCaseImpl downloadUseCase;

    private static final UUID DOCUMENT_ID = UUID.randomUUID();
    private static final String PATIENT_CPF = "12345678900";
    private static final String FILE_NAME = "exame.pdf";
    private static final String CONTENT_TYPE = "application/pdf";
    private static final String STORAGE_PATH = "prenatal-records/12345678900/exame.pdf";

    @Test
    @DisplayName("Deve fazer download de documento ativo com sucesso")
    void shouldDownloadActiveDocumentSuccessfully() {
        // Arrange
        MedicalDocument document = createActiveDocument();
        byte[] expectedContent = "PDF content".getBytes();
        
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        when(storageGateway.download(STORAGE_PATH)).thenReturn(expectedContent);

        // Act
        byte[] result = downloadUseCase.download(DOCUMENT_ID);

        // Assert
        assertNotNull(result);
        assertArrayEquals(expectedContent, result);
        
        verify(repository, times(1)).findById(DOCUMENT_ID);
        verify(storageGateway, times(1)).download(STORAGE_PATH);
    }

    @Test
    @DisplayName("Deve lançar exceção quando documento não existe")
    void shouldThrowExceptionWhenDocumentNotFound() {
        // Arrange
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        DocumentNotFoundException exception = assertThrows(
                DocumentNotFoundException.class,
                () -> downloadUseCase.download(DOCUMENT_ID)
        );
        
        assertTrue(exception.getMessage().contains("Documento não encontrado"));
        verify(repository, times(1)).findById(DOCUMENT_ID);
        verify(storageGateway, never()).download(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando documento está inativo")
    void shouldThrowExceptionWhenDocumentIsInactive() {
        // Arrange
        MedicalDocument document = createActiveDocument();
        document.setActive(false);
        
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> downloadUseCase.download(DOCUMENT_ID)
        );
        
        assertEquals("Documento inativo", exception.getMessage());
        verify(repository, times(1)).findById(DOCUMENT_ID);
        verify(storageGateway, never()).download(anyString());
    }

    @Test
    @DisplayName("Deve obter content type do documento")
    void shouldGetContentType() {
        // Arrange
        MedicalDocument document = createActiveDocument();
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));

        // Act
        String result = downloadUseCase.getContentType(DOCUMENT_ID);

        // Assert
        assertEquals(CONTENT_TYPE, result);
        verify(repository, times(1)).findById(DOCUMENT_ID);
    }

    @Test
    @DisplayName("Deve obter nome do arquivo")
    void shouldGetFileName() {
        // Arrange
        MedicalDocument document = createActiveDocument();
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));

        // Act
        String result = downloadUseCase.getFileName(DOCUMENT_ID);

        // Assert
        assertEquals(FILE_NAME, result);
        verify(repository, times(1)).findById(DOCUMENT_ID);
    }

    @Test
    @DisplayName("Deve lançar exceção ao obter content type de documento inexistente")
    void shouldThrowExceptionWhenGettingContentTypeOfNonExistentDocument() {
        // Arrange
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                DocumentNotFoundException.class,
                () -> downloadUseCase.getContentType(DOCUMENT_ID)
        );
        
        verify(repository, times(1)).findById(DOCUMENT_ID);
    }

    @Test
    @DisplayName("Deve lançar exceção ao obter nome de arquivo de documento inexistente")
    void shouldThrowExceptionWhenGettingFileNameOfNonExistentDocument() {
        // Arrange
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                DocumentNotFoundException.class,
                () -> downloadUseCase.getFileName(DOCUMENT_ID)
        );
        
        verify(repository, times(1)).findById(DOCUMENT_ID);
    }

    private MedicalDocument createActiveDocument() {
        MedicalDocument document = new MedicalDocument(
                PATIENT_CPF,
                FILE_NAME,
                FILE_NAME,
                CONTENT_TYPE,
                1024L,
                DocumentType.EXAM,
                STORAGE_PATH
        );
        document.setId(DOCUMENT_ID);
        document.setActive(true);
        return document;
    }
}
