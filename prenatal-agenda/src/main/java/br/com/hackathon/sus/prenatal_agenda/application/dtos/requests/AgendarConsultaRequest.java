package br.com.hackathon.sus.prenatal_agenda.application.dtos.requests;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request para agendar consulta. Aceita o que a gestante conhece ou IDs quando o sistema já os tiver:
 * <p>
 * Gestante: gestanteId OU gestanteCpf OU gestanteEmail
 * Médico: medicoId OU medicoNome OU especialidade
 * Unidade: unidadeId OU unidadeNome
 * <p>
 * Validação de "pelo menos um de cada grupo" é feita no use case.
 */
public record AgendarConsultaRequest(
        Long gestanteId,
        String gestanteCpf,
        String gestanteEmail,

        Long medicoId,
        String medicoNome,
        String especialidade,

        Long unidadeId,
        String unidadeNome,

        @NotNull(message = "Data da consulta é obrigatória")
        @FutureOrPresent(message = "Data da consulta não pode ser no passado")
        LocalDate data,

        @NotNull(message = "Horário da consulta é obrigatório")
        LocalTime horario
) {
}
