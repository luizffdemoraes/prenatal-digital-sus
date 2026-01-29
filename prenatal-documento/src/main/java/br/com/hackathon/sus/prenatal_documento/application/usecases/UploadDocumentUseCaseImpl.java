package br.com.hackathon.sus.prenatal_documento.application.usecases;

import br.com.hackathon.sus.prenatal_documento.domain.enums.DocumentType;
import br.com.hackathon.sus.prenatal_documento.domain.gateways.StorageGateway;
import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import br.com.hackathon.sus.prenatal_documento.domain.repositories.MedicalDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public class UploadDocumentUseCaseImpl implements UploadDocumentUseCase {

    private static final Logger logger = LoggerFactory.getLogger(UploadDocumentUseCaseImpl.class);
    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024; // 10MB
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    private final MedicalDocumentRepository repository;
    private final StorageGateway storagePort;

    public UploadDocumentUseCaseImpl(MedicalDocumentRepository repository, StorageGateway storagePort) {
        this.repository = repository;
        this.storagePort = storagePort;
    }

    @Override
    @Transactional
    public MedicalDocument upload(String patientCpf, MultipartFile file, String documentType) {
        validateFile(file);

        try {
            DocumentType type = DocumentType.valueOf(documentType.toUpperCase());
            String storagePath = generateStoragePath(patientCpf, file.getOriginalFilename());

            String uploadedPath = storagePort.upload(
                    storagePath,
                    file.getInputStream(),
                    file.getContentType(),
                    file.getSize()
            );

            MedicalDocument document = new MedicalDocument(
                    patientCpf,
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
            throw new IllegalArgumentException("Tipo de documento inválido: " + documentType, e);
        } catch (IOException e) {
            logger.error("Erro ao processar arquivo", e);
            throw new RuntimeException("Erro ao processar arquivo", e);
        }
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

    private String generateStoragePath(String patientCpf, String originalFileName) {
        String safeFileName = originalFileName != null ? originalFileName : "arquivo.pdf";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String extension = safeFileName.contains(".")
                ? safeFileName.substring(safeFileName.lastIndexOf("."))
                : "";
        String fileName = java.util.UUID.randomUUID() + extension;
        return String.format("prenatal-records/%s/%s/%s", patientCpf, timestamp, fileName);
    }

    private String extractFileName(String storagePath) {
        return storagePath.substring(storagePath.lastIndexOf("/") + 1);
    }
}

