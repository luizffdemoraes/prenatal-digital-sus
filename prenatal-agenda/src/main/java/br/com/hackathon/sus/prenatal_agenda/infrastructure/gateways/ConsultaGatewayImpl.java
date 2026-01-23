package br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.StatusConsulta;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.ConsultaGateway;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper.ConsultaMapper;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity.ConsultaEntity;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.repository.ConsultaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConsultaGatewayImpl implements ConsultaGateway {

    private final ConsultaRepository repository;

    public ConsultaGatewayImpl(ConsultaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Consulta salvar(Consulta consulta) {
        ConsultaEntity entity = ConsultaMapper.fromDomain(consulta);
        ConsultaEntity saved = repository.save(entity);
        return ConsultaMapper.toDomain(saved);
    }

    @Override
    public Optional<Consulta> buscarPorId(Long id) {
        return repository.findById(id)
                .map(ConsultaMapper::toDomain);
    }

    @Override
    public List<Consulta> buscarPorGestanteId(Long gestanteId) {
        return repository.findByGestanteId(gestanteId).stream()
                .map(ConsultaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Consulta> buscarConsultasAgendadas(Long medicoId, LocalDate data, LocalTime horario) {
        return repository.findByMedicoIdAndDataAndHorarioAndStatus(
                medicoId, data, horario, StatusConsulta.AGENDADA).stream()
                .map(ConsultaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Consulta> buscarConsultasAgendadasPorMedicoEData(Long medicoId, LocalDate data) {
        return repository.findByMedicoIdAndDataAndStatus(
                medicoId, data, StatusConsulta.AGENDADA).stream()
                .map(ConsultaMapper::toDomain)
                .collect(Collectors.toList());
    }
}
