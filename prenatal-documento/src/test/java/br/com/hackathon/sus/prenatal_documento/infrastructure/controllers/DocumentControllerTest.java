package br.com.hackathon.sus.prenatal_documento.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_documento.application.dtos.responses.DocumentResponse;
import br.com.hackathon.sus.prenatal_documento.application.usecases.*;
import br.com.hackathon.sus.prenatal_documento.domain.enums.DocumentType;
import br.com.hackathon.sus.prenatal_documento.domain.exceptions.DocumentNotFoundException;
import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import br.com.hackathon.sus.prenatal_documento.infrastructure.config.mapper.DocumentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do DocumentController")
class DocumentControllerTest {

    @Mock
    private UploadDocumentUseCase uploadUseCase;

    @Mock
    private DownloadDocumentUseCase downloadUseCase;

    @Mock
    private ListDocumentsUseCase listUseCase;

    @Mock
    private InactivateDocumentUseCase inactivateUseCase;

    @Mock
    private DeleteDocumentUseCase deleteUseCase;

    @Mock
    private RequestDeleteDocumentUseCase requestDeleteUseCase;

    @Mock
    private DocumentMapper mapper;

    @InjectMocks
    private DocumentController controller;

    private static final String PATIENT_CPF = "12345678900";
    private static final UUID DOCUMENT_ID = UUID.randomUUID();
    private static final String FILE_NAME = "exame.pdf";
    private static final String CONTENT_TYPE = "application/pdf";

    private MockMultipartFile mockFile;
    private MedicalDocument mockDocument;
    private DocumentResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockFile = new MockMultipartFile(
                "file",
                FILE_NAME,
                CONTENT_TYPE,
                "PDF content".getBytes()
        );

        mockDocument = new MedicalDocument(
                PATIENT_CPF,
                FILE_NAME,
                FILE_NAME,
                CONTENT_TYPE,
                1024L,
                DocumentType.EXAM,
                null,
                "prenatal-records/12345678900/exame.pdf"
        );
        mockDocument.setId(DOCUMENT_ID);

