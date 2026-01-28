package com.hackathon.sus.prenatal_documento.application.services;

import com.hackathon.sus.prenatal_documento.domain.enums.DocumentType;
import com.hackathon.sus.prenatal_documento.domain.exceptions.DocumentNotFoundException;
import com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import com.hackathon.sus.prenatal_documento.domain.ports.inbound.*;
import com.hackathon.sus.prenatal_documento.domain.ports.outbound.StoragePort;
import com.hackathon.sus.prenatal_documento.domain.repositories.MedicalDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService implements UploadDocumentUseCase, DownloadDocumentUseCase,
        ListDocumentsUseCase, InactivateDocumentUseCase, DeleteDocumentUseCase, RequestDeleteDocumentUseCase {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    private final MedicalDocumentRepository repository;
    private final StoragePort storagePort;

    public DocumentService(MedicalDocumentRepository repository, StoragePort storagePort) {
        this.repository = repository;
        this.storagePort = storagePort;
    }

    @Override
    @Transactional
    public MedicalDocument upload(Long prenatalRecordId, MultipartFile file, String documentType) {
        validateFile(file);

        try {
            DocumentType type = DocumentType.valueOf(documentType.toUpperCase());
            String storagePath = generateStoragePath(prenatalRecordId, file.getOriginalFilename());
            
            // Upload para S3
            String uploadedPath = storagePort.upload(
                    storagePath,
                    file.getInputStream(),
                    file.getContentType(),
                    file.getSize()
            );

            // Salva metadados no banco
            MedicalDocument document = new MedicalDocument(
                    prenatalRecordId,
                    extractFileName(storagePath),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    type,
                    uploadedPath
            );

            MedicalDocument saved = repository.save(document);
            logger.info("Documento salvo com sucesso: {}", saved.getId());
            return saved;

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de documento inválido: " + documentType);
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo", e);
            throw new RuntimeException("Erro ao processar arquivo", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] download(UUID documentId) {
        MedicalDocument document = repository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Documento não encontrado: " + documentId));

        if (!document.getActive()) {
            throw new IllegalStateException("Documento inativo");
        }

        return storagePort.download(document.getStoragePath());
    }

    @Override
    @Transactional(readOnly = true)
    public String getContentType(UUID documentId) {
        MedicalDocument document = repository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Documento não encontrado: " + documentId));
        return document.getContentType();
    }

    @Override
    @Transactional(readOnly = true)
    public String getFileName(UUID documentId) {
        MedicalDocument document = repository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Documento não encontrado: " + documentId));
        return document.getOriginalFileName();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalDocument> listActiveByPrenatalRecord(Long prenatalRecordId) {
        return repository.findByPrenatalRecordIdAndActiveTrue(prenatalRecordId);
    }

    @Override
    @Transactional
    public void inactivate(UUID documentId) {
        MedicalDocument document = repository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Documento não encontrado: " + documentId));
        document.inactivate();
        repository.save(document);
        logger.info("Documento inativado: {}", documentId);
    }

    @Override
    @Transactional
    public void deletePermanently(UUID documentId) {
        MedicalDocument document = repository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Documento não encontrado: " + documentId));
        
        // Deleta do S3
        storagePort.delete(document.getStoragePath());
        
        // Deleta do banco
        repository.delete(document);
        logger.info("Documento deletado permanentemente: {}", documentId);
    }

    @Override
    @Transactional
    public void requestDelete(UUID documentId) {
        // Por enquanto, apenas inativa. Em uma implementação completa,
        // isso criaria uma solicitação para aprovação de um profissional
        inactivate(documentId);
        logger.info("Solicitação de exclusão criada para documento: {}", documentId);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode ser vazio");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Arquivo excede o tamanho máximo de 10MB");
        }

        if (!PDF_CONTENT_TYPE.equals(file.getContentType())) {
            throw new IllegalArgumentException("Apenas arquivos PDF são permitidos");
        }
    }

    private String generateStoragePath(Long prenatalRecordId, String originalFileName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;
        return String.format("prenatal-records/%d/%s/%s", prenatalRecordId, timestamp, fileName);
    }

    private String extractFileName(String storagePath) {
        return storagePath.substring(storagePath.lastIndexOf("/") + 1);
    }
}
