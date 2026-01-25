package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.application.dtos.requests.UpdateRiskFactorsRequest;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecordHistory;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordGateway;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordHistoryGateway;
import com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions.ResourceNotFoundException;

public class UpdateRiskFactorsUseCaseImp implements UpdateRiskFactorsUseCase {

    private final MedicalRecordGateway medicalRecordGateway;
    private final MedicalRecordHistoryGateway historyGateway;

    public UpdateRiskFactorsUseCaseImp(MedicalRecordGateway medicalRecordGateway,
                                       MedicalRecordHistoryGateway historyGateway) {
        this.medicalRecordGateway = medicalRecordGateway;
        this.historyGateway = historyGateway;
    }

    @Override
    public MedicalRecord execute(String cpf, UpdateRiskFactorsRequest request, String professionalUserId) {
        MedicalRecord m = medicalRecordGateway.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado para o CPF informado."));

        m.updateRiskFactors(request.riskFactors());
        MedicalRecord updated = medicalRecordGateway.update(m);

        historyGateway.register(new MedicalRecordHistory(
                updated.getId(),
                professionalUserId != null ? professionalUserId : "sistema",
                "Fatores de risco atualizados"
        ));

        return updated;
    }
}
