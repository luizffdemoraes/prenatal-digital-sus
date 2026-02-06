package br.com.hackathon.sus.prenatal_agenda.infrastructure.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BusinessException")
class BusinessExceptionTest {

    @Test
    @DisplayName("construtor com mensagem define message e cause null")
    void construtorMensagem() {
        BusinessException ex = new BusinessException("Erro de negócio");

        assertEquals("Erro de negócio", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    @DisplayName("construtor com mensagem e cause define cause")
    void construtorMensagemECause() {
        Throwable cause = new IllegalArgumentException("causa");
        BusinessException ex = new BusinessException("Erro de negócio", cause);

        assertEquals("Erro de negócio", ex.getMessage());
        assertSame(cause, ex.getCause());
    }
}
