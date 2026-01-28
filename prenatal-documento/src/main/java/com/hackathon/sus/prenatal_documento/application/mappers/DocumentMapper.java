package com.hackathon.sus.prenatal_documento.application.mappers;

import com.hackathon.sus.prenatal_documento.application.dtos.responses.DocumentResponse;
import com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {
    
    public DocumentResponse toResponse(MedicalDocument document) {
        return new DocumentResponse(document);
    }
}
