package com.hackathon.sus.prenatal_documento.application.dtos.responses;

import com.hackathon.sus.prenatal_documento.domain.enums.DocumentType;
import com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;

import java.time.LocalDateTime;
import java.util.UUID;

public class DocumentResponse {
    private UUID id;
    private Long prenatalRecordId;
    private String fileName;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private DocumentType documentType;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DocumentResponse() {
    }

    public DocumentResponse(MedicalDocument document) {
        this.id = document.getId();
        this.prenatalRecordId = document.getPrenatalRecordId();
        this.fileName = document.getFileName();
        this.originalFileName = document.getOriginalFileName();
        this.contentType = document.getContentType();
        this.fileSize = document.getFileSize();
        this.documentType = document.getDocumentType();
        this.active = document.getActive();
        this.createdAt = document.getCreatedAt();
        this.updatedAt = document.getUpdatedAt();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getPrenatalRecordId() {
        return prenatalRecordId;
    }

    public void setPrenatalRecordId(Long prenatalRecordId) {
        this.prenatalRecordId = prenatalRecordId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
