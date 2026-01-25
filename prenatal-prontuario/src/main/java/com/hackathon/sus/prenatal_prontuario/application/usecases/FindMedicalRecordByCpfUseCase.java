package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;

import java.util.Optional;

public interface FindMedicalRecordByCpfUseCase {

    Optional<MedicalRecord> execute(String cpf);
}
