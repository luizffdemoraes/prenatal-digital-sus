package br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions.handler;


import br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions.BusinessException;
import br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions.StandardError;
import br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions.ValidationError;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardError> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String error = messageSource.getMessage("error.endpoint.not.found", null, LocaleContextHolder.getLocale());
        String message = messageSource.getMessage("error.endpoint.description", null, LocaleContextHolder.getLocale());
        
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                error,
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<StandardError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String messageKey = "error.json.invalid";
        if (ex.getCause() instanceof JsonParseException) {
            messageKey = "error.json.format";
        } else if (ex.getCause() instanceof JsonMappingException) {
            messageKey = "error.json.mapping";
        }

        String error = messageSource.getMessage("error.request", null, LocaleContextHolder.getLocale());
        String message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                error,
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError err = new ValidationError();

        e.getBindingResult().getFieldErrors().forEach(fieldError -> 
            err.addError(fieldError.getField(), fieldError.getDefaultMessage())
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardError> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        String errorMessage;
        String errorTitle;
        HttpStatus status;

        if (ex.hasMessageKey()) {
            // Usa a chave do properties
            errorMessage = messageSource.getMessage(ex.getMessageKey(), ex.getMessageArgs(), LocaleContextHolder.getLocale());
            
            // Determina o status baseado na chave
            if (ex.getMessageKey().contains("not.found")) {
                status = HttpStatus.NOT_FOUND;
                errorTitle = messageSource.getMessage("error.resource.not.found", null, LocaleContextHolder.getLocale());
            } else if (ex.getMessageKey().contains("access.denied") || ex.getMessageKey().contains("unauthorized")) {
                status = HttpStatus.UNAUTHORIZED;
                errorTitle = messageSource.getMessage("error.unauthorized", null, LocaleContextHolder.getLocale());
            } else {
                status = HttpStatus.BAD_REQUEST;
                errorTitle = messageSource.getMessage("error.request.invalid", null, LocaleContextHolder.getLocale());
            }
        } else {
            // Fallback para mensagens hardcoded (compatibilidade)
            String msg = ex.getMessage().toLowerCase();
            status = msg.contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.UNAUTHORIZED;
            errorTitle = status == HttpStatus.NOT_FOUND 
                    ? messageSource.getMessage("error.resource.not.found", null, LocaleContextHolder.getLocale())
                    : messageSource.getMessage("error.unauthorized", null, LocaleContextHolder.getLocale());
            errorMessage = ex.getMessage();
        }

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                errorTitle,
                errorMessage,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        
        // Tenta resolver mensagem como chave, se não encontrar, usa a mensagem original
        String errorMessage;
        try {
            // Verifica se a mensagem é uma chave (contém "error." ou "user." etc)
            if (ex.getMessage() != null && (ex.getMessage().startsWith("error.") || ex.getMessage().contains("."))) {
                String[] parts = ex.getMessage().split(": ");
                if (parts.length > 1) {
                    // Formato "error.role.invalid: ROLE_XXX"
                    String messageKey = parts[0];
                    String arg = parts[1];
                    errorMessage = messageSource.getMessage(messageKey, new Object[]{arg}, LocaleContextHolder.getLocale());
                } else {
                    errorMessage = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), LocaleContextHolder.getLocale());
                }
            } else {
                errorMessage = ex.getMessage();
            }
        } catch (NoSuchMessageException e) {
            // Se não conseguir resolver, usa a mensagem original
            errorMessage = ex.getMessage();
        }
        
        String errorTitle = messageSource.getMessage("error.request.invalid", null, LocaleContextHolder.getLocale());
        
        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                errorTitle,
                errorMessage,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }
}
