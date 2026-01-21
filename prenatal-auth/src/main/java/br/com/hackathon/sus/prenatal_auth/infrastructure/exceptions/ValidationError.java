package br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions;

import java.util.ArrayList;
import java.util.List;

public class ValidationError {
    private List<FieldMessage> errors = new ArrayList<>();

    public List<FieldMessage> getErrors() {
        return errors;
    }

    public void addError(String fieldName, String message) {
        errors.add(new FieldMessage(fieldName, message));
    }
}
