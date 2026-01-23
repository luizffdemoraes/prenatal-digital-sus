package br.com.hackathon.sus.prenatal_agenda.application.dtos.responses;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.MotivoCancelamento;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.StatusConsulta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ConsultaResponse(
        Long id,
        Long gestanteId,
        Long medicoId,
        Long unidadeId,
        LocalDate data,
        LocalTime horario,
        StatusConsulta status,
        MotivoCancelamento motivoCancelamento,
        LocalDateTime dataAgendamento,
        LocalDateTime dataCancelamento
) {
}
