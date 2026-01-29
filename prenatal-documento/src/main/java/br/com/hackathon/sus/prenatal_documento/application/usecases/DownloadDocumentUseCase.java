package br.com.hackathon.sus.prenatal_documento.application.usecases;

import java.util.UUID;

public interface DownloadDocumentUseCase {
    byte[] download(UUID documentId);
    String getContentType(UUID documentId);
    String getFileName(UUID documentId);
}

