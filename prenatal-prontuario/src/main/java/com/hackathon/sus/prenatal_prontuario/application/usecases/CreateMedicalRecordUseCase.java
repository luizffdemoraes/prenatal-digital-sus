package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.application.dtos.requests.CreateMedicalRecordRequest;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;

public interface CreateMedicalRecordUseCase {

    MedicalRecord execute(CreateMedicalRecordRequest request, String professionalUserId);
}
