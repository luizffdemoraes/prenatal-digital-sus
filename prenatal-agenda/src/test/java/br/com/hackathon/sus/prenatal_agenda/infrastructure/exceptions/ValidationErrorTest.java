package br.com.hackathon.sus.prenatal_agenda.infrastructure.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidationError")
class ValidationErrorTest {

    @Test
    @DisplayName("construtor e getters")
    void construtorEGetters() {
        LocalDateTime now = LocalDateTime.now();
        ValidationError error = new ValidationError(now, 400, "Erro de validação", "Verifique os campos.");

        assertEquals(now, error.getTimestamp());
        assertEquals(400, error.getStatus());
        assertEquals("Erro de validação", error.getError());
        assertEquals("Verifique os campos.", error.getMessage());
        assertTrue(error.getErrors().isEmpty());
    }

    @Test
    @DisplayName("addError adiciona FieldMessage à lista")
    void addError() {
        ValidationError error = new ValidationError(LocalDateTime.now(), 400, "Erro", "Msg");
        error.addError("data", "não pode ser nula");
        error.addError("horario", "obrigatório");

        List<FieldMessage> errors = error.getErrors();
        assertEquals(2, errors.size());
        assertEquals("data", errors.get(0).fieldName());
        assertEquals("não pode ser nula", errors.get(0).message());
        assertEquals("horario", errors.get(1).fieldName());
        assertEquals("obrigatório", errors.get(1).message());
    }
}
