package com.hackathon.sus.prenatal_prontuario.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain entity: prontuário da gestante.
 * Um prontuário por gestação (por CPF). Identificação por cpf ou pregnantWomanId (quando vinculado ao auth).
 */
public class MedicalRecord {

    private UUID id;
    private String cpf;
    private String fullName;
    private LocalDate dateOfBirth;
    private UUID pregnantWomanId;
    private UUID appointmentId;
    private LocalDate lastMenstrualPeriod;
    private Integer gestationalAgeWeeks;
    private PregnancyType pregnancyType;
    private Integer previousPregnancies;
    private Integer previousDeliveries;
    private Integer previousAbortions;
    private Boolean highRiskPregnancy;
    private String highRiskReason;
    private List<RiskFactor> riskFactors;
    private Boolean vitaminUse;
    private Boolean aspirinUse;
    private String notes;
    private DeliveryType deliveryType;
    private LocalDateTime createdAt;
    private String patientEmail;
    private String doctorName;
    private String doctorEmail;

    /**
     * Cria prontuário a partir dos dados da primeira consulta (modelo por CPF).
     * Idade gestacional = semanas entre dataUltimaMenstruacao e referenceDate (ou hoje se null).
     */
    public static MedicalRecord fromFirstAppointment(
            String cpf, String fullName, LocalDate dateOfBirth, LocalDate lastMenstrualPeriod,
            PregnancyType pregnancyType, Integer previousPregnancies, Integer previousDeliveries, Integer previousAbortions,
            Boolean highRiskPregnancy, String highRiskReason, List<RiskFactor> riskFactors,
            Boolean vitaminUse, Boolean aspirinUse, String notes, DeliveryType deliveryType, LocalDate referenceDate,
            String patientEmail, String doctorName, String doctorEmail) {
        if (cpf == null || cpf.isBlank()) throw new IllegalArgumentException("CPF é obrigatório");
        if (lastMenstrualPeriod == null) throw new IllegalArgumentException("dataUltimaMenstruacao é obrigatória");
        LocalDate ref = referenceDate != null ? referenceDate : LocalDate.now();
        int weeks = (int) ChronoUnit.WEEKS.between(lastMenstrualPeriod, ref);
        if (weeks < 1 || weeks > 44) {
            LocalDate minDum = ref.minusWeeks(44);
            LocalDate maxDum = ref.minusWeeks(1);
            throw new IllegalArgumentException(
                    "Idade gestacional calculada: " + weeks + " semanas. Deve ser entre 1 e 44. dataUltimaMenstruacao: " + lastMenstrualPeriod
                            + ". Para a data de referência " + ref + ", use dataUltimaMenstruacao entre " + minDum + " e " + maxDum
                            + (referenceDate == null ? "" : ". Ou informe dataConsulta no body para simular outra data.")
            );
        }

        return new MedicalRecord(
                null, cpf, fullName, dateOfBirth, null, null,
                lastMenstrualPeriod, weeks,
                pregnancyType != null ? pregnancyType : PregnancyType.SINGLETON,
                previousPregnancies != null ? previousPregnancies : 0,
                previousDeliveries != null ? previousDeliveries : 0,
                previousAbortions != null ? previousAbortions : 0,
                highRiskPregnancy != null ? highRiskPregnancy : false,
                highRiskReason,
                riskFactors != null ? new ArrayList<>(riskFactors) : new ArrayList<>(),
                vitaminUse != null ? vitaminUse : false,
                aspirinUse != null ? aspirinUse : false,
                notes,
                deliveryType,
                LocalDateTime.now(),
                patientEmail,
                doctorName,
                doctorEmail
        );
    }

