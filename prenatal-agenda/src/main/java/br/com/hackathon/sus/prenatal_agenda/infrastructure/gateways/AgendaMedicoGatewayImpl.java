package br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.AgendaMedico;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AgendaMedicoGateway;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper.AgendaMedicoMapper;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity.AgendaMedicoEntity;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.repository.AgendaMedicoRepository;

import java.util.Optional;

public class AgendaMedicoGatewayImpl implements AgendaMedicoGateway {

    private final AgendaMedicoRepository repository;

    public AgendaMedicoGatewayImpl(AgendaMedicoRepository repository) {
        this.repository = repository;
    }

    @Override
    public AgendaMedico salvar(AgendaMedico agenda) {
        AgendaMedicoEntity entity = AgendaMedicoMapper.fromDomain(agenda);
        AgendaMedicoEntity saved = repository.save(entity);
        return AgendaMedicoMapper.toDomain(saved);
    }

    @Override
    public Optional<AgendaMedico> buscarPorId(Long id) {
        return repository.findById(id)
                .map(AgendaMedicoMapper::toDomain);
    }

    @Override
    public Optional<AgendaMedico> buscarPorMedicoId(Long medicoId) {
        return repository.findByMedicoId(medicoId)
                .map(AgendaMedicoMapper::toDomain);
    }
}
