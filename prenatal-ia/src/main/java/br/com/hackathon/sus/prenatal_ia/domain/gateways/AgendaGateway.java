package br.com.hackathon.sus.prenatal_ia.domain.gateways;

import br.com.hackathon.sus.prenatal_ia.domain.entities.AppointmentSummary;

import java.util.List;

public interface AgendaGateway {
    List<AppointmentSummary> findAppointmentsByCpf(String cpf);
}
