package br.com.hackathon.sus.prenatal_agenda.application.dtos.requests;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request para agendar consulta (MVP).
 * <p>
 * - Gestante: nome e CPF (a própria UBS que agenda identifica a gestante).
 * - Médico: nome OU especialidade OU CRM (sem ID).
 * - Unidade: a UBS que realiza o agendamento envia no header X-Unidade-Id.
 */
public record AgendarConsultaRequest(
        @NotBlank(message = "Nome da gestante é obrigatório")
        String gestanteNome,

        @NotBlank(message = "CPF da gestante é obrigatório")
        String gestanteCpf,

        String medicoNome,
        String especialidade,
        String crm,

        @NotNull(message = "Data da consulta é obrigatória")
        @FutureOrPresent(message = "Data da consulta não pode ser no passado")
        LocalDate data,

        @NotNull(message = "Horário da consulta é obrigatório")
        LocalTime horario
) {
}
