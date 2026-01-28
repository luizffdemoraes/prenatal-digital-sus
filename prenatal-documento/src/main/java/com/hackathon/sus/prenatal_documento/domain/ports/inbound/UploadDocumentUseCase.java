package com.hackathon.sus.prenatal_documento.domain.ports.inbound;

import com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import org.springframework.web.multipart.MultipartFile;

public interface UploadDocumentUseCase {
    MedicalDocument upload(Long prenatalRecordId, MultipartFile file, String documentType);
}
