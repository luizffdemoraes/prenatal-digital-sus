package com.hackathon.sus.prenatal_documento.domain.ports.inbound;

import java.util.UUID;

public interface DownloadDocumentUseCase {
    byte[] download(UUID documentId);
    String getContentType(UUID documentId);
    String getFileName(UUID documentId);
}
