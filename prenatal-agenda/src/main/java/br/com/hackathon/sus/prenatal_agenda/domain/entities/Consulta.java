package br.com.hackathon.sus.prenatal_agenda.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entidade de domínio que representa uma consulta médica
 * Contém as regras de negócio para agendamento e cancelamento
 */
public class Consulta {
    private Long id;
    private Long gestanteId;
    private Long medicoId;
    private Long unidadeId;
    private LocalDate data;
    private LocalTime horario;
    private StatusConsulta status;
    private MotivoCancelamento motivoCancelamento;
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataCancelamento;

    public Consulta(Long gestanteId, Long medicoId, Long unidadeId, 
                   LocalDate data, LocalTime horario) {
        this.gestanteId = gestanteId;
        this.medicoId = medicoId;
        this.unidadeId = unidadeId;
        this.data = data;
        this.horario = horario;
        this.status = StatusConsulta.AGENDADA;
        this.dataAgendamento = LocalDateTime.now();
        validar();
    }

    public Consulta(Long id, Long gestanteId, Long medicoId, Long unidadeId,
                   LocalDate data, LocalTime horario, StatusConsulta status,
                   MotivoCancelamento motivoCancelamento, LocalDateTime dataAgendamento,
                   LocalDateTime dataCancelamento) {
        this.id = id;
        this.gestanteId = gestanteId;
        this.medicoId = medicoId;
        this.unidadeId = unidadeId;
        this.data = data;
        this.horario = horario;
        this.status = status;
        this.motivoCancelamento = motivoCancelamento;
        this.dataAgendamento = dataAgendamento;
        this.dataCancelamento = dataCancelamento;
        validar();
    }

    /**
     * Valida as regras de negócio da consulta
     */
    private void validar() {
        if (gestanteId == null) {
            throw new IllegalArgumentException("ID da gestante é obrigatório");
        }
        if (medicoId == null) {
            throw new IllegalArgumentException("ID do médico é obrigatório");
        }
        if (unidadeId == null) {
            throw new IllegalArgumentException("ID da unidade é obrigatório");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data da consulta é obrigatória");
        }
        if (horario == null) {
            throw new IllegalArgumentException("Horário da consulta é obrigatório");
        }
        if (data.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Não é possível agendar consulta em data passada");
        }
    }

    /**
     * Cancela a consulta com um motivo
     */
    public void cancelar(MotivoCancelamento motivo) {
        if (status == StatusConsulta.CANCELADA) {
            throw new IllegalStateException("Consulta já está cancelada");
        }
        if (status == StatusConsulta.REALIZADA) {
            throw new IllegalStateException("Não é possível cancelar uma consulta já realizada");
        }
        if (motivo == null) {
            throw new IllegalArgumentException("Motivo do cancelamento é obrigatório");
        }
        this.status = StatusConsulta.CANCELADA;
        this.motivoCancelamento = motivo;
        this.dataCancelamento = LocalDateTime.now();
    }

    /**
     * Verifica se a consulta está agendada
     */
    public boolean estaAgendada() {
        return status == StatusConsulta.AGENDADA;
    }

    /**
     * Verifica se a consulta está cancelada
     */
    public boolean estaCancelada() {
        return status == StatusConsulta.CANCELADA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGestanteId() {
        return gestanteId;
    }

    public void setGestanteId(Long gestanteId) {
        this.gestanteId = gestanteId;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public StatusConsulta getStatus() {
        return status;
    }

    public void setStatus(StatusConsulta status) {
        this.status = status;
    }

    public MotivoCancelamento getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(MotivoCancelamento motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public LocalDateTime getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDateTime dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalDateTime getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(LocalDateTime dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }
}
