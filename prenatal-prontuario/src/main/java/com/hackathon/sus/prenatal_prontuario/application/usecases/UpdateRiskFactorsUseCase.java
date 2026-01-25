package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.application.dtos.requests.UpdateRiskFactorsRequest;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;

public interface UpdateRiskFactorsUseCase {

    MedicalRecord execute(String cpf, UpdateRiskFactorsRequest request, String professionalUserId);
}
