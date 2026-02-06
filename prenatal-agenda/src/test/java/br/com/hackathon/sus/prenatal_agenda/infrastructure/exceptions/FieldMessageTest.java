package br.com.hackathon.sus.prenatal_agenda.infrastructure.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FieldMessage")
class FieldMessageTest {

    @Test
    @DisplayName("record expõe fieldName e message")
    void recordAccessors() {
        FieldMessage msg = new FieldMessage("data", "não pode ser nula");

        assertEquals("data", msg.fieldName());
        assertEquals("não pode ser nula", msg.message());
    }
}
