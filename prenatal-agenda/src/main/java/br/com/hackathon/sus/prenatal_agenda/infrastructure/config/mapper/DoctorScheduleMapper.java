package br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.DoctorScheduleResponse;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity.DoctorScheduleEntity;

public class DoctorScheduleMapper {

    public static DoctorSchedule toDomain(DoctorScheduleEntity entity) {
        if (entity == null) return null;
        return new DoctorSchedule(
                entity.getId(),
                entity.getMedicoId(),
                entity.getUnidadeId(),
                entity.getDiasAtendimento(),
                entity.getHorarioInicio(),
                entity.getHorarioFim(),
                entity.getDuracaoConsultaMinutos()
        );
    }

    public static DoctorScheduleResponse toResponse(DoctorSchedule domain) {
        if (domain == null) return null;
        return new DoctorScheduleResponse(
                domain.getId(),
                domain.getMedicoId(),
                domain.getUnidadeId(),
                domain.getDiasAtendimento(),
                domain.getHorarioInicio(),
                domain.getHorarioFim(),
                domain.getDuracaoConsultaMinutos()
        );
    }

    public static DoctorScheduleEntity fromDomain(DoctorSchedule domain) {
        if (domain == null) return null;
        return new DoctorScheduleEntity(
                domain.getId(),
                domain.getMedicoId(),
                domain.getUnidadeId(),
                domain.getDiasAtendimento(),
                domain.getHorarioInicio(),
                domain.getHorarioFim(),
                domain.getDuracaoConsultaMinutos()
        );
    }
}
