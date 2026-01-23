package br.com.hackathon.sus.prenatal_agenda.domain.entities;

/**
 * Enum para motivos de cancelamento de consulta
 */
public enum MotivoCancelamento {
    GESTANTE_DESISTIU("Gestante desistiu"),
    MEDICO_INDISPONIVEL("Médico indisponível"),
    EMERGENCIA("Emergência"),
    OUTRO("Outro");

    private final String descricao;

    MotivoCancelamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
