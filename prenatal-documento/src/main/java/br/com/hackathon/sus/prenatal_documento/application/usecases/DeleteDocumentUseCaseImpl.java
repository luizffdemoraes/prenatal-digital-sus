package br.com.hackathon.sus.prenatal_documento.application.usecases;

import br.com.hackathon.sus.prenatal_documento.domain.gateways.StorageGateway;
import br.com.hackathon.sus.prenatal_documento.domain.exceptions.DocumentNotFoundException;
import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import br.com.hackathon.sus.prenatal_documento.domain.repositories.MedicalDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class DeleteDocumentUseCaseImpl implements DeleteDocumentUseCase {

    private static final Logger logger = LoggerFactory.getLogger(DeleteDocumentUseCaseImpl.class);
    private static final String DOCUMENTO_NAO_ENCONTRADO = "Documento não encontrado: ";

    private final MedicalDocumentRepository repository;
    private final StorageGateway storagePort;

    public DeleteDocumentUseCaseImpl(MedicalDocumentRepository repository, StorageGateway storagePort) {
        this.repository = repository;
        this.storagePort = storagePort;
    }

    @Override
    @Transactional
    public void deletePermanently(UUID documentId) {
        MedicalDocument document = repository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(DOCUMENTO_NAO_ENCONTRADO + documentId));

        storagePort.delete(document.getStoragePath());
        repository.delete(document);

        logger.info("Documento deletado permanentemente: {}", documentId);
    }
}

