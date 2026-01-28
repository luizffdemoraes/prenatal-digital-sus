package com.hackathon.sus.prenatal_documento.domain.models;

import com.hackathon.sus.prenatal_documento.domain.enums.DocumentType;

import java.time.LocalDateTime;
import java.util.UUID;

public class MedicalDocument {
    private UUID id;
    private Long prenatalRecordId;
    private String fileName;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private DocumentType documentType;
    private String storagePath;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public MedicalDocument() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public MedicalDocument(Long prenatalRecordId, String fileName, String originalFileName,
                          String contentType, Long fileSize, DocumentType documentType, String storagePath) {
        this();
        this.prenatalRecordId = prenatalRecordId;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.documentType = documentType;
        this.storagePath = storagePath;
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

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void inactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
