package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.application.dtos.requests.UpdateMedicalRecordRequest;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;

public interface UpdateMedicalRecordUseCase {

    MedicalRecord execute(String cpf, UpdateMedicalRecordRequest request, String professionalUserId);
}
