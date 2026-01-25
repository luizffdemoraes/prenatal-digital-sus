package com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions;

import java.time.LocalDateTime;

public record StandardError(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message
) {}
