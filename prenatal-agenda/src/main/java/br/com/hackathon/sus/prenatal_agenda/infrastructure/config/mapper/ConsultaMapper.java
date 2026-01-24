package br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.ConsultaResponse;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity.ConsultaEntity;

public class ConsultaMapper {

    public static Consulta toDomain(ConsultaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Consulta(
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

    public static ConsultaResponse toResponse(Consulta domain) {
        if (domain == null) {
            return null;
        }
        return new ConsultaResponse(
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

    public static ConsultaEntity fromDomain(Consulta domain) {
        if (domain == null) {
            return null;
        }
        return new ConsultaEntity(
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
