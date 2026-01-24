package br.com.hackathon.sus.prenatal_agenda.domain.entities;

/**
 * Appointment status.
 * Constants kept for DB compatibility (@Enumerated STRING).
 */
public enum AppointmentStatus {
    AGENDADA("Agendada"),
    CANCELADA("Cancelada"),
    REALIZADA("Realizada");

    private final String descricao;

    AppointmentStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
