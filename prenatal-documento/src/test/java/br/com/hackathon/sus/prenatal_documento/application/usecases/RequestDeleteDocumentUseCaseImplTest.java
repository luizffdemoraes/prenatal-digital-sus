package br.com.hackathon.sus.prenatal_documento.application.usecases;

import br.com.hackathon.sus.prenatal_documento.domain.exceptions.DocumentNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do RequestDeleteDocumentUseCaseImpl")
class RequestDeleteDocumentUseCaseImplTest {

    @Mock
    private InactivateDocumentUseCase inactivateDocumentUseCase;

    @InjectMocks
    private RequestDeleteDocumentUseCaseImpl requestDeleteUseCase;

    private static final UUID DOCUMENT_ID = UUID.randomUUID();

    @Test
    @DisplayName("Deve solicitar deleção de documento com sucesso")
    void shouldRequestDeleteSuccessfully() {
        // Arrange
        doNothing().when(inactivateDocumentUseCase).inactivate(DOCUMENT_ID);

        // Act
        requestDeleteUseCase.requestDelete(DOCUMENT_ID);

        // Assert
        verify(inactivateDocumentUseCase, times(1)).inactivate(DOCUMENT_ID);
    }

    @Test
    @DisplayName("Deve propagar exceção quando documento não existe")
    void shouldPropagateExceptionWhenDocumentNotFound() {
        // Arrange
        doThrow(new DocumentNotFoundException("Documento não encontrado"))
                .when(inactivateDocumentUseCase).inactivate(DOCUMENT_ID);

        // Act & Assert
        assertThrows(
                DocumentNotFoundException.class,
                () -> requestDeleteUseCase.requestDelete(DOCUMENT_ID)
        );
        
        verify(inactivateDocumentUseCase, times(1)).inactivate(DOCUMENT_ID);
    }

    @Test
    @DisplayName("Deve chamar inactivateDocumentUseCase corretamente")
    void shouldCallInactivateDocumentUseCaseCorrectly() {
        // Arrange
        UUID specificDocumentId = UUID.randomUUID();
        doNothing().when(inactivateDocumentUseCase).inactivate(specificDocumentId);

        // Act
        requestDeleteUseCase.requestDelete(specificDocumentId);

        // Assert
        verify(inactivateDocumentUseCase, times(1)).inactivate(eq(specificDocumentId));
    }
}
