package br.com.hackathon.sus.prenatal_documento.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_documento.application.dtos.responses.DocumentResponse;
import br.com.hackathon.sus.prenatal_documento.application.usecases.*;
import br.com.hackathon.sus.prenatal_documento.infrastructure.config.mapper.DocumentMapper;
import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private final UploadDocumentUseCase uploadUseCase;
    private final DownloadDocumentUseCase downloadUseCase;
    private final ListDocumentsUseCase listUseCase;
    private final InactivateDocumentUseCase inactivateUseCase;
    private final DeleteDocumentUseCase deleteUseCase;
    private final RequestDeleteDocumentUseCase requestDeleteUseCase;
    private final DocumentMapper mapper;

    public DocumentController(
            UploadDocumentUseCase uploadUseCase,
            DownloadDocumentUseCase downloadUseCase,
            ListDocumentsUseCase listUseCase,
            InactivateDocumentUseCase inactivateUseCase,
            DeleteDocumentUseCase deleteUseCase,
            RequestDeleteDocumentUseCase requestDeleteUseCase,
            DocumentMapper mapper) {
        this.uploadUseCase = uploadUseCase;
        this.downloadUseCase = downloadUseCase;
        this.listUseCase = listUseCase;
        this.inactivateUseCase = inactivateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.requestDeleteUseCase = requestDeleteUseCase;
        this.mapper = mapper;
    }

    @PostMapping(value = "/prenatal-records/{cpf}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('PATIENT', 'NURSE', 'DOCTOR')")
    public ResponseEntity<DocumentResponse> upload(
            @PathVariable String cpf,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") @NotBlank String documentType) {

        MedicalDocument document = uploadUseCase.upload(cpf, file, documentType);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(document));
    }

    @GetMapping("/prenatal-records/{cpf}/documents")
    @PreAuthorize("hasAnyRole('PATIENT', 'NURSE', 'DOCTOR')")
    public ResponseEntity<List<DocumentResponse>> list(@PathVariable String cpf) {
        List<MedicalDocument> documents = listUseCase.listActiveByPatientCpf(cpf);
        List<DocumentResponse> responses = documents.stream()
                .map(mapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/documents/{id}/download")
    @PreAuthorize("hasAnyRole('PATIENT', 'NURSE', 'DOCTOR')")
    public ResponseEntity<byte[]> download(@PathVariable UUID id) {
        byte[] fileContent = downloadUseCase.download(id);
        String contentType = downloadUseCase.getContentType(id);
        String fileName = downloadUseCase.getFileName(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentLength(fileContent.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }

    @PatchMapping("/documents/{id}/inactivate")
    @PreAuthorize("hasAnyRole('NURSE', 'DOCTOR')")
    public ResponseEntity<Void> inactivate(@PathVariable UUID id) {
        inactivateUseCase.inactivate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/documents/{id}/permanent")
    @PreAuthorize("hasAnyRole('NURSE', 'DOCTOR')")
    public ResponseEntity<Void> deletePermanently(@PathVariable UUID id) {
        deleteUseCase.deletePermanently(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/documents/{id}/request-delete")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Void> requestDelete(@PathVariable UUID id) {
        requestDeleteUseCase.requestDelete(id);
        return ResponseEntity.accepted().build();
    }
}