    /** Construtor completo (mapper e carga). */
    public MedicalRecord(UUID id, String cpf, String fullName, LocalDate dateOfBirth,
                         UUID pregnantWomanId, UUID appointmentId,
                         LocalDate lastMenstrualPeriod, Integer gestationalAgeWeeks, PregnancyType pregnancyType,
                         Integer previousPregnancies, Integer previousDeliveries, Integer previousAbortions,
                         Boolean highRiskPregnancy, String highRiskReason, List<RiskFactor> riskFactors,
                         Boolean vitaminUse, Boolean aspirinUse, String notes, DeliveryType deliveryType, LocalDateTime createdAt,
                         String patientEmail, String doctorName, String doctorEmail) {
        this.id = id;
        this.cpf = cpf;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.pregnantWomanId = pregnantWomanId;
        this.appointmentId = appointmentId;
        this.lastMenstrualPeriod = lastMenstrualPeriod;
        this.gestationalAgeWeeks = gestationalAgeWeeks;
        this.pregnancyType = pregnancyType != null ? pregnancyType : PregnancyType.SINGLETON;
        this.previousPregnancies = previousPregnancies != null ? previousPregnancies : 0;
        this.previousDeliveries = previousDeliveries != null ? previousDeliveries : 0;
        this.previousAbortions = previousAbortions != null ? previousAbortions : 0;
        this.highRiskPregnancy = highRiskPregnancy != null ? highRiskPregnancy : false;
        this.highRiskReason = highRiskReason;
        this.riskFactors = riskFactors != null ? new ArrayList<>(riskFactors) : new ArrayList<>();
        this.vitaminUse = vitaminUse != null ? vitaminUse : false;
        this.aspirinUse = aspirinUse != null ? aspirinUse : false;
        this.notes = notes;
        this.deliveryType = deliveryType;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.patientEmail = patientEmail;
        this.doctorName = doctorName;
        this.doctorEmail = doctorEmail;
        validate();
    }

    private void validate() {
        if (gestationalAgeWeeks != null && (gestationalAgeWeeks < 1 || gestationalAgeWeeks > 44)) {
            throw new IllegalArgumentException("Idade gestacional em semanas deve ser entre 1 e 44");
        }
    }

    public void updateClinicalData(Boolean vitaminUse, Boolean aspirinUse, String notes, DeliveryType deliveryType) {
        if (vitaminUse != null) this.vitaminUse = vitaminUse;
        if (aspirinUse != null) this.aspirinUse = aspirinUse;
        if (notes != null) this.notes = notes;
        if (deliveryType != null) this.deliveryType = deliveryType;
    }

    public void updateContactAndDoctorData(String patientEmail, String doctorName, String doctorEmail) {
        if (patientEmail != null) this.patientEmail = patientEmail;
        if (doctorName != null) this.doctorName = doctorName;
        if (doctorEmail != null) this.doctorEmail = doctorEmail;
    }

    public void updateRiskFactors(List<RiskFactor> riskFactors) {
        this.riskFactors = riskFactors != null ? new ArrayList<>(riskFactors) : new ArrayList<>();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCpf() { return cpf; }
    public String getFullName() { return fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public UUID getPregnantWomanId() { return pregnantWomanId; }
    public UUID getAppointmentId() { return appointmentId; }
    public LocalDate getLastMenstrualPeriod() { return lastMenstrualPeriod; }
    public Integer getGestationalAgeWeeks() { return gestationalAgeWeeks; }
    public PregnancyType getPregnancyType() { return pregnancyType; }
    public Integer getPreviousPregnancies() { return previousPregnancies; }
    public Integer getPreviousDeliveries() { return previousDeliveries; }
    public Integer getPreviousAbortions() { return previousAbortions; }
    public Boolean getHighRiskPregnancy() { return highRiskPregnancy; }
    public String getHighRiskReason() { return highRiskReason; }
    /** Retorna cópia imutável para evitar mutação externa da lista interna. */
    public List<RiskFactor> getRiskFactors() {
        return riskFactors == null ? List.of() : List.copyOf(riskFactors);
    }
    public Boolean getVitaminUse() { return vitaminUse; }
    public Boolean getAspirinUse() { return aspirinUse; }
    public String getNotes() { return notes; }
    public DeliveryType getDeliveryType() { return deliveryType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorEmail() { return doctorEmail; }
    public void setDoctorEmail(String doctorEmail) { this.doctorEmail = doctorEmail; }
}
