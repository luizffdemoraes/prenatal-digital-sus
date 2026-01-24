package br.com.hackathon.sus.prenatal_agenda.infrastructure.exceptions;

public record FieldMessage(
        String fieldName,
        String message
) {
}
