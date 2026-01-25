package com.hackathon.sus.prenatal_prontuario.infrastructure.gateways;

import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordGateway;
import com.hackathon.sus.prenatal_prontuario.infrastructure.config.mapper.MedicalRecordMapper;
import com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions.ResourceNotFoundException;
import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.entity.MedicalRecordEntity;
import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.repository.MedicalRecordRepository;

import java.util.Optional;
import java.util.UUID;

public class MedicalRecordGatewayImpl implements MedicalRecordGateway {

    private final MedicalRecordRepository repository;

    public MedicalRecordGatewayImpl(MedicalRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public MedicalRecord save(MedicalRecord medicalRecord) {
        MedicalRecordEntity e = MedicalRecordMapper.fromDomain(medicalRecord);
        if (e.getId() == null) e.setId(UUID.randomUUID());
        return MedicalRecordMapper.toDomain(repository.save(e));
    }

    @Override
    public Optional<MedicalRecord> findById(UUID id) {
        return repository.findById(id).map(MedicalRecordMapper::toDomain);
    }

    @Override
    public Optional<MedicalRecord> findByPregnantWomanId(UUID pregnantWomanId) {
        return repository.findByPregnantWomanId(pregnantWomanId).map(MedicalRecordMapper::toDomain);
    }

    @Override
    public Optional<MedicalRecord> findByCpf(String cpf) {
        return repository.findByCpf(cpf).map(MedicalRecordMapper::toDomain);
    }

    @Override
    public boolean existsByPregnantWomanId(UUID pregnantWomanId) {
        return repository.existsByPregnantWomanId(pregnantWomanId);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return repository.existsByCpf(cpf);
    }

    @Override
    public MedicalRecord update(MedicalRecord medicalRecord) {
        MedicalRecordEntity e = repository.findById(medicalRecord.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado: " + medicalRecord.getId()));
        MedicalRecordMapper.applyToEntity(e, medicalRecord);
        return MedicalRecordMapper.toDomain(repository.save(e));
    }
}
