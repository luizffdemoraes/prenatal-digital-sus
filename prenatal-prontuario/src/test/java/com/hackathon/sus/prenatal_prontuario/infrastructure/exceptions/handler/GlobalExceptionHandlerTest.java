package com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions.handler;

import com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions.BusinessException;
import com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions.ResourceNotFoundException;
import com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions.StandardError;
import com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Deve tratar ResourceNotFoundException")
    void shouldHandleResourceNotFoundException() {
        // Arrange
        ResourceNotFoundException exception = new ResourceNotFoundException("Prontuário não encontrado");

        // Act
        ResponseEntity<StandardError> response = handler.handleResourceNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().status());
        assertEquals("Recurso não encontrado", response.getBody().error());
        assertEquals("Prontuário não encontrado", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("Deve tratar BusinessException")
    void shouldHandleBusinessException() {
        // Arrange
        BusinessException exception = new BusinessException("Já existe prontuário para este CPF");

        // Act
        ResponseEntity<StandardError> response = handler.handleBusinessException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().status());
        assertEquals("Erro de negócio", response.getBody().error());
        assertEquals("Já existe prontuário para este CPF", response.getBody().message());
    }

    @Test
    void deveTratarIllegalArgumentException() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("CPF é obrigatório");

        // Act
        ResponseEntity<StandardError> response = handler.handleIllegalArgumentException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().status());
        assertEquals("Erro de validação", response.getBody().error());
        assertEquals("CPF é obrigatório", response.getBody().message());
    }

    @Test
    @DisplayName("Deve tratar AccessDeniedException")
    void shouldHandleAccessDeniedException() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Acesso negado");

        // Act
        ResponseEntity<StandardError> response = handler.handleAccessDenied(exception);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getBody().status());
        assertEquals("Acesso negado", response.getBody().error());
        assertEquals("Acesso negado", response.getBody().message());
    }

    @Test
    @DisplayName("Deve tratar IllegalStateException")
    void shouldHandleIllegalStateException() {
        // Arrange
        IllegalStateException exception = new IllegalStateException("Estado inválido");

        // Act
        ResponseEntity<StandardError> response = handler.handleIllegalStateException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().status());
        assertEquals("Estado inválido", response.getBody().error());
        assertEquals("Estado inválido", response.getBody().message());
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException")
    void shouldHandleMethodArgumentNotValidException() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("request", "cpf", "CPF é obrigatório");
        FieldError fieldError2 = new FieldError("request", "nomeCompleto", "Nome é obrigatório");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // Act
        ResponseEntity<ValidationError> response = handler.handleValidationExceptions(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("Erro de validação", response.getBody().getError());
        assertEquals("Dados inválidos fornecidos", response.getBody().getMessage());
        assertNotNull(response.getBody().getErrors());
        assertEquals(2, response.getBody().getErrors().size());
    }

    @Test
    @DisplayName("Deve tratar exceção genérica")
    void shouldHandleGenericException() {
        // Arrange
        Exception exception = new Exception("Erro interno");

        // Act
        ResponseEntity<StandardError> response = handler.handleGenericException(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().status());
        assertEquals("Erro interno do servidor", response.getBody().error());
        assertEquals("Erro interno", response.getBody().message());
    }
}
