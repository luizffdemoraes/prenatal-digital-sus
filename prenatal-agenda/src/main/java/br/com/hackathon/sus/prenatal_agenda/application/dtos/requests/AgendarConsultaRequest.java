package br.com.hackathon.sus.prenatal_agenda.application.dtos.requests;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;




public record AgendarConsultaRequest(
        @NotNull(message = "ID da gestante é obrigatório")
        @Positive(message = "ID da gestante deve ser positivo")
        Long gestanteId,
        
        @NotNull(message = "ID do médico é obrigatório")
        @Positive(message = "ID do médico deve ser positivo")
        Long medicoId,
        
        @NotNull(message = "ID da unidade é obrigatório")
        @Positive(message = "ID da unidade deve ser positivo")
        Long unidadeId,
        
        @NotNull(message = "Data da consulta é obrigatória")
        @FutureOrPresent(message = "Data da consulta não pode ser no passado")
        LocalDate data,
        
        @NotNull(message = "Horário da consulta é obrigatório")
        LocalTime horario
) {
}
