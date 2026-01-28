package com.hackathon.sus.prenatal_documento.domain.ports.inbound;

import java.util.UUID;

public interface InactivateDocumentUseCase {
    void inactivate(UUID documentId);
}
