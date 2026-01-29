package br.com.hackathon.sus.prenatal_documento.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class RequestDeleteDocumentUseCaseImpl implements RequestDeleteDocumentUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RequestDeleteDocumentUseCaseImpl.class);

    private final InactivateDocumentUseCase inactivateDocumentUseCase;

    public RequestDeleteDocumentUseCaseImpl(InactivateDocumentUseCase inactivateDocumentUseCase) {
        this.inactivateDocumentUseCase = inactivateDocumentUseCase;
    }

    @Override
    @Transactional
    public void requestDelete(UUID documentId) {
        // Por enquanto, apenas inativa. Em uma implementação completa,
        // isso criaria uma solicitação para aprovação de um profissional
        inactivateDocumentUseCase.inactivate(documentId);
        logger.info("Solicitação de exclusão criada para documento: {}", documentId);
    }
}

