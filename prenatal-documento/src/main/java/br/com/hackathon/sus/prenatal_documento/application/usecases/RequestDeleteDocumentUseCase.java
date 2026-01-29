package br.com.hackathon.sus.prenatal_documento.application.usecases;

import java.util.UUID;

public interface RequestDeleteDocumentUseCase {
    void requestDelete(UUID documentId);
}

