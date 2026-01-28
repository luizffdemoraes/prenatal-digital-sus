package com.hackathon.sus.prenatal_documento.domain.ports.inbound;

import com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;

import java.util.List;

public interface ListDocumentsUseCase {
    List<MedicalDocument> listActiveByPrenatalRecord(Long prenatalRecordId);
}
