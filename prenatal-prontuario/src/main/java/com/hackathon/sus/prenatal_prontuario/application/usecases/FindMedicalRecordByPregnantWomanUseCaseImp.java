package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordGateway;

import java.util.Optional;
import java.util.UUID;

public class FindMedicalRecordByPregnantWomanUseCaseImp implements FindMedicalRecordByPregnantWomanUseCase {

    private final MedicalRecordGateway medicalRecordGateway;

    public FindMedicalRecordByPregnantWomanUseCaseImp(MedicalRecordGateway medicalRecordGateway) {
        this.medicalRecordGateway = medicalRecordGateway;
    }

    @Override
    public Optional<MedicalRecord> execute(UUID pregnantWomanId) {
        return medicalRecordGateway.findByPregnantWomanId(pregnantWomanId);
    }
}
