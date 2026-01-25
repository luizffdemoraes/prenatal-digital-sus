package com.hackathon.sus.prenatal_prontuario.application.usecases;

import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;

import java.util.Optional;
import java.util.UUID;

public interface FindMedicalRecordByPregnantWomanUseCase {

    Optional<MedicalRecord> execute(UUID pregnantWomanId);
}
