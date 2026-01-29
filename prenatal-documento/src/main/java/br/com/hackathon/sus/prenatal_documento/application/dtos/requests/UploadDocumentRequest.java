package br.com.hackathon.sus.prenatal_documento.application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UploadDocumentRequest {
    @NotNull(message = "Arquivo é obrigatório")
    private String file;

    @NotBlank(message = "Tipo de documento é obrigatório")
    private String documentType;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
}
