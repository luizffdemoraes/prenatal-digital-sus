package br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.AppointmentResponse;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity.AppointmentEntity;

public class AppointmentMapper {

    public static Appointment toDomain(AppointmentEntity entity) {
        if (entity == null) return null;
        return new Appointment(
                entity.getId(),
                entity.getGestanteId(),
                entity.getMedicoId(),
                entity.getUnidadeId(),
                entity.getData(),
                entity.getHorario(),
                entity.getStatus(),
                entity.getMotivoCancelamento(),
                entity.getDataAgendamento(),
                entity.getDataCancelamento()
        );
    }

    /**
     * Monta o response com nome da paciente, do médico e especialidade (resolvidos pelos gateways).
     */
    public static AppointmentResponse toResponse(Appointment domain,
                                                String patientName,
                                                String doctorName,
                                                String specialty) {
        if (domain == null) return null;
        return new AppointmentResponse(
                domain.getId(),
                patientName != null ? patientName : "",
                doctorName != null ? doctorName : "",
                specialty != null ? specialty : "",
                domain.getUnidadeId(),
                domain.getData(),
                domain.getHorario(),
                domain.getStatus(),
                domain.getMotivoCancelamento(),
                domain.getDataAgendamento(),
                domain.getDataCancelamento()
        );
    }

    public static AppointmentEntity fromDomain(Appointment domain) {
        if (domain == null) return null;
        return new AppointmentEntity(
                domain.getId(),
                domain.getGestanteId(),
                domain.getMedicoId(),
                domain.getUnidadeId(),
                domain.getData(),
                domain.getHorario(),
                domain.getStatus(),
                domain.getMotivoCancelamento(),
                domain.getDataAgendamento(),
                domain.getDataCancelamento()
        );
    }
}
