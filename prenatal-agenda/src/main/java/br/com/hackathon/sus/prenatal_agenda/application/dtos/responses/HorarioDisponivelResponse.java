package br.com.hackathon.sus.prenatal_agenda.application.dtos.responses;

import java.time.LocalTime;
import java.util.List;

public record HorarioDisponivelResponse(
        Long medicoId,
        String data,
        List<LocalTime> horariosDisponiveis
) {
}
