package br.com.hackathon.sus.prenatal_agenda.infrastructure.exceptions.handler;

import br.com.hackathon.sus.prenatal_agenda.infrastructure.exceptions.BusinessException;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.exceptions.StandardError;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.exceptions.ValidationError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleBusinessException retorna 400 e StandardError")
    void handleBusinessException() {
        BusinessException ex = new BusinessException("Horário indisponível");

        ResponseEntity<StandardError> response = handler.handleBusinessException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().status());
        assertEquals("Erro de negócio", response.getBody().error());
        assertEquals("Horário indisponível", response.getBody().message());
    }

    @Test
    @DisplayName("handleIllegalArgumentException retorna 400 e StandardError")
    void handleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Data inválida");

        ResponseEntity<StandardError> response = handler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro de validação", response.getBody().error());
        assertEquals("Data inválida", response.getBody().message());
    }

    @Test
    @DisplayName("handleIllegalStateException retorna 400 e StandardError")
    void handleIllegalStateException() {
        IllegalStateException ex = new IllegalStateException("Consulta já cancelada");

        ResponseEntity<StandardError> response = handler.handleIllegalStateException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Estado inválido", response.getBody().error());
        assertEquals("Consulta já cancelada", response.getBody().message());
    }

    @Test
    @DisplayName("handleValidationExceptions retorna 400 e ValidationError com campos")
    void handleValidationExceptions() throws Exception {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "obj");
        bindingResult.addError(new FieldError("obj", "data", "não pode ser nula"));
        bindingResult.addError(new FieldError("obj", "horario", "obrigatório"));
        Method method = GlobalExceptionHandler.class.getDeclaredMethod("handleValidationExceptions", MethodArgumentNotValidException.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                new org.springframework.core.MethodParameter(method, 0), bindingResult);

        ResponseEntity<ValidationError> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Erro de validação", response.getBody().getError());
        assertEquals(2, response.getBody().getErrors().size());
        assertEquals("data", response.getBody().getErrors().get(0).fieldName());
        assertEquals("não pode ser nula", response.getBody().getErrors().get(0).message());
        assertEquals("horario", response.getBody().getErrors().get(1).fieldName());
    }

    @Test
    @DisplayName("handleGenericException retorna 500 e StandardError")
    void handleGenericException() {
        Exception ex = new RuntimeException("Falha inesperada");

        ResponseEntity<StandardError> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("Erro interno do servidor", response.getBody().error());
        assertEquals("Falha inesperada", response.getBody().message());
    }
}
