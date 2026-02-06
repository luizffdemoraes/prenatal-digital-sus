package br.com.hackathon.sus.prenatal_agenda.infrastructure.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StandardError")
class StandardErrorTest {

    @Test
    @DisplayName("record expõe timestamp, status, error e message")
    void recordAccessors() {
        LocalDateTime now = LocalDateTime.now();
        StandardError error = new StandardError(now, 400, "Erro de negócio", "Mensagem");

        assertEquals(now, error.timestamp());
        assertEquals(400, error.status());
        assertEquals("Erro de negócio", error.error());
        assertEquals("Mensagem", error.message());
    }
}
