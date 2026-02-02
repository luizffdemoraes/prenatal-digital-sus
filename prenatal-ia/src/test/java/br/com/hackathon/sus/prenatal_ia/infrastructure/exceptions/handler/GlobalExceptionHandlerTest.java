package br.com.hackathon.sus.prenatal_ia.infrastructure.exceptions.handler;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import br.com.hackathon.sus.prenatal_ia.infrastructure.exceptions.StandardError;
import br.com.hackathon.sus.prenatal_ia.infrastructure.exceptions.ValidationError;

@DisplayName("Testes do GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleIllegalArgumentException retorna 400 com StandardError")
    void handleIllegalArgumentExceptionRetorna400() throws Exception {
        IllegalArgumentException ex = new IllegalArgumentException("CPF inválido");

        ResponseEntity<StandardError> response = handler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Erro de validação", response.getBody().error());
        assertEquals("CPF inválido", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    @DisplayName("handleValidationExceptions retorna 400 com ValidationError e erros de campo")
    void handleValidationExceptionsRetorna400ComErrosDeCampo() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "email", "não pode ser vazio"));
        bindingResult.addError(new FieldError("target", "nome", "tamanho mínimo 3"));
        Method method = GlobalExceptionHandler.class.getMethod("handleValidationExceptions", MethodArgumentNotValidException.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                new MethodParameter(method, 0),
                bindingResult
        );

        ResponseEntity<ValidationError> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Erro de validação", response.getBody().getError());
        assertEquals("Dados inválidos fornecidos", response.getBody().getMessage());
        assertEquals(2, response.getBody().getErrors().size());
        assertTrue(response.getBody().getErrors().stream().anyMatch(e -> "email".equals(e.fieldName()) && "não pode ser vazio".equals(e.message())));
        assertTrue(response.getBody().getErrors().stream().anyMatch(e -> "nome".equals(e.fieldName()) && "tamanho mínimo 3".equals(e.message())));
    }

    @Test
    @DisplayName("handleGenericException retorna 500 com StandardError")
    void handleGenericExceptionRetorna500() {
        Exception ex = new RuntimeException("Erro inesperado");

        ResponseEntity<StandardError> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("Erro interno do servidor", response.getBody().error());
        assertEquals("Erro inesperado", response.getBody().message());
        assertNotNull(response.getBody().timestamp());
    }
}
