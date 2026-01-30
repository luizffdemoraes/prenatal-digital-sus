package br.com.hackathon.sus.prenatal_documento.domain.models;

import br.com.hackathon.sus.prenatal_documento.domain.enums.DocumentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do MedicalDocument")
class MedicalDocumentTest {

    private static final String PATIENT_CPF = "12345678900";
    private static final String FILE_NAME = "exame.pdf";
    private static final String ORIGINAL_FILE_NAME = "exame_original.pdf";
    private static final String CONTENT_TYPE = "application/pdf";
    private static final Long FILE_SIZE = 1024L;
    private static final String STORAGE_PATH = "prenatal-records/12345678900/exame.pdf";

    @Test
    @DisplayName("Deve criar documento com construtor padrão")
    void shouldCreateDocumentWithDefaultConstructor() {
        // Act
        MedicalDocument document = new MedicalDocument();

        // Assert
        assertNotNull(document);
        assertTrue(document.getActive());
        assertNotNull(document.getCreatedAt());
        assertNull(document.getId());
    }

    @Test
    @DisplayName("Deve criar documento com todos os parâmetros")
    void shouldCreateDocumentWithAllParameters() {
        // Act
        MedicalDocument document = new MedicalDocument(
                PATIENT_CPF,
                FILE_NAME,
                ORIGINAL_FILE_NAME,
                CONTENT_TYPE,
                FILE_SIZE,
                DocumentType.EXAM,
                STORAGE_PATH
        );

        // Assert
        assertNotNull(document);
        assertEquals(PATIENT_CPF, document.getPatientCpf());
        assertEquals(FILE_NAME, document.getFileName());
        assertEquals(ORIGINAL_FILE_NAME, document.getOriginalFileName());
        assertEquals(CONTENT_TYPE, document.getContentType());
        assertEquals(FILE_SIZE, document.getFileSize());
        assertEquals(DocumentType.EXAM, document.getDocumentType());
        assertEquals(STORAGE_PATH, document.getStoragePath());
        assertTrue(document.getActive());
        assertNotNull(document.getCreatedAt());
    }

    @Test
    @DisplayName("Deve inativar documento")
    void shouldInactivateDocument() {
        // Arrange
        MedicalDocument document = createDocument();
        LocalDateTime beforeInactivate = LocalDateTime.now();

        // Act
        document.inactivate();

        // Assert
        assertFalse(document.getActive());
        assertNotNull(document.getUpdatedAt());
        assertTrue(document.getUpdatedAt().isAfter(beforeInactivate) || 
                   document.getUpdatedAt().isEqual(beforeInactivate));
    }

    @Test
    @DisplayName("Deve marcar documento como deletado")
    void shouldMarkDocumentAsDeleted() {
        // Arrange
        MedicalDocument document = createDocument();
        LocalDateTime beforeDelete = LocalDateTime.now();

        // Act
        document.delete();

        // Assert
        assertNotNull(document.getDeletedAt());
        assertNotNull(document.getUpdatedAt());
        assertTrue(document.getDeletedAt().isAfter(beforeDelete) || 
                   document.getDeletedAt().isEqual(beforeDelete));
        assertTrue(document.getUpdatedAt().isAfter(beforeDelete) || 
                   document.getUpdatedAt().isEqual(beforeDelete));
    }

    @Test
    @DisplayName("Deve configurar e obter ID")
    void shouldSetAndGetId() {
        // Arrange
        MedicalDocument document = createDocument();
        UUID expectedId = UUID.randomUUID();

        // Act
        document.setId(expectedId);

        // Assert
        assertEquals(expectedId, document.getId());
    }

    @Test
    @DisplayName("Deve configurar e obter CPF do paciente")
    void shouldSetAndGetPatientCpf() {
        // Arrange
        MedicalDocument document = new MedicalDocument();
        String newCpf = "98765432100";

        // Act
        document.setPatientCpf(newCpf);

        // Assert
        assertEquals(newCpf, document.getPatientCpf());
    }

    @Test
    @DisplayName("Deve configurar e obter nome do arquivo")
    void shouldSetAndGetFileName() {
        // Arrange
        MedicalDocument document = new MedicalDocument();
        String newFileName = "novo_exame.pdf";

        // Act
        document.setFileName(newFileName);

        // Assert
        assertEquals(newFileName, document.getFileName());
    }

    @Test
    @DisplayName("Deve configurar e obter tipo de documento")
    void shouldSetAndGetDocumentType() {
        // Arrange
        MedicalDocument document = createDocument();

        // Act
        document.setDocumentType(DocumentType.ULTRASOUND);

        // Assert
        assertEquals(DocumentType.ULTRASOUND, document.getDocumentType());
    }

    @Test
    @DisplayName("Deve configurar e obter status ativo")
    void shouldSetAndGetActive() {
        // Arrange
        MedicalDocument document = createDocument();

        // Act
        document.setActive(false);

        // Assert
        assertFalse(document.getActive());
    }

    @Test
    @DisplayName("Deve configurar e obter data de criação")
    void shouldSetAndGetCreatedAt() {
        // Arrange
        MedicalDocument document = new MedicalDocument();
        LocalDateTime newCreatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);

        // Act
        document.setCreatedAt(newCreatedAt);

        // Assert
        assertEquals(newCreatedAt, document.getCreatedAt());
    }

    @Test
    @DisplayName("Deve configurar e obter data de atualização")
    void shouldSetAndGetUpdatedAt() {
        // Arrange
        MedicalDocument document = new MedicalDocument();
        LocalDateTime newUpdatedAt = LocalDateTime.of(2024, 1, 2, 10, 0);

        // Act
        document.setUpdatedAt(newUpdatedAt);

        // Assert
        assertEquals(newUpdatedAt, document.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve configurar e obter data de deleção")
    void shouldSetAndGetDeletedAt() {
        // Arrange
        MedicalDocument document = new MedicalDocument();
        LocalDateTime newDeletedAt = LocalDateTime.of(2024, 1, 3, 10, 0);

        // Act
        document.setDeletedAt(newDeletedAt);

        // Assert
        assertEquals(newDeletedAt, document.getDeletedAt());
    }

    @Test
    @DisplayName("Deve criar documento do tipo ULTRASOUND")
    void shouldCreateUltrasoundDocument() {
        // Act
        MedicalDocument document = new MedicalDocument(
                PATIENT_CPF,
                FILE_NAME,
                ORIGINAL_FILE_NAME,
                CONTENT_TYPE,
                FILE_SIZE,
                DocumentType.ULTRASOUND,
                STORAGE_PATH
        );

        // Assert
        assertEquals(DocumentType.ULTRASOUND, document.getDocumentType());
    }

    private MedicalDocument createDocument() {
        return new MedicalDocument(
                PATIENT_CPF,
                FILE_NAME,
                ORIGINAL_FILE_NAME,
                CONTENT_TYPE,
                FILE_SIZE,
                DocumentType.EXAM,
                STORAGE_PATH
        );
    }
}
