package br.com.hackathon.sus.prenatal_ia.domain.entities;

public class PregnantPatient {
    private String id;
    private String name;
    private String cpf;
    private Integer gestationalWeeks;
    private String email;

    public PregnantPatient() {
    }

    public PregnantPatient(String id, String name, String cpf, Integer gestationalWeeks, String email) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.gestationalWeeks = gestationalWeeks;
        this.email = email;
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
}
