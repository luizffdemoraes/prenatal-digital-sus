package br.com.hackathon.sus.prenatal_documento.application.usecases;

import br.com.hackathon.sus.prenatal_documento.domain.enums.DocumentType;
import br.com.hackathon.sus.prenatal_documento.domain.exceptions.DocumentNotFoundException;
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
@DisplayName("Testes do InactivateDocumentUseCaseImpl")
class InactivateDocumentUseCaseImplTest {

    @Mock
    private MedicalDocumentRepository repository;

    @InjectMocks
    private InactivateDocumentUseCaseImpl inactivateUseCase;

    private static final UUID DOCUMENT_ID = UUID.randomUUID();
    private static final String PATIENT_CPF = "12345678900";

    @Test
    @DisplayName("Deve inativar documento com sucesso")
    void shouldInactivateDocumentSuccessfully() {
        // Arrange
        MedicalDocument document = createActiveDocument();
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        when(repository.save(any(MedicalDocument.class))).thenReturn(document);

        // Act
        inactivateUseCase.inactivate(DOCUMENT_ID);

        // Assert
        assertFalse(document.getActive());
        assertNotNull(document.getUpdatedAt());
        
        verify(repository, times(1)).findById(DOCUMENT_ID);
        verify(repository, times(1)).save(document);
    }

    @Test
    @DisplayName("Deve lançar exceção quando documento não existe")
    void shouldThrowExceptionWhenDocumentNotFound() {
        // Arrange
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        DocumentNotFoundException exception = assertThrows(
                DocumentNotFoundException.class,
                () -> inactivateUseCase.inactivate(DOCUMENT_ID)
        );
        
        assertTrue(exception.getMessage().contains("Documento não encontrado"));
        verify(repository, times(1)).findById(DOCUMENT_ID);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve inativar documento já inativo")
    void shouldInactivateAlreadyInactiveDocument() {
        // Arrange
        MedicalDocument document = createActiveDocument();
        document.setActive(false);
        
        when(repository.findById(DOCUMENT_ID)).thenReturn(Optional.of(document));
        when(repository.save(any(MedicalDocument.class))).thenReturn(document);

        // Act
        inactivateUseCase.inactivate(DOCUMENT_ID);

        // Assert
        assertFalse(document.getActive());
        
        verify(repository, times(1)).findById(DOCUMENT_ID);
        verify(repository, times(1)).save(document);
    }

    private MedicalDocument createActiveDocument() {
        MedicalDocument document = new MedicalDocument(
                PATIENT_CPF,
                "exame.pdf",
                "exame.pdf",
                "application/pdf",
                1024L,
                DocumentType.EXAM,
                "prenatal-records/12345678900/exame.pdf"
        );
        document.setId(DOCUMENT_ID);
        document.setActive(true);
        return document;
    }
}
