package br.com.hackathon.sus.prenatal_agenda.domain.entities;

/**
 * Cancellation reason for appointments.
 * Constants kept for DB compatibility (@Enumerated STRING).
 */
public enum CancellationReason {
    GESTANTE_DESISTIU("Gestante desistiu"),
    MEDICO_INDISPONIVEL("Médico indisponível"),
    EMERGENCIA("Emergência"),
    OUTRO("Outro");

    private final String descricao;

    CancellationReason(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
