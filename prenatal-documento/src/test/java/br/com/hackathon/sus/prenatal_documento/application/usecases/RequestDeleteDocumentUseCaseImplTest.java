package br.com.hackathon.sus.prenatal_documento.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do RequestDeleteDocumentUseCaseImpl")
class RequestDeleteDocumentUseCaseImplTest {

    @Mock
    private InactivateDocumentUseCase inactivateDocumentUseCase;

    @InjectMocks
    private RequestDeleteDocumentUseCaseImpl useCase;

    private static final UUID DOCUMENT_ID = UUID.randomUUID();

    @Test
    @DisplayName("Deve chamar inativação ao solicitar deleção")
    void shouldCallInactivateWhenRequestingDelete() {
        useCase.requestDelete(DOCUMENT_ID);

        verify(inactivateDocumentUseCase, times(1)).inactivate(DOCUMENT_ID);
    }
}
