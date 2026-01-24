package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ListAvailabilityUseCase {
    List<LocalTime> execute(Long medicoId, LocalDate data);
}
