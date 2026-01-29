package br.com.hackathon.sus.prenatal_documento.application.usecases;

import br.com.hackathon.sus.prenatal_documento.domain.gateways.StorageGateway;
import br.com.hackathon.sus.prenatal_documento.domain.exceptions.DocumentNotFoundException;
import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import br.com.hackathon.sus.prenatal_documento.domain.repositories.MedicalDocumentRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class DownloadDocumentUseCaseImpl implements DownloadDocumentUseCase {

    private static final String DOCUMENTO_NAO_ENCONTRADO = "Documento não encontrado: ";

    private final MedicalDocumentRepository repository;
    private final StorageGateway storagePort;

    public DownloadDocumentUseCaseImpl(MedicalDocumentRepository repository, StorageGateway storagePort) {
        this.repository = repository;
        this.storagePort = storagePort;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] download(UUID documentId) {
        MedicalDocument document = findActiveDocument(documentId);
        return storagePort.download(document.getStoragePath());
    }

    @Override
    @Transactional(readOnly = true)
    public String getContentType(UUID documentId) {
        return findDocument(documentId).getContentType();
    }

    @Override
    @Transactional(readOnly = true)
    public String getFileName(UUID documentId) {
        return findDocument(documentId).getOriginalFileName();
    }

    private MedicalDocument findActiveDocument(UUID documentId) {
        MedicalDocument document = findDocument(documentId);
        if (!Boolean.TRUE.equals(document.getActive())) {
            throw new IllegalStateException("Documento inativo");
        }
        return document;
    }

    private MedicalDocument findDocument(UUID documentId) {
        return repository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(DOCUMENTO_NAO_ENCONTRADO + documentId));
    }
}

