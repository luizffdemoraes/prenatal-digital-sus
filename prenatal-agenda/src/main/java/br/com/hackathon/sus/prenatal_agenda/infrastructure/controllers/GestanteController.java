package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.ConsultaResponse;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.BuscarConsultasPorGestanteUseCase;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.GestanteResolver;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper.ConsultaMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gestantes")
public class GestanteController {

    private final BuscarConsultasPorGestanteUseCase buscarConsultasPorGestanteUseCase;
    private final GestanteResolver gestanteResolver;

    public GestanteController(BuscarConsultasPorGestanteUseCase buscarConsultasPorGestanteUseCase,
                              GestanteResolver gestanteResolver) {
        this.buscarConsultasPorGestanteUseCase = buscarConsultasPorGestanteUseCase;
        this.gestanteResolver = gestanteResolver;
    }

    /**
     * Consultar própria agenda (consultas) da gestante por CPF.
     * Gestantes, Enfermeiras e Médicos.
     */
    @GetMapping("/consultas")
    public ResponseEntity<List<ConsultaResponse>> buscarConsultasPorCpf(@RequestParam String cpf) {
        Long gestanteId = gestanteResolver.buscarPorCpf(cpf)
                .orElse(null);
        if (gestanteId == null) {
            return ResponseEntity.notFound().build();
        }
        List<ConsultaResponse> consultas = buscarConsultasPorGestanteUseCase.execute(gestanteId).stream()
                .map(ConsultaMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(consultas);
    }

    @GetMapping("/{gestanteId}/consultas")
    public ResponseEntity<List<ConsultaResponse>> buscarConsultasPorId(@PathVariable Long gestanteId) {
        List<ConsultaResponse> consultas = buscarConsultasPorGestanteUseCase.execute(gestanteId).stream()
                .map(ConsultaMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(consultas);
    }
}
