package br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpInputMessage;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions.BusinessException;
import br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions.StandardError;
import br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions.ValidationError;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private MessageSource messageSource;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler(messageSource);
        when(request.getRequestURI()).thenReturn("/test-uri");

        // Configurar mocks padrão para MessageSource (lenient para evitar stubbings desnecessários)
        when(messageSource.getMessage(eq("error.endpoint.not.found"), any(), any())).thenReturn("Endpoint not found");
        when(messageSource.getMessage(eq("error.endpoint.description"), any(), any())).thenReturn("The requested endpoint does not exist or the HTTP method is incorrect");
        when(messageSource.getMessage(eq("error.request"), any(), any())).thenReturn("Request error");
        when(messageSource.getMessage(eq("error.json.invalid"), any(), any())).thenReturn("Invalid JSON format");
        when(messageSource.getMessage(eq("error.json.format"), any(), any())).thenReturn("JSON formatting error. Please check if all fields are correctly formatted");
        when(messageSource.getMessage(eq("error.json.mapping"), any(), any())).thenReturn("JSON mapping error. Please verify that all fields have the correct types");
        when(messageSource.getMessage(eq("error.unauthorized"), any(), any())).thenReturn("Unauthorized access");
        when(messageSource.getMessage(eq("error.resource.not.found"), any(), any())).thenReturn("Resource not found");
        when(messageSource.getMessage(eq("error.request.invalid"), any(), any())).thenReturn("Invalid request");
        when(messageSource.getMessage(eq("error.access.denied"), any(), any())).thenReturn("Access denied");
    }

    @Test
    void handleNoResourceFound_ShouldReturnNotFoundResponse() {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/non-existent-endpoint");

        ResponseEntity<StandardError> response = handler.handleNoResourceFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Endpoint not found", response.getBody().error());
        assertEquals("The requested endpoint does not exist or the HTTP method is incorrect", response.getBody().message());
        assertEquals("/test-uri", response.getBody().path());
    }

    @Test
    @DisplayName("Deve retornar 400 com mensagem de mapeamento quando causa é JsonMappingException")
    void shouldReturnBadRequestWithMappingMessageWhenCauseIsJsonMappingException() {
        JsonMappingException cause = mock(JsonMappingException.class);
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Error", cause, inputMessage);

        ResponseEntity<StandardError> response = handler.handleHttpMessageNotReadable(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Request error", response.getBody().error());
        assertEquals("JSON mapping error. Please verify that all fields have the correct types", response.getBody().message());
    }

    @Test
    @DisplayName("Deve retornar 400 com mensagem padrão quando causa é outra")
    void shouldReturnBadRequestWithDefaultMessageWhenCauseIsOther() {
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Error", new RuntimeException(), inputMessage);

        ResponseEntity<StandardError> response = handler.handleHttpMessageNotReadable(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Request error", response.getBody().error());
        assertEquals("Invalid JSON format", response.getBody().message());
    }

    @Test
    void validation_ShouldReturnValidationErrorWithFieldErrors() {
        // Cria erros de campo reais
        FieldError fieldError1 = new FieldError("object", "field1", "must not be blank");
        FieldError fieldError2 = new FieldError("object", "field2", "must be a number");

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "object");
        bindingResult.addError(fieldError1);
        bindingResult.addError(fieldError2);

        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ValidationError> response = handler.validation(exception, request);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getErrors().size());

        assertTrue(response.getBody().getErrors().toString().contains("field1"));
        assertTrue(response.getBody().getErrors().toString().contains("field2"));
        assertTrue(response.getBody().getErrors().toString().contains("must not be blank"));
        assertTrue(response.getBody().getErrors().toString().contains("must be a number"));
    }

    @Test
    @DisplayName("Deve retornar 401 quando BusinessException (acesso negado)")
    void shouldReturnUnauthorizedWhenBusinessExceptionAccessDenied() {
        BusinessException ex = new BusinessException("Access denied");

        ResponseEntity<StandardError> response = handler.handleBusinessException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unauthorized access", response.getBody().error());
        assertEquals("Access denied", response.getBody().message());
        assertEquals("/test-uri", response.getBody().path());
    }

    @Test
    @DisplayName("Deve retornar 404 quando BusinessException (recurso não encontrado)")
    void shouldReturnNotFoundWhenBusinessExceptionResourceNotFound() {
        BusinessException ex = new BusinessException("Resource not found");

        ResponseEntity<StandardError> response = handler.handleBusinessException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Resource not found", response.getBody().error());
        assertEquals("Resource not found", response.getBody().message());
        assertEquals("/test-uri", response.getBody().path());
    }

    @Test
    @DisplayName("Deve retornar mensagem específica quando causa é JsonParseException")
    void shouldReturnSpecificMessageWhenCauseIsJsonParseException() {
        JsonParseException cause = new JsonParseException(null, "Mock cause");
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Erro ao ler mensagem", cause, inputMessage);

        ResponseEntity<StandardError> response = handler.handleHttpMessageNotReadable(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Request error", response.getBody().error());
        assertEquals("JSON formatting error. Please check if all fields are correctly formatted", response.getBody().message());
        assertEquals("/test-uri", response.getBody().path());
    }

    @Test
    @DisplayName("Deve retornar 400 quando IllegalArgumentException")
    void shouldReturnBadRequestWhenIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid parameter");

        ResponseEntity<StandardError> response = handler.handleIllegalArgument(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid request", response.getBody().error());
        assertEquals("Invalid parameter", response.getBody().message());
        assertEquals("/test-uri", response.getBody().path());
    }

    @Test
    @DisplayName("Deve retornar 404 quando BusinessException tem messageKey contendo not.found")
    void shouldReturnNotFoundWhenBusinessExceptionHasMessageKeyNotFound() {
        BusinessException ex = new BusinessException("error.user.id.not.found", 999);
        when(messageSource.getMessage(eq("error.user.id.not.found"), any(), any())).thenReturn("User not found");
        when(messageSource.getMessage(eq("error.resource.not.found"), any(), any())).thenReturn("Resource not found");

        ResponseEntity<StandardError> response = handler.handleBusinessException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Resource not found", response.getBody().error());
        assertEquals("User not found", response.getBody().message());
    }

    @Test
    @DisplayName("Deve retornar 401 quando BusinessException tem messageKey contendo access.denied")
    void shouldReturnUnauthorizedWhenBusinessExceptionHasMessageKeyAccessDenied() {
        BusinessException ex = new BusinessException("error.access.denied", new Object[0]);

        ResponseEntity<StandardError> response = handler.handleBusinessException(ex, request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unauthorized access", response.getBody().error());
        assertEquals("Access denied", response.getBody().message());
    }

    @Test
    @DisplayName("Deve retornar 400 quando BusinessException tem messageKey sem not.found nem access.denied")
    void shouldReturnBadRequestWhenBusinessExceptionHasOtherMessageKey() {
        BusinessException ex = new BusinessException("user.cpf.exists", new Object[0]);
        when(messageSource.getMessage(eq("user.cpf.exists"), any(), any())).thenReturn("CPF already registered");

        ResponseEntity<StandardError> response = handler.handleBusinessException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid request", response.getBody().error());
        assertEquals("CPF already registered", response.getBody().message());
    }

    @Test
    @DisplayName("handleIllegalArgument deve usar mensagem traduzida quando formato error.xxx: arg")
    void handleIllegalArgument_shouldUseTranslatedMessageWhenFormatKeyArg() {
        when(messageSource.getMessage(eq("error.role.invalid"), any(), any())).thenReturn("Invalid role: {0}");
        IllegalArgumentException ex = new IllegalArgumentException("error.role.invalid: ROLE_XXX");

        ResponseEntity<StandardError> response = handler.handleIllegalArgument(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid request", response.getBody().error());
        assertTrue(response.getBody().message().contains("ROLE_XXX") || response.getBody().message().contains("Invalid"));
    }

    @Test
    @DisplayName("handleIllegalArgument deve usar mensagem original quando mensagem é null")
    void handleIllegalArgument_shouldUseOriginalMessageWhenMessageIsNull() {
        IllegalArgumentException ex = new IllegalArgumentException((String) null);

        ResponseEntity<StandardError> response = handler.handleIllegalArgument(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid request", response.getBody().error());
        assertEquals(null, response.getBody().message());
    }

    @Test
    @DisplayName("handleIllegalArgument deve usar mensagem original quando NoSuchMessageException")
    void handleIllegalArgument_shouldUseOriginalMessageWhenNoSuchMessage() {
        when(messageSource.getMessage(eq("error.unknown.key"), any(), any(), any())).thenThrow(new NoSuchMessageException("error.unknown.key"));
        IllegalArgumentException ex = new IllegalArgumentException("error.unknown.key");

        ResponseEntity<StandardError> response = handler.handleIllegalArgument(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid request", response.getBody().error());
        assertEquals("error.unknown.key", response.getBody().message());
    }
}
