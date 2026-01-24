package br.com.hackathon.sus.prenatal_agenda.infrastructure.exceptions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ValidationError {

    private final LocalDateTime timestamp;
    private final Integer status;
    private final String error;
    private final String message;
    private final List<FieldMessage> errors = new ArrayList<>();

    public ValidationError(LocalDateTime timestamp, Integer status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public void addError(String fieldName, String message) {
        errors.add(new FieldMessage(fieldName, message));
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public Integer getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public List<FieldMessage> getErrors() { return errors; }
}
