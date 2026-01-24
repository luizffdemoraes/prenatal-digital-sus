package br.com.hackathon.sus.prenatal_agenda.domain.entities;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Domain entity: doctor's weekly schedule.
 * Business rules for availability.
 */
public class DoctorSchedule {
    private Long id;
    private Long medicoId;
    private Long unidadeId;
    private Set<Weekday> diasAtendimento;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private Integer duracaoConsultaMinutos;

    public DoctorSchedule(Long medicoId, Long unidadeId, Set<Weekday> diasAtendimento,
                          LocalTime horarioInicio, LocalTime horarioFim, Integer duracaoConsultaMinutos) {
        this.medicoId = medicoId;
        this.unidadeId = unidadeId;
        this.diasAtendimento = new HashSet<>(diasAtendimento);
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.duracaoConsultaMinutos = duracaoConsultaMinutos;
        validar();
    }

    public DoctorSchedule(Long id, Long medicoId, Long unidadeId, Set<Weekday> diasAtendimento,
                          LocalTime horarioInicio, LocalTime horarioFim, Integer duracaoConsultaMinutos) {
        this.id = id;
        this.medicoId = medicoId;
        this.unidadeId = unidadeId;
        this.diasAtendimento = new HashSet<>(diasAtendimento);
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.duracaoConsultaMinutos = duracaoConsultaMinutos;
        validar();
    }

    private void validar() {
        if (medicoId == null) throw new IllegalArgumentException("ID do médico é obrigatório");
        if (unidadeId == null) throw new IllegalArgumentException("ID da unidade é obrigatório");
        if (diasAtendimento == null || diasAtendimento.isEmpty())
            throw new IllegalArgumentException("Pelo menos um dia da semana deve ser informado");
        if (horarioInicio == null || horarioFim == null)
            throw new IllegalArgumentException("Horário de início e fim são obrigatórios");
        if (horarioInicio.isAfter(horarioFim) || horarioInicio.equals(horarioFim))
            throw new IllegalArgumentException("Horário de início deve ser anterior ao horário de fim");
        if (duracaoConsultaMinutos == null || duracaoConsultaMinutos <= 0)
            throw new IllegalArgumentException("Duração da consulta deve ser maior que zero");
    }

    public boolean atendeNoDia(Weekday dia) {
        return diasAtendimento.contains(dia);
    }

    public boolean horarioDentroDoPeriodo(LocalTime horario) {
        return !horario.isBefore(horarioInicio) && !horario.isAfter(horarioFim);
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
