package br.com.hackathon.sus.prenatal_documento.application.usecases;

import br.com.hackathon.sus.prenatal_documento.domain.enums.DocumentType;
import br.com.hackathon.sus.prenatal_documento.domain.gateways.StorageGateway;
import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import br.com.hackathon.sus.prenatal_documento.domain.repositories.MedicalDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UploadDocumentUseCaseImpl")
class UploadDocumentUseCaseImplTest {

    @Mock
    private MedicalDocumentRepository repository;

    @Mock
    private StorageGateway storageGateway;

    @Mock(lenient = true)
    private MultipartFile file;

    @InjectMocks
    private UploadDocumentUseCaseImpl uploadUseCase;

    private static final String PATIENT_CPF = "12345678900";
    private static final String DOCUMENT_TYPE = "EXAM";
    private static final String FILE_NAME = "exame.pdf";
    private static final String CONTENT_TYPE = "application/pdf";
    private static final long FILE_SIZE = 1024L;

    @BeforeEach
    void setUp() {
        when(file.getOriginalFilename()).thenReturn(FILE_NAME);
        when(file.getContentType()).thenReturn(CONTENT_TYPE);
        when(file.getSize()).thenReturn(FILE_SIZE);
        when(file.isEmpty()).thenReturn(false);
    }

    @Test
    @DisplayName("Deve fazer upload de documento com sucesso")
    void shouldUploadDocumentSuccessfully() throws IOException {
        // Arrange
        byte[] fileContent = "PDF content".getBytes();
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));
        
        String storagePath = "prenatal-records/12345678900/123456789/test.pdf";
        when(storageGateway.upload(anyString(), any(), eq(CONTENT_TYPE), eq(FILE_SIZE)))
                .thenReturn(storagePath);

        MedicalDocument savedDocument = new MedicalDocument(
                PATIENT_CPF, "test.pdf", FILE_NAME, CONTENT_TYPE, FILE_SIZE,
                DocumentType.EXAM, storagePath
        );
        savedDocument.setId(UUID.randomUUID());
        when(repository.save(any(MedicalDocument.class))).thenReturn(savedDocument);

        // Act
        MedicalDocument result = uploadUseCase.upload(PATIENT_CPF, file, DOCUMENT_TYPE);

        // Assert
        assertNotNull(result);
        assertEquals(PATIENT_CPF, result.getPatientCpf());
        assertEquals(FILE_NAME, result.getOriginalFileName());
        assertEquals(CONTENT_TYPE, result.getContentType());
        assertEquals(FILE_SIZE, result.getFileSize());
        assertEquals(DocumentType.EXAM, result.getDocumentType());
        
        verify(storageGateway, times(1)).upload(anyString(), any(), eq(CONTENT_TYPE), eq(FILE_SIZE));
        verify(repository, times(1)).save(any(MedicalDocument.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando arquivo é nulo")
    void shouldThrowExceptionWhenFileIsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> uploadUseCase.upload(PATIENT_CPF, null, DOCUMENT_TYPE)
        );
        
        assertEquals("Arquivo não pode ser vazio", exception.getMessage());
        verify(storageGateway, never()).upload(anyString(), any(), anyString(), anyLong());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando arquivo está vazio")
    void shouldThrowExceptionWhenFileIsEmpty() {
        // Arrange
        when(file.isEmpty()).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> uploadUseCase.upload(PATIENT_CPF, file, DOCUMENT_TYPE)
        );
        
        assertEquals("Arquivo não pode ser vazio", exception.getMessage());
        verify(storageGateway, never()).upload(anyString(), any(), anyString(), anyLong());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando arquivo excede tamanho máximo")
    void shouldThrowExceptionWhenFileSizeExceedsLimit() {
        // Arrange
        long maxSize = 11L * 1024 * 1024; // 11MB
        when(file.getSize()).thenReturn(maxSize);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> uploadUseCase.upload(PATIENT_CPF, file, DOCUMENT_TYPE)
        );
        
        assertEquals("Arquivo excede o tamanho máximo de 10MB", exception.getMessage());
        verify(storageGateway, never()).upload(anyString(), any(), anyString(), anyLong());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando tipo de arquivo não é PDF")
    void shouldThrowExceptionWhenFileTypeIsNotPdf() {
        // Arrange
        when(file.getContentType()).thenReturn("image/jpeg");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> uploadUseCase.upload(PATIENT_CPF, file, DOCUMENT_TYPE)
        );
        
        assertEquals("Apenas arquivos PDF são permitidos", exception.getMessage());
        verify(storageGateway, never()).upload(anyString(), any(), anyString(), anyLong());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando tipo de documento é inválido")
    void shouldThrowExceptionWhenDocumentTypeIsInvalid() throws IOException {
        // Arrange
        byte[] fileContent = "PDF content".getBytes();
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> uploadUseCase.upload(PATIENT_CPF, file, "INVALID_TYPE")
        );
        
        assertTrue(exception.getMessage().contains("Tipo de documento inválido"));
        verify(storageGateway, never()).upload(anyString(), any(), anyString(), anyLong());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorre erro ao processar arquivo")
    void shouldThrowExceptionWhenIOErrorOccurs() throws IOException {
        // Arrange
        when(file.getInputStream()).thenThrow(new IOException("Erro de I/O"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> uploadUseCase.upload(PATIENT_CPF, file, DOCUMENT_TYPE)
        );
        
        assertEquals("Erro ao processar arquivo", exception.getMessage());
        verify(storageGateway, never()).upload(anyString(), any(), anyString(), anyLong());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve fazer upload de documento do tipo ULTRASOUND")
    void shouldUploadUltrasoundDocumentSuccessfully() throws IOException {
        // Arrange
        byte[] fileContent = "PDF content".getBytes();
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(fileContent));
        
        String storagePath = "prenatal-records/12345678900/123456789/test.pdf";
        when(storageGateway.upload(anyString(), any(), eq(CONTENT_TYPE), eq(FILE_SIZE)))
                .thenReturn(storagePath);

        MedicalDocument savedDocument = new MedicalDocument(
                PATIENT_CPF, "test.pdf", FILE_NAME, CONTENT_TYPE, FILE_SIZE,
                DocumentType.ULTRASOUND, storagePath
        );
        savedDocument.setId(UUID.randomUUID());
        when(repository.save(any(MedicalDocument.class))).thenReturn(savedDocument);

        // Act
        MedicalDocument result = uploadUseCase.upload(PATIENT_CPF, file, "ULTRASOUND");

        // Assert
        assertNotNull(result);
        assertEquals(DocumentType.ULTRASOUND, result.getDocumentType());
        verify(storageGateway, times(1)).upload(anyString(), any(), eq(CONTENT_TYPE), eq(FILE_SIZE));
        verify(repository, times(1)).save(any(MedicalDocument.class));
    }
}
