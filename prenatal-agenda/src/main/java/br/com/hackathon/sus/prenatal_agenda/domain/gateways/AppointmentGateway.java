package br.com.hackathon.sus.prenatal_agenda.domain.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentGateway {

    Appointment salvar(Appointment appointment);

    Optional<Appointment> buscarPorId(Long id);

    List<Appointment> buscarPorGestanteId(Long gestanteId);

    List<Appointment> buscarConsultasAgendadas(Long medicoId, LocalDate data, LocalTime horario);

    List<Appointment> buscarConsultasAgendadasPorMedicoEData(Long medicoId, LocalDate data);

    boolean existeAgendamentoPorMedico(Long medicoId);
}
