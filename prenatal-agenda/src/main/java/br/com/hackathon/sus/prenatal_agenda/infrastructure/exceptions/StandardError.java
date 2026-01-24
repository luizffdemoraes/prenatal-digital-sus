package br.com.hackathon.sus.prenatal_agenda.infrastructure.exceptions;

import java.time.LocalDateTime;

public record StandardError(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message
) {
}
