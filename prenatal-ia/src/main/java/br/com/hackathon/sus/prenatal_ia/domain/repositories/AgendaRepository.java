package br.com.hackathon.sus.prenatal_ia.domain.repositories;

import br.com.hackathon.sus.prenatal_ia.domain.entities.AppointmentSummary;

import java.util.List;

public interface AgendaRepository {
    List<AppointmentSummary> findAppointmentsByCpf(String cpf);
}
