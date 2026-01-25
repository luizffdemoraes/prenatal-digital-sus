package com.hackathon.sus.prenatal_prontuario.infrastructure.gateways;

import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecordHistory;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordHistoryGateway;
import com.hackathon.sus.prenatal_prontuario.infrastructure.config.mapper.MedicalRecordHistoryMapper;
import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.entity.MedicalRecordHistoryEntity;
import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.repository.MedicalRecordHistoryRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MedicalRecordHistoryGatewayImpl implements MedicalRecordHistoryGateway {

    private final MedicalRecordHistoryRepository repository;

    public MedicalRecordHistoryGatewayImpl(MedicalRecordHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public MedicalRecordHistory register(MedicalRecordHistory history) {
        MedicalRecordHistoryEntity e = MedicalRecordHistoryMapper.fromDomain(history);
        if (e.getId() == null) e.setId(UUID.randomUUID());
        return MedicalRecordHistoryMapper.toDomain(repository.save(e));
    }

    @Override
    public List<MedicalRecordHistory> listByMedicalRecordId(UUID medicalRecordId) {
        return repository.findByMedicalRecordIdOrderByOccurredAtDesc(medicalRecordId).stream()
                .map(MedicalRecordHistoryMapper::toDomain)
                .collect(Collectors.toList());
    }
}
