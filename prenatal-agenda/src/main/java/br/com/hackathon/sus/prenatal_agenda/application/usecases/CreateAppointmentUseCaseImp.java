package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.CreateAppointmentRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Weekday;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AppointmentGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorScheduleGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.PatientGateway;

import java.util.List;

public class CreateAppointmentUseCaseImp implements CreateAppointmentUseCase {

    private final PatientGateway patientGateway;
    private final DoctorGateway doctorGateway;
    private final AppointmentGateway appointmentGateway;
    private final DoctorScheduleGateway doctorScheduleGateway;

    public CreateAppointmentUseCaseImp(PatientGateway patientGateway,
                                       DoctorGateway doctorGateway,
                                       AppointmentGateway appointmentGateway,
                                       DoctorScheduleGateway doctorScheduleGateway) {
        this.patientGateway = patientGateway;
        this.doctorGateway = doctorGateway;
        this.appointmentGateway = appointmentGateway;
        this.doctorScheduleGateway = doctorScheduleGateway;
    }

    @Override
    public Appointment execute(CreateAppointmentRequest req, Long unidadeId) {
        if (unidadeId == null) {
            throw new IllegalArgumentException("Unidade (UBS) é obrigatória. Envie o header X-Unidade-Id.");
        }
        Long gestanteId = resolveGestanteId(req);
        Long medicoId = resolveMedicoId(req, unidadeId);

        Appointment appointment = new Appointment(gestanteId, medicoId, unidadeId, req.date(), req.time());

        var schedule = doctorScheduleGateway.buscarPorMedicoId(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada para o médico informado"));

        Weekday diaSemana = Weekday.fromDayOfWeek(appointment.getData().getDayOfWeek());
        if (!schedule.atendeNoDia(diaSemana)) {
            throw new IllegalArgumentException("Médico não atende neste dia da semana");
        }

        if (!schedule.horarioDentroDoPeriodo(appointment.getHorario())) {
            throw new IllegalArgumentException("Horário fora do período de atendimento do médico");
        }

        List<Appointment> existentes = appointmentGateway.buscarConsultasAgendadas(
                medicoId, appointment.getData(), appointment.getHorario());
        if (!existentes.isEmpty()) {
            throw new IllegalArgumentException("Horário já está ocupado");
        }

        return appointmentGateway.salvar(appointment);
    }

    private Long resolveGestanteId(CreateAppointmentRequest req) {
        String cpf = req.patientCpf() != null ? req.patientCpf().trim() : "";
        if (cpf.isBlank()) throw new IllegalArgumentException("CPF da gestante é obrigatório.");
        return patientGateway.buscarPorCpf(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Gestante não encontrada para o CPF informado."));
    }

    private Long resolveMedicoId(CreateAppointmentRequest req, Long unidadeId) {
        if (req.crm() != null && !req.crm().isBlank()) {
            return doctorGateway.buscarPorCrm(req.crm().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado para o CRM informado."));
        }
        if (req.doctorName() != null && !req.doctorName().isBlank()) {
            return doctorGateway.buscarPorNome(req.doctorName().trim(), unidadeId)
                    .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado: \"" + req.doctorName() + "\"."));
        }
        if (req.specialty() != null && !req.specialty().isBlank()) {
            return doctorGateway.buscarPorEspecialidade(req.specialty().trim(), unidadeId)
                    .orElseThrow(() -> new IllegalArgumentException("Nenhum médico encontrado para a especialidade: \"" + req.specialty() + "\"."));
        }
        throw new IllegalArgumentException("Informe o nome, a especialidade ou o CRM do médico.");
    }
}
