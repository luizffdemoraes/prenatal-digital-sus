package br.com.hackathon.sus.prenatal_documento.application.usecases;

import br.com.hackathon.sus.prenatal_documento.domain.exceptions.DocumentNotFoundException;
import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import br.com.hackathon.sus.prenatal_documento.domain.repositories.MedicalDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class InactivateDocumentUseCaseImpl implements InactivateDocumentUseCase {

    private static final Logger logger = LoggerFactory.getLogger(InactivateDocumentUseCaseImpl.class);
    private static final String DOCUMENTO_NAO_ENCONTRADO = "Documento não encontrado: ";

    private final MedicalDocumentRepository repository;

    public InactivateDocumentUseCaseImpl(MedicalDocumentRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void inactivate(UUID documentId) {
        MedicalDocument document = repository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(DOCUMENTO_NAO_ENCONTRADO + documentId));
        document.inactivate();
        repository.save(document);
        logger.info("Documento inativado: {}", documentId);
    }
}

