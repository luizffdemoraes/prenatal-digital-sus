package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecordHistory;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordGateway;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordHistoryGateway;
import com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions.ResourceNotFoundException;

import java.util.List;

public class FindMedicalRecordHistoryUseCaseImp implements FindMedicalRecordHistoryUseCase {

    private final MedicalRecordHistoryGateway historyGateway;
    private final MedicalRecordGateway medicalRecordGateway;

    public FindMedicalRecordHistoryUseCaseImp(MedicalRecordHistoryGateway historyGateway,
                                              MedicalRecordGateway medicalRecordGateway) {
        this.historyGateway = historyGateway;
        this.medicalRecordGateway = medicalRecordGateway;
    }

    @Override
    public List<MedicalRecordHistory> execute(String cpf) {
        var m = medicalRecordGateway.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado para o CPF informado."));
        return historyGateway.listByMedicalRecordId(m.getId());
    }
}
