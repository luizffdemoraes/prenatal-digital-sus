package br.com.hackathon.sus.prenatal_agenda.domain.entities;

/**
 * Enum para status da consulta
 */
public enum StatusConsulta {
    AGENDADA("Agendada"),
    CANCELADA("Cancelada"),
    REALIZADA("Realizada");

    private final String descricao;

    StatusConsulta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
