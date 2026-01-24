package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.CriarAgendaMedicoRequest;
import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.AgendaMedicoResponse;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.BuscarAgendaMedicoUseCase;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.CriarAgendaMedicoUseCase;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.MedicoResolver;
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
    private final MedicoResolver medicoResolver;

    public AgendaMedicoController(CriarAgendaMedicoUseCase criarAgendaMedicoUseCase,
                                  BuscarAgendaMedicoUseCase buscarAgendaMedicoUseCase,
                                  MedicoResolver medicoResolver) {
        this.criarAgendaMedicoUseCase = criarAgendaMedicoUseCase;
        this.buscarAgendaMedicoUseCase = buscarAgendaMedicoUseCase;
        this.medicoResolver = medicoResolver;
    }

    @PostMapping("/medico")
    public ResponseEntity<AgendaMedicoResponse> criarAgenda(@Valid @RequestBody CriarAgendaMedicoRequest request) {
        var agendaSalva = criarAgendaMedicoUseCase.execute(request);
        AgendaMedicoResponse response = AgendaMedicoMapper.toResponse(agendaSalva);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{crm}")
                .buildAndExpand(request.crm())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/medico/{crm}")
    public ResponseEntity<AgendaMedicoResponse> buscarPorMedico(@PathVariable String crm) {
        return medicoResolver.buscarPorCrm(crm)
                .flatMap(medicoId -> buscarAgendaMedicoUseCase.execute(medicoId))
                .map(agenda -> ResponseEntity.ok(AgendaMedicoMapper.toResponse(agenda)))
                .orElse(ResponseEntity.notFound().build());
    }
}
