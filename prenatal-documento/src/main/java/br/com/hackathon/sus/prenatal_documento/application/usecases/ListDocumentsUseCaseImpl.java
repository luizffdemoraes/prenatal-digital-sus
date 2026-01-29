package br.com.hackathon.sus.prenatal_documento.application.usecases;

import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import br.com.hackathon.sus.prenatal_documento.domain.repositories.MedicalDocumentRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ListDocumentsUseCaseImpl implements ListDocumentsUseCase {

    private final MedicalDocumentRepository repository;

    public ListDocumentsUseCaseImpl(MedicalDocumentRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalDocument> listActiveByPrenatalRecord(Long prenatalRecordId) {
        return repository.findByPrenatalRecordIdAndActiveTrue(prenatalRecordId);
    }
}

