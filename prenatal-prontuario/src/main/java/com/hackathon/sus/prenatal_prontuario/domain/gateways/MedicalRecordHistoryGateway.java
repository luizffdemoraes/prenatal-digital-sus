package com.hackathon.sus.prenatal_prontuario.domain.gateways;

import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecordHistory;

import java.util.List;
import java.util.UUID;

public interface MedicalRecordHistoryGateway {

    MedicalRecordHistory register(MedicalRecordHistory history);

    List<MedicalRecordHistory> listByMedicalRecordId(UUID medicalRecordId);
}
