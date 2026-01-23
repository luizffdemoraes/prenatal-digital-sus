package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.ConsultaResponse;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.BuscarConsultasPorGestanteUseCase;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper.ConsultaMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gestantes")
public class GestanteController {

    private final BuscarConsultasPorGestanteUseCase buscarConsultasPorGestanteUseCase;

    public GestanteController(BuscarConsultasPorGestanteUseCase buscarConsultasPorGestanteUseCase) {
        this.buscarConsultasPorGestanteUseCase = buscarConsultasPorGestanteUseCase;
    }

    @GetMapping("/{gestanteId}/consultas")
    public ResponseEntity<List<ConsultaResponse>> buscarConsultas(@PathVariable Long gestanteId) {
        List<ConsultaResponse> consultas = buscarConsultasPorGestanteUseCase.execute(gestanteId).stream()
                .map(ConsultaMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(consultas);
    }
}
