package br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.AppointmentStatus;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.CancellationReason;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "consulta", schema = "agenda")
public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gestante_id", nullable = false)
    private Long gestanteId;

    @Column(name = "medico_id", nullable = false)
    private Long medicoId;

    @Column(name = "unidade_id", nullable = false)
    private Long unidadeId;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_cancelamento")
    private CancellationReason motivoCancelamento;

    @Column(name = "data_agendamento", nullable = false)
    private LocalDateTime dataAgendamento;

    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;

    public AppointmentEntity() {
    }

    public AppointmentEntity(Long id, Long gestanteId, Long medicoId, Long unidadeId,
                             LocalDate data, LocalTime horario, AppointmentStatus status,
                             CancellationReason motivoCancelamento, LocalDateTime dataAgendamento,
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
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGestanteId() { return gestanteId; }
    public void setGestanteId(Long gestanteId) { this.gestanteId = gestanteId; }
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
