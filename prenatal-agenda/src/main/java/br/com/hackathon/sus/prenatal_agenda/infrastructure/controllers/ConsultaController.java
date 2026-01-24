package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.AgendarConsultaRequest;
import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.ConsultaResponse;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.AgendarConsultaPorIdentificacaoUseCase;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.CancelarConsultaUseCase;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.MotivoCancelamento;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper.ConsultaMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final AgendarConsultaPorIdentificacaoUseCase agendarConsultaUseCase;
    private final CancelarConsultaUseCase cancelarConsultaUseCase;

    public ConsultaController(AgendarConsultaPorIdentificacaoUseCase agendarConsultaUseCase,
                              CancelarConsultaUseCase cancelarConsultaUseCase) {
        this.agendarConsultaUseCase = agendarConsultaUseCase;
        this.cancelarConsultaUseCase = cancelarConsultaUseCase;
    }

    /**
     * Agenda uma consulta. Aceita IDs ou o que a gestante conhece:
     * Gestante: gestanteId OU gestanteCpf OU gestanteEmail
     * Médico: medicoId OU medicoNome OU especialidade
     * Unidade: unidadeId OU unidadeNome
     */
    @PostMapping("/agendar")
    public ResponseEntity<ConsultaResponse> agendar(@Valid @RequestBody AgendarConsultaRequest request) {
        Consulta consultaSalva = agendarConsultaUseCase.execute(request);
        ConsultaResponse response = ConsultaMapper.toResponse(consultaSalva);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @DeleteMapping("/{consultaId}/cancelar")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long consultaId,
            @RequestParam MotivoCancelamento motivo) {
        cancelarConsultaUseCase.execute(consultaId, motivo);
        return ResponseEntity.noContent().build();
    }
}