        mockResponse = new DocumentResponse(
                DOCUMENT_ID,
                PATIENT_CPF,
                FILE_NAME,
                FILE_NAME,
                CONTENT_TYPE,
                1024L,
                DocumentType.EXAM,
                null,
                true,
                null,
                null
        );
    }

    @Test
    @DisplayName("Deve fazer upload de documento com sucesso")
    void shouldUploadDocumentSuccessfully() {
        // Arrange
        when(uploadUseCase.upload(eq(PATIENT_CPF), any(), eq("EXAM"), any())).thenReturn(mockDocument);
        when(mapper.toResponse(mockDocument)).thenReturn(mockResponse);

        // Act
        ResponseEntity<DocumentResponse> response = controller.upload(PATIENT_CPF, mockFile, "EXAM", null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(DOCUMENT_ID, response.getBody().id());
        
        verify(uploadUseCase, times(1)).upload(eq(PATIENT_CPF), any(), eq("EXAM"), any());
        verify(mapper, times(1)).toResponse(mockDocument);
    }

    @Test
    @DisplayName("Deve listar documentos do paciente")
    void shouldListDocuments() {
        // Arrange
        MedicalDocument doc1 = createDocument("exame1.pdf");
        MedicalDocument doc2 = createDocument("exame2.pdf");
        List<MedicalDocument> documents = Arrays.asList(doc1, doc2);
        
        DocumentResponse response1 = createResponse(doc1.getId(), "exame1.pdf");
        DocumentResponse response2 = createResponse(doc2.getId(), "exame2.pdf");
        
        when(listUseCase.listActiveByPatientCpf(PATIENT_CPF)).thenReturn(documents);
        when(mapper.toResponse(doc1)).thenReturn(response1);
        when(mapper.toResponse(doc2)).thenReturn(response2);

        // Act
        ResponseEntity<List<DocumentResponse>> response = controller.list(PATIENT_CPF);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        
        verify(listUseCase, times(1)).listActiveByPatientCpf(PATIENT_CPF);
        verify(mapper, times(2)).toResponse(any(MedicalDocument.class));
    }

    @Test
    @DisplayName("Deve fazer download de documento")
    void shouldDownloadDocument() {
        // Arrange
        byte[] fileContent = "PDF content".getBytes();
        when(downloadUseCase.download(DOCUMENT_ID)).thenReturn(fileContent);
        when(downloadUseCase.getContentType(DOCUMENT_ID)).thenReturn(CONTENT_TYPE);
        when(downloadUseCase.getFileName(DOCUMENT_ID)).thenReturn(FILE_NAME);

        // Act
        ResponseEntity<byte[]> response = controller.download(DOCUMENT_ID);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(fileContent, response.getBody());
        assertEquals(CONTENT_TYPE, response.getHeaders().getContentType().toString());
        
        verify(downloadUseCase, times(1)).download(DOCUMENT_ID);
        verify(downloadUseCase, times(1)).getContentType(DOCUMENT_ID);
        verify(downloadUseCase, times(1)).getFileName(DOCUMENT_ID);
    }

    @Test
    @DisplayName("Deve inativar documento")
    void shouldInactivateDocument() {
        // Arrange
        doNothing().when(inactivateUseCase).inactivate(DOCUMENT_ID);

        // Act
        ResponseEntity<Void> response = controller.inactivate(DOCUMENT_ID);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        verify(inactivateUseCase, times(1)).inactivate(DOCUMENT_ID);
    }

    @Test
    @DisplayName("Deve deletar documento permanentemente")
    void shouldDeleteDocumentPermanently() {
        // Arrange
        doNothing().when(deleteUseCase).deletePermanently(DOCUMENT_ID);

        // Act
        ResponseEntity<Void> response = controller.deletePermanently(DOCUMENT_ID);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        verify(deleteUseCase, times(1)).deletePermanently(DOCUMENT_ID);
    }

    @Test
    @DisplayName("Deve solicitar deleção de documento")
    void shouldRequestDeleteDocument() {
        // Arrange
        doNothing().when(requestDeleteUseCase).requestDelete(DOCUMENT_ID);

        // Act
        ResponseEntity<Void> response = controller.requestDelete(DOCUMENT_ID);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        
        verify(requestDeleteUseCase, times(1)).requestDelete(DOCUMENT_ID);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há documentos")
    void shouldReturnEmptyListWhenNoDocuments() {
        // Arrange
        when(listUseCase.listActiveByPatientCpf(PATIENT_CPF)).thenReturn(List.of());

        // Act
        ResponseEntity<List<DocumentResponse>> response = controller.list(PATIENT_CPF);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        
        verify(listUseCase, times(1)).listActiveByPatientCpf(PATIENT_CPF);
    }

    @Test
    @DisplayName("Deve propagar exceção quando documento não encontrado no download")
    void shouldPropagateExceptionWhenDocumentNotFoundOnDownload() {
        // Arrange
        when(downloadUseCase.download(DOCUMENT_ID))
                .thenThrow(new DocumentNotFoundException("Documento não encontrado"));

        // Act & Assert
        assertThrows(
                DocumentNotFoundException.class,
                () -> controller.download(DOCUMENT_ID)
        );
        
        verify(downloadUseCase, times(1)).download(DOCUMENT_ID);
    }

    @Test
    @DisplayName("Deve propagar exceção quando documento não encontrado na inativação")
    void shouldPropagateExceptionWhenDocumentNotFoundOnInactivate() {
        // Arrange
        doThrow(new DocumentNotFoundException("Documento não encontrado"))
                .when(inactivateUseCase).inactivate(DOCUMENT_ID);

        // Act & Assert
        assertThrows(
                DocumentNotFoundException.class,
                () -> controller.inactivate(DOCUMENT_ID)
        );
        
        verify(inactivateUseCase, times(1)).inactivate(DOCUMENT_ID);
    }

    private MedicalDocument createDocument(String fileName) {
        MedicalDocument doc = new MedicalDocument(
                PATIENT_CPF,
                fileName,
                fileName,
                CONTENT_TYPE,
                1024L,
                DocumentType.EXAM,
                null,
                "prenatal-records/" + PATIENT_CPF + "/" + fileName
        );
        doc.setId(UUID.randomUUID());
        return doc;
    }

    private DocumentResponse createResponse(UUID id, String fileName) {
        return new DocumentResponse(
                id,
                PATIENT_CPF,
                fileName,
                fileName,
                CONTENT_TYPE,
                1024L,
                DocumentType.EXAM,
                null,
                true,
                null,
                null
        );
    }
}
