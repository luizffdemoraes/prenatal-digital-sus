package com.hackathon.sus.prenatal_prontuario.infrastructure.config.mapper;

import com.hackathon.sus.prenatal_prontuario.application.dtos.responses.MedicalRecordResponse;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.entity.MedicalRecordEntity;

import java.util.ArrayList;
import java.util.List;

public final class MedicalRecordMapper {

    private MedicalRecordMapper() {}

    public static MedicalRecord toDomain(MedicalRecordEntity e) {
        if (e == null) return null;
        return new MedicalRecord(
                e.getId(),
                e.getCpf(),
                e.getFullName(),
                e.getDateOfBirth(),
                e.getPregnantWomanId(),
                e.getAppointmentId(),
                e.getLastMenstrualPeriod(),
                e.getGestationalAgeWeeks(),
                e.getPregnancyType(),
                e.getPreviousPregnancies(),
                e.getPreviousDeliveries(),
                e.getPreviousAbortions(),
                e.getHighRiskPregnancy(),
                e.getHighRiskReason(),
                e.getRiskFactors() != null ? new ArrayList<>(e.getRiskFactors()) : new ArrayList<>(),
                e.getVitaminUse(),
                e.getAspirinUse(),
                e.getNotes(),
                e.getDeliveryType(),
                e.getCreatedAt(),
                e.getPatientEmail(),
                e.getDoctorName(),
                e.getDoctorEmail()
        );
    }

    public static MedicalRecordResponse toResponse(MedicalRecord m) {
        return MedicalRecordResponse.from(m);
    }

    public static MedicalRecordEntity fromDomain(MedicalRecord m) {
        if (m == null) return null;
        MedicalRecordEntity e = new MedicalRecordEntity();
        e.setId(m.getId());
        e.setCpf(m.getCpf());
        e.setFullName(m.getFullName());
        e.setDateOfBirth(m.getDateOfBirth());
        e.setPregnantWomanId(m.getPregnantWomanId());
        e.setAppointmentId(m.getAppointmentId());
        e.setLastMenstrualPeriod(m.getLastMenstrualPeriod());
        e.setGestationalAgeWeeks(m.getGestationalAgeWeeks());
        e.setPregnancyType(m.getPregnancyType());
        e.setPreviousPregnancies(m.getPreviousPregnancies() != null ? m.getPreviousPregnancies() : 0);
        e.setPreviousDeliveries(m.getPreviousDeliveries() != null ? m.getPreviousDeliveries() : 0);
        e.setPreviousAbortions(m.getPreviousAbortions() != null ? m.getPreviousAbortions() : 0);
        e.setHighRiskPregnancy(m.getHighRiskPregnancy() != null ? m.getHighRiskPregnancy() : false);
        e.setHighRiskReason(m.getHighRiskReason());
        e.setRiskFactors(m.getRiskFactors() != null ? new ArrayList<>(m.getRiskFactors()) : new ArrayList<>());
        e.setVitaminUse(m.getVitaminUse());
        e.setAspirinUse(m.getAspirinUse());
        e.setNotes(m.getNotes());
        e.setDeliveryType(m.getDeliveryType());
        e.setCreatedAt(m.getCreatedAt());
        e.setPatientEmail(m.getPatientEmail());
        e.setDoctorName(m.getDoctorName());
        e.setDoctorEmail(m.getDoctorEmail());
        return e;
    }

    public static void applyToEntity(MedicalRecordEntity e, MedicalRecord m) {
        if (e == null || m == null) return;
        e.setLastMenstrualPeriod(m.getLastMenstrualPeriod());
        e.setGestationalAgeWeeks(m.getGestationalAgeWeeks());
        e.setPregnancyType(m.getPregnancyType());
        e.setPreviousPregnancies(m.getPreviousPregnancies() != null ? m.getPreviousPregnancies() : 0);
        e.setPreviousDeliveries(m.getPreviousDeliveries() != null ? m.getPreviousDeliveries() : 0);
        e.setPreviousAbortions(m.getPreviousAbortions() != null ? m.getPreviousAbortions() : 0);
        e.setHighRiskPregnancy(m.getHighRiskPregnancy() != null ? m.getHighRiskPregnancy() : false);
        e.setHighRiskReason(m.getHighRiskReason());
        e.setVitaminUse(m.getVitaminUse() != null ? m.getVitaminUse() : false);
        e.setAspirinUse(m.getAspirinUse() != null ? m.getAspirinUse() : false);
        e.setNotes(m.getNotes());
        e.setDeliveryType(m.getDeliveryType());
        e.getRiskFactors().clear();
        e.getRiskFactors().addAll(m.getRiskFactors() != null ? m.getRiskFactors() : List.of());
        if (m.getPatientEmail() != null) e.setPatientEmail(m.getPatientEmail());
        if (m.getDoctorName() != null) e.setDoctorName(m.getDoctorName());
        if (m.getDoctorEmail() != null) e.setDoctorEmail(m.getDoctorEmail());
    }
}
