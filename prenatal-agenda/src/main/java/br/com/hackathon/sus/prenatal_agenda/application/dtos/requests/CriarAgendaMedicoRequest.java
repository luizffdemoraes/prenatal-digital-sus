package br.com.hackathon.sus.prenatal_agenda.application.dtos.requests;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.DiaSemana;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;
import java.util.Set;

public record CriarAgendaMedicoRequest(
        @NotBlank(message = "CRM do médico é obrigatório")
        String crm,

        @NotBlank(message = "Nome do médico é obrigatório")
        String medicoNome,

        @NotBlank(message = "Especialidade do médico é obrigatória")
        String especialidade,

        @NotNull(message = "ID da unidade é obrigatório")
        @Positive(message = "ID da unidade deve ser positivo")
        Long unidadeId,

        @NotNull(message = "Dias de atendimento são obrigatórios")
        Set<DiaSemana> diasAtendimento,

        @NotNull(message = "Horário de início é obrigatório")
        LocalTime horarioInicio,

        @NotNull(message = "Horário de fim é obrigatório")
        LocalTime horarioFim,

        @NotNull(message = "Duração da consulta é obrigatória")
        @Positive(message = "Duração da consulta deve ser positiva")
        Integer duracaoConsultaMinutos
) {
}
