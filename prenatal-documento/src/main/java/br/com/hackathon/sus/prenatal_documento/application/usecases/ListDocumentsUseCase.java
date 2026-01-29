package br.com.hackathon.sus.prenatal_documento.application.usecases;

import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;

import java.util.List;

public interface ListDocumentsUseCase {
    List<MedicalDocument> listActiveByPrenatalRecord(Long prenatalRecordId);
}

