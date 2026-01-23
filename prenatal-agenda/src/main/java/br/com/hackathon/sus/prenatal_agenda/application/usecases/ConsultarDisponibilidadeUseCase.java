package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ConsultarDisponibilidadeUseCase {
    List<LocalTime> execute(Long medicoId, LocalDate data);
}
