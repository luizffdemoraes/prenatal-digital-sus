package br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.AgendaMedicoResponse;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.AgendaMedico;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity.AgendaMedicoEntity;

public class AgendaMedicoMapper {

    public static AgendaMedico toDomain(AgendaMedicoEntity entity) {
        if (entity == null) {
            return null;
        }
        return new AgendaMedico(
                entity.getId(),
                entity.getMedicoId(),
                entity.getUnidadeId(),
                entity.getDiasAtendimento(),
                entity.getHorarioInicio(),
                entity.getHorarioFim(),
                entity.getDuracaoConsultaMinutos()
        );
    }

    public static AgendaMedicoResponse toResponse(AgendaMedico domain) {
        if (domain == null) {
            return null;
        }
        return new AgendaMedicoResponse(
                domain.getId(),
                domain.getMedicoId(),
                domain.getUnidadeId(),
                domain.getDiasAtendimento(),
                domain.getHorarioInicio(),
                domain.getHorarioFim(),
                domain.getDuracaoConsultaMinutos()
        );
    }

    public static AgendaMedicoEntity fromDomain(AgendaMedico domain) {
        if (domain == null) {
            return null;
        }
        return new AgendaMedicoEntity(
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
