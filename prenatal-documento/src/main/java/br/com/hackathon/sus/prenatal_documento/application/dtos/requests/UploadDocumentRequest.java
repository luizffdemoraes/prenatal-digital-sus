package br.com.hackathon.sus.prenatal_documento.application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record UploadDocumentRequest(
        @JsonProperty("tipoDocumento")
        @NotBlank(message = "{document.tipoDocumento.required}")
        String documentType
) {
}
