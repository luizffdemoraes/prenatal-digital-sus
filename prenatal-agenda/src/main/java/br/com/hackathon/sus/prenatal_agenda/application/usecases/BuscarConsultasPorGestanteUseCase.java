package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;

import java.util.List;

public interface BuscarConsultasPorGestanteUseCase {
    List<Consulta> execute(Long gestanteId);
}
