package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.application.dtos.requests.CreateMedicalRecordRequest;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecordHistory;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordGateway;
import com.hackathon.sus.prenatal_prontuario.domain.gateways.MedicalRecordHistoryGateway;
import com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions.BusinessException;

public class CreateMedicalRecordUseCaseImp implements CreateMedicalRecordUseCase {

    private final MedicalRecordGateway medicalRecordGateway;
    private final MedicalRecordHistoryGateway historyGateway;

    public CreateMedicalRecordUseCaseImp(MedicalRecordGateway medicalRecordGateway,
                                         MedicalRecordHistoryGateway historyGateway) {
        this.medicalRecordGateway = medicalRecordGateway;
        this.historyGateway = historyGateway;
    }

    @Override
    public MedicalRecord execute(CreateMedicalRecordRequest request, String professionalUserId) {
        if (medicalRecordGateway.existsByCpf(request.cpf())) {
            throw new BusinessException("Já existe prontuário para este CPF. Um prontuário por gestação.");
        }

        MedicalRecord m = MedicalRecord.fromFirstAppointment(
                request.cpf(),
                request.fullName(),
                request.dateOfBirth(),
                request.lastMenstrualPeriod(),
                request.pregnancyType(),
                request.previousPregnancies(),
                request.previousDeliveries(),
                request.previousAbortions(),
                request.highRiskPregnancy(),
                request.highRiskReason(),
                request.riskFactors(),
                request.vitaminUse(),
                request.aspirinUse(),
                request.notes(),
                request.consultationDate()
        );

        MedicalRecord saved = medicalRecordGateway.save(m);

        historyGateway.register(new MedicalRecordHistory(
                saved.getId(),
                professionalUserId != null ? professionalUserId : "sistema",
                "Prontuário criado na primeira consulta"
        ));

        return saved;
    }
}
