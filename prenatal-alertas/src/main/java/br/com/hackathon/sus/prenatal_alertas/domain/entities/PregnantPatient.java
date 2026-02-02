package br.com.hackathon.sus.prenatal_alertas.domain.entities;

import java.util.List;

public class PregnantPatient {
    private String id;
    private String name;
    private String cpf;
    private Integer gestationalWeeks;
    private String email;
    private Boolean highRisk;
    private List<String> riskFactors;
    private String doctorName;
    private String doctorEmail;

    public PregnantPatient() {
    }

    public PregnantPatient(String id, String name, String cpf, Integer gestationalWeeks, String email,
                           Boolean highRisk, List<String> riskFactors) {
        this(id, name, cpf, gestationalWeeks, email, highRisk, riskFactors, null, null);
    }

    public PregnantPatient(String id, String name, String cpf, Integer gestationalWeeks, String email,
                           Boolean highRisk, List<String> riskFactors, String doctorName, String doctorEmail) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.gestationalWeeks = gestationalWeeks;
        this.email = email;
        this.highRisk = highRisk;
        this.riskFactors = riskFactors != null ? riskFactors : List.of();
        this.doctorName = doctorName;
        this.doctorEmail = doctorEmail;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public Integer getGestationalWeeks() { return gestationalWeeks; }
    public void setGestationalWeeks(Integer gestationalWeeks) { this.gestationalWeeks = gestationalWeeks; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Boolean getHighRisk() { return highRisk; }
    public void setHighRisk(Boolean highRisk) { this.highRisk = highRisk; }
    public List<String> getRiskFactors() { return riskFactors; }
    public void setRiskFactors(List<String> riskFactors) { this.riskFactors = riskFactors != null ? riskFactors : List.of(); }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorEmail() { return doctorEmail; }
    public void setDoctorEmail(String doctorEmail) { this.doctorEmail = doctorEmail; }

    public boolean hasRiskFactor() {
        return Boolean.TRUE.equals(highRisk) || (riskFactors != null && !riskFactors.isEmpty());
    }
}
