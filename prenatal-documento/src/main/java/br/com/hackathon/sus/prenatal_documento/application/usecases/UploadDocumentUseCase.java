package br.com.hackathon.sus.prenatal_documento.application.usecases;

import org.springframework.web.multipart.MultipartFile;

import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;

public interface UploadDocumentUseCase {
    MedicalDocument upload(String patientCpf, MultipartFile file, String documentType, String examType);
}

