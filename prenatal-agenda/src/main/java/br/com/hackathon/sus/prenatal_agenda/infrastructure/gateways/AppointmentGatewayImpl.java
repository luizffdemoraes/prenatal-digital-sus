package br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.AppointmentStatus;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AppointmentGateway;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper.AppointmentMapper;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity.AppointmentEntity;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.repository.AppointmentRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AppointmentGatewayImpl implements AppointmentGateway {

    private final AppointmentRepository repository;

    public AppointmentGatewayImpl(AppointmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Appointment salvar(Appointment appointment) {
        AppointmentEntity entity = AppointmentMapper.fromDomain(appointment);
        AppointmentEntity saved = repository.save(entity);
        return AppointmentMapper.toDomain(saved);
    }

    @Override
    public Optional<Appointment> buscarPorId(Long id) {
        return repository.findById(id).map(AppointmentMapper::toDomain);
    }

    @Override
    public List<Appointment> buscarPorGestanteId(Long gestanteId) {
        return repository.findByGestanteId(gestanteId).stream()
                .map(AppointmentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> buscarConsultasAgendadas(Long medicoId, LocalDate data, LocalTime horario) {
        return repository.findByMedicoIdAndDataAndHorarioAndStatus(
                medicoId, data, horario, AppointmentStatus.AGENDADA).stream()
                .map(AppointmentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> buscarConsultasAgendadasPorMedicoEData(Long medicoId, LocalDate data) {
        return repository.findByMedicoIdAndDataAndStatus(
                medicoId, data, AppointmentStatus.AGENDADA).stream()
                .map(AppointmentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existeAgendamentoPorMedico(Long medicoId) {
        return repository.existsByMedicoIdAndStatus(medicoId, AppointmentStatus.AGENDADA);
    }
}
