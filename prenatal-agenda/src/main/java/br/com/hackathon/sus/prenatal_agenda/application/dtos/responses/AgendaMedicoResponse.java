package br.com.hackathon.sus.prenatal_agenda.application.dtos.responses;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.DiaSemana;

import java.time.LocalTime;
import java.util.Set;

public record AgendaMedicoResponse(
        Long id,
        Long medicoId,
        Long unidadeId,
        Set<DiaSemana> diasAtendimento,
        LocalTime horarioInicio,
        LocalTime horarioFim,
        Integer duracaoConsultaMinutos
) {
}
