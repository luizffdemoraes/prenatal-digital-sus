package br.com.hackathon.sus.prenatal_documento.infrastructure.exceptions;

import br.com.hackathon.sus.prenatal_documento.domain.exceptions.DocumentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Deve tratar DocumentNotFoundException")
    void shouldHandleDocumentNotFoundException() {
        DocumentNotFoundException ex = new DocumentNotFoundException("Documento não encontrado: 123");

        ResponseEntity<Map<String, String>> response = handler.handleDocumentNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Documento não encontrado", response.getBody().get("error"));
        assertTrue(response.getBody().get("message").contains("123"));
    }

    @Test
    @DisplayName("Deve tratar IllegalArgumentException")
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Tipo de documento inválido");

        ResponseEntity<Map<String, String>> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Argumento inválido", response.getBody().get("error"));
        assertEquals("Tipo de documento inválido", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Deve tratar IllegalStateException")
    void shouldHandleIllegalStateException() {
        IllegalStateException ex = new IllegalStateException("Documento inativo");

        ResponseEntity<Map<String, String>> response = handler.handleIllegalState(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Estado inválido", response.getBody().get("error"));
        assertEquals("Documento inativo", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException")
    void shouldHandleMethodArgumentNotValidException() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "cpf", "CPF é obrigatório"));
        bindingResult.addError(new FieldError("target", "documentType", "Tipo é obrigatório"));

        Method method = String.class.getMethod("indexOf", int.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro de validação", response.getBody().get("error"));

        @SuppressWarnings("unchecked")
        Map<String, String> fields = (Map<String, String>) response.getBody().get("fields");
        assertNotNull(fields);
        assertEquals("CPF é obrigatório", fields.get("cpf"));
        assertEquals("Tipo é obrigatório", fields.get("documentType"));
    }

    @Test
    @DisplayName("Deve tratar MaxUploadSizeExceededException")
    void shouldHandleMaxUploadSizeExceededException() {
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(10 * 1024 * 1024);

        ResponseEntity<Map<String, String>> response = handler.handleMaxUploadSizeExceeded(ex);

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tamanho de arquivo excedido", response.getBody().get("error"));
        assertEquals("O arquivo excede o tamanho máximo permitido", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Deve tratar AccessDeniedException")
    void shouldHandleAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Acesso negado");

        ResponseEntity<Map<String, String>> response = handler.handleAccessDenied(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Acesso negado", response.getBody().get("error"));
        assertEquals("Você não tem permissão para realizar esta ação", response.getBody().get("message"));
    }

    @Test
    @DisplayName("Deve tratar RuntimeException genérica")
    void shouldHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("Erro inesperado");

        ResponseEntity<Map<String, String>> response = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro interno do servidor", response.getBody().get("error"));
        assertEquals("Ocorreu um erro ao processar a requisição", response.getBody().get("message"));
    }
}
