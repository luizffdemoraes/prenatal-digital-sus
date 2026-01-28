package com.hackathon.sus.prenatal_documento.domain.ports.inbound;

import java.util.UUID;

public interface DeleteDocumentUseCase {
    void deletePermanently(UUID documentId);
}
