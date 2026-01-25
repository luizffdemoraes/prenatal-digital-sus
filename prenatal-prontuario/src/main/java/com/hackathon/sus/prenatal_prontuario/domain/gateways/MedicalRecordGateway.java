package com.hackathon.sus.prenatal_prontuario.domain.gateways;

import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;

import java.util.Optional;
import java.util.UUID;

public interface MedicalRecordGateway {

    MedicalRecord save(MedicalRecord medicalRecord);

    Optional<MedicalRecord> findById(UUID id);

    Optional<MedicalRecord> findByPregnantWomanId(UUID pregnantWomanId);

    Optional<MedicalRecord> findByCpf(String cpf);

    boolean existsByPregnantWomanId(UUID pregnantWomanId);

    boolean existsByCpf(String cpf);

    MedicalRecord update(MedicalRecord medicalRecord);
}
