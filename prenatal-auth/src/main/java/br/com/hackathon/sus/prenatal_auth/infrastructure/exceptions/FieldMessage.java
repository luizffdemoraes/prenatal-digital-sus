package br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions;

public record FieldMessage(String fieldName, String message) {

    public FieldMessage(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }
}