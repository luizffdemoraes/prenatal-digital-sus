package br.com.hackathon.sus.prenatal_alertas.domain.entities;

import java.time.LocalDateTime;

public class DocumentSummary {
    private String id;
    private String documentType;
    private String examSubType;
    private LocalDateTime createdAt;

    public DocumentSummary() {
    }

    public DocumentSummary(String id, String documentType, String examSubType, LocalDateTime createdAt) {
        this.id = id;
        this.documentType = documentType;
        this.examSubType = examSubType;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getExamSubType() { return examSubType; }
    public void setExamSubType(String examSubType) { this.examSubType = examSubType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
