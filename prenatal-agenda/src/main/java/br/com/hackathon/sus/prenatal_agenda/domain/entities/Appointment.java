package br.com.hackathon.sus.prenatal_agenda.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Domain entity: medical appointment.
 * Business rules for scheduling and cancellation.
 */
public class Appointment {
    private Long id;
    private Long gestanteId;
    private String cpf;
    private Long medicoId;
    private Long unidadeId;
    private LocalDate data;
    private LocalTime horario;
    private AppointmentStatus status;
    private CancellationReason motivoCancelamento;
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataCancelamento;

    public Appointment(Long gestanteId, String cpf, Long medicoId, Long unidadeId, LocalDate data, LocalTime horario) {
        this.gestanteId = gestanteId;
        this.cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
        this.medicoId = medicoId;
        this.unidadeId = unidadeId;
        this.data = data;
        this.horario = horario;
        this.status = AppointmentStatus.AGENDADA;
        this.dataAgendamento = LocalDateTime.now();
        validar();
    }

    public Appointment(Long id, Long gestanteId, String cpf, Long medicoId, Long unidadeId,
                       LocalDate data, LocalTime horario, AppointmentStatus status,
                       CancellationReason motivoCancelamento, LocalDateTime dataAgendamento,
                       LocalDateTime dataCancelamento) {
        this.id = id;
        this.gestanteId = gestanteId;
        this.cpf = cpf;
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

    private void validar() {
        if (gestanteId == null) throw new IllegalArgumentException("ID da gestante é obrigatório");
        if (medicoId == null) throw new IllegalArgumentException("ID do médico é obrigatório");
        if (unidadeId == null) throw new IllegalArgumentException("ID da unidade é obrigatório");
        if (data == null) throw new IllegalArgumentException("Data da consulta é obrigatória");
        if (horario == null) throw new IllegalArgumentException("Horário da consulta é obrigatório");
        if (data.isBefore(LocalDate.now())) throw new IllegalArgumentException("Não é possível agendar consulta em data passada");
    }

    public void cancelar(CancellationReason motivo) {
        if (status == AppointmentStatus.CANCELADA) throw new IllegalStateException("Consulta já está cancelada");
        if (status == AppointmentStatus.REALIZADA) throw new IllegalStateException("Não é possível cancelar uma consulta já realizada");
        if (motivo == null) throw new IllegalArgumentException("Motivo do cancelamento é obrigatório");
        this.status = AppointmentStatus.CANCELADA;
        this.motivoCancelamento = motivo;
        this.dataCancelamento = LocalDateTime.now();
    }

    public boolean estaAgendada() { return status == AppointmentStatus.AGENDADA; }
    public boolean estaCancelada() { return status == AppointmentStatus.CANCELADA; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGestanteId() { return gestanteId; }
    public void setGestanteId(Long gestanteId) { this.gestanteId = gestanteId; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public Long getUnidadeId() { return unidadeId; }
    public void setUnidadeId(Long unidadeId) { this.unidadeId = unidadeId; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    public CancellationReason getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(CancellationReason motivoCancelamento) { this.motivoCancelamento = motivoCancelamento; }
    public LocalDateTime getDataAgendamento() { return dataAgendamento; }
    public void setDataAgendamento(LocalDateTime dataAgendamento) { this.dataAgendamento = dataAgendamento; }
    public LocalDateTime getDataCancelamento() { return dataCancelamento; }
    public void setDataCancelamento(LocalDateTime dataCancelamento) { this.dataCancelamento = dataCancelamento; }
}
