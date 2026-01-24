package br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorScheduleGateway;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper.DoctorScheduleMapper;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity.DoctorScheduleEntity;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.repository.DoctorScheduleRepository;

import java.util.Optional;

public class DoctorScheduleGatewayImpl implements DoctorScheduleGateway {

    private final DoctorScheduleRepository repository;

    public DoctorScheduleGatewayImpl(DoctorScheduleRepository repository) {
        this.repository = repository;
    }

    @Override
    public DoctorSchedule salvar(DoctorSchedule schedule) {
        DoctorScheduleEntity entity = DoctorScheduleMapper.fromDomain(schedule);
        DoctorScheduleEntity saved = repository.save(entity);
        return DoctorScheduleMapper.toDomain(saved);
    }

    @Override
    public Optional<DoctorSchedule> buscarPorId(Long id) {
        return repository.findById(id).map(DoctorScheduleMapper::toDomain);
    }

    @Override
    public Optional<DoctorSchedule> buscarPorMedicoId(Long medicoId) {
        return repository.findByMedicoId(medicoId).map(DoctorScheduleMapper::toDomain);
    }

    @Override
    public void excluirPorId(Long id) {
        repository.deleteById(id);
    }
}
