package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Weekday;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AppointmentGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorScheduleGateway;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ListAvailabilityUseCaseImp implements ListAvailabilityUseCase {

    private final DoctorScheduleGateway doctorScheduleGateway;
    private final AppointmentGateway appointmentGateway;

    public ListAvailabilityUseCaseImp(DoctorScheduleGateway doctorScheduleGateway,
                                      AppointmentGateway appointmentGateway) {
        this.doctorScheduleGateway = doctorScheduleGateway;
        this.appointmentGateway = appointmentGateway;
    }

    @Override
    public List<LocalTime> execute(Long medicoId, LocalDate data) {
        DoctorSchedule schedule = doctorScheduleGateway.buscarPorMedicoId(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada para o médico informado"));

        Weekday diaSemana = Weekday.fromDayOfWeek(data.getDayOfWeek());
        if (!schedule.atendeNoDia(diaSemana)) {
            return List.of();
        }

        List<LocalTime> todosOsSlots = gerarSlotsDisponiveis(schedule);

        List<Appointment> consultasAgendadas = appointmentGateway.buscarConsultasAgendadasPorMedicoEData(medicoId, data);

        Set<LocalTime> horariosOcupados = consultasAgendadas.stream()
                .filter(Appointment::estaAgendada)
                .map(Appointment::getHorario)
                .collect(Collectors.toSet());

        return todosOsSlots.stream()
                .filter(horario -> !horariosOcupados.contains(horario))
                .toList();
    }

    private List<LocalTime> gerarSlotsDisponiveis(DoctorSchedule schedule) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime horarioAtual = schedule.getHorarioInicio();
        LocalTime horarioFim = schedule.getHorarioFim();
        int duracaoMinutos = schedule.getDuracaoConsultaMinutos();

        while (!horarioAtual.plusMinutes(duracaoMinutos).isAfter(horarioFim)) {
            slots.add(horarioAtual);
            horarioAtual = horarioAtual.plusMinutes(duracaoMinutos);
        }

        return slots;
    }
}
