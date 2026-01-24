package br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Weekday;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "agenda_medico", schema = "agenda")
public class DoctorScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medico_id", nullable = false)
    private Long medicoId;

    @Column(name = "unidade_id", nullable = false)
    private Long unidadeId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "agenda_dias_atendimento",
                    joinColumns = @JoinColumn(name = "agenda_id"),
                    schema = "agenda")
    @Column(name = "dia_semana")
    @Enumerated(EnumType.STRING)
    private Set<Weekday> diasAtendimento = new HashSet<>();

    @Column(name = "horario_inicio", nullable = false)
    private LocalTime horarioInicio;

    @Column(name = "horario_fim", nullable = false)
    private LocalTime horarioFim;

    @Column(name = "duracao_consulta_minutos", nullable = false)
    private Integer duracaoConsultaMinutos;

    public DoctorScheduleEntity() {
    }

    public DoctorScheduleEntity(Long id, Long medicoId, Long unidadeId, Set<Weekday> diasAtendimento,
                                LocalTime horarioInicio, LocalTime horarioFim, Integer duracaoConsultaMinutos) {
        this.id = id;
        this.medicoId = medicoId;
        this.unidadeId = unidadeId;
        this.diasAtendimento = new HashSet<>(diasAtendimento);
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.duracaoConsultaMinutos = duracaoConsultaMinutos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMedicoId() { return medicoId; }
    public void setMedicoId(Long medicoId) { this.medicoId = medicoId; }
    public Long getUnidadeId() { return unidadeId; }
    public void setUnidadeId(Long unidadeId) { this.unidadeId = unidadeId; }
    public Set<Weekday> getDiasAtendimento() { return new HashSet<>(diasAtendimento); }
    public void setDiasAtendimento(Set<Weekday> diasAtendimento) { this.diasAtendimento = new HashSet<>(diasAtendimento); }
    public LocalTime getHorarioInicio() { return horarioInicio; }
    public void setHorarioInicio(LocalTime horarioInicio) { this.horarioInicio = horarioInicio; }
    public LocalTime getHorarioFim() { return horarioFim; }
    public void setHorarioFim(LocalTime horarioFim) { this.horarioFim = horarioFim; }
    public Integer getDuracaoConsultaMinutos() { return duracaoConsultaMinutos; }
    public void setDuracaoConsultaMinutos(Integer duracaoConsultaMinutos) { this.duracaoConsultaMinutos = duracaoConsultaMinutos; }
}
