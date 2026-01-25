package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordGateway;

import java.util.Optional;

public class FindMedicalRecordByCpfUseCaseImp implements FindMedicalRecordByCpfUseCase {

    private final MedicalRecordGateway medicalRecordGateway;

    public FindMedicalRecordByCpfUseCaseImp(MedicalRecordGateway medicalRecordGateway) {
        this.medicalRecordGateway = medicalRecordGateway;
    }

    @Override
    public Optional<MedicalRecord> execute(String cpf) {
        return medicalRecordGateway.findByCpf(cpf);
    }
}
