package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.CriarAgendaMedicoRequest;
import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.AgendaMedicoResponse;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.BuscarAgendaMedicoUseCase;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.CriarAgendaMedicoUseCase;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.AgendaMedico;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper.AgendaMedicoMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/agendas")
public class AgendaMedicoController {

    private final CriarAgendaMedicoUseCase criarAgendaMedicoUseCase;
    private final BuscarAgendaMedicoUseCase buscarAgendaMedicoUseCase;

    public AgendaMedicoController(CriarAgendaMedicoUseCase criarAgendaMedicoUseCase,
                                 BuscarAgendaMedicoUseCase buscarAgendaMedicoUseCase) {
        this.criarAgendaMedicoUseCase = criarAgendaMedicoUseCase;
        this.buscarAgendaMedicoUseCase = buscarAgendaMedicoUseCase;
    }

    @PostMapping("/medico")
    public ResponseEntity<AgendaMedicoResponse> criarAgenda(@Valid @RequestBody CriarAgendaMedicoRequest request) {
        AgendaMedico agenda = AgendaMedicoMapper.toDomain(request);
        AgendaMedico agendaSalva = criarAgendaMedicoUseCase.execute(agenda);
        AgendaMedicoResponse response = AgendaMedicoMapper.toResponse(agendaSalva);
        
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/medico/{medicoId}")
    public ResponseEntity<AgendaMedicoResponse> buscarPorMedico(@PathVariable Long medicoId) {
        return buscarAgendaMedicoUseCase.execute(medicoId)
                .map(agenda -> ResponseEntity.ok(AgendaMedicoMapper.toResponse(agenda)))
                .orElse(ResponseEntity.notFound().build());
    }
}
