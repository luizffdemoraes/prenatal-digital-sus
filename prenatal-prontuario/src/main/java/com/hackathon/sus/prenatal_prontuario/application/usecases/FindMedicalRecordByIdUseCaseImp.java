package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordGateway;

import java.util.Optional;
import java.util.UUID;

public class FindMedicalRecordByIdUseCaseImp implements FindMedicalRecordByIdUseCase {

    private final MedicalRecordGateway medicalRecordGateway;

    public FindMedicalRecordByIdUseCaseImp(MedicalRecordGateway medicalRecordGateway) {
        this.medicalRecordGateway = medicalRecordGateway;
    }

    @Override
    public Optional<MedicalRecord> execute(UUID medicalRecordId) {
        return medicalRecordGateway.findById(medicalRecordId);
    }
}
