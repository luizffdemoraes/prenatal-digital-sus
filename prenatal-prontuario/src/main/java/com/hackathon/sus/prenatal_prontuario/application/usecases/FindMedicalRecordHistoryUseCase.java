package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecordHistory;

import java.util.List;

public interface FindMedicalRecordHistoryUseCase {

    List<MedicalRecordHistory> execute(String cpf);
}
