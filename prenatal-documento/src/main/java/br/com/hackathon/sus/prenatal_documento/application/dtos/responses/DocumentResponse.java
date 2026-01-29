package br.com.hackathon.sus.prenatal_documento.application.dtos.responses;

import br.com.hackathon.sus.prenatal_documento.domain.enums.DocumentType;
import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("gestanteCpf") String patientCpf,
        @JsonProperty("nomeArquivo") String fileName,
        @JsonProperty("nomeArquivoOriginal") String originalFileName,
        @JsonProperty("tipoConteudo") String contentType,
        @JsonProperty("tamanhoArquivo") Long fileSize,
        @JsonProperty("tipoDocumento") DocumentType documentType,
        @JsonProperty("ativo") Boolean active,
        @JsonProperty("dataCriacao") LocalDateTime createdAt,
        @JsonProperty("dataAtualizacao") LocalDateTime updatedAt
) {
    public static DocumentResponse from(MedicalDocument document) {
        return new DocumentResponse(
                document.getId(),
                document.getPatientCpf(),
                document.getFileName(),
                document.getOriginalFileName(),
                document.getContentType(),
                document.getFileSize(),
                document.getDocumentType(),
                document.getActive(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}
