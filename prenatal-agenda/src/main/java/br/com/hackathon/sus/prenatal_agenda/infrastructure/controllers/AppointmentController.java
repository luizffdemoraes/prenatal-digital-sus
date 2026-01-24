package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.CreateAppointmentRequest;
import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.AppointmentResponse;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.CancelAppointmentUseCase;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.CreateAppointmentUseCase;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.CancellationReason;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper.AppointmentMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/consultas")
public class AppointmentController {

    private final CreateAppointmentUseCase createAppointmentUseCase;
    private final CancelAppointmentUseCase cancelAppointmentUseCase;

    public AppointmentController(CreateAppointmentUseCase createAppointmentUseCase,
                                 CancelAppointmentUseCase cancelAppointmentUseCase) {
        this.createAppointmentUseCase = createAppointmentUseCase;
        this.cancelAppointmentUseCase = cancelAppointmentUseCase;
    }

    @PostMapping("/agendar")
    public ResponseEntity<AppointmentResponse> agendar(
            @Valid @RequestBody CreateAppointmentRequest request,
            @RequestHeader("X-Unidade-Id") Long unidadeId) {
        var saved = createAppointmentUseCase.execute(request, unidadeId);
        AppointmentResponse response = AppointmentMapper.toResponse(saved);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @DeleteMapping("/{consultaId}/cancelar")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long consultaId,
            @RequestParam CancellationReason motivo) {
        cancelAppointmentUseCase.execute(consultaId, motivo);
        return ResponseEntity.noContent().build();
    }
}
