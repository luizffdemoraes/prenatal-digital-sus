package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.CreateDoctorScheduleRequest;
import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.UpdateDoctorScheduleRequest;
import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.DoctorScheduleResponse;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.CreateDoctorScheduleUseCase;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.DeleteDoctorScheduleUseCase;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.FindDoctorScheduleUseCase;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.UpdateDoctorScheduleUseCase;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorGateway;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper.DoctorScheduleMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/agendas")
public class DoctorScheduleController {

    private final CreateDoctorScheduleUseCase createDoctorScheduleUseCase;
    private final UpdateDoctorScheduleUseCase updateDoctorScheduleUseCase;
    private final DeleteDoctorScheduleUseCase deleteDoctorScheduleUseCase;
    private final FindDoctorScheduleUseCase findDoctorScheduleUseCase;
    private final DoctorGateway doctorGateway;

    public DoctorScheduleController(CreateDoctorScheduleUseCase createDoctorScheduleUseCase,
                                    UpdateDoctorScheduleUseCase updateDoctorScheduleUseCase,
                                    DeleteDoctorScheduleUseCase deleteDoctorScheduleUseCase,
                                    FindDoctorScheduleUseCase findDoctorScheduleUseCase,
                                    DoctorGateway doctorGateway) {
        this.createDoctorScheduleUseCase = createDoctorScheduleUseCase;
        this.updateDoctorScheduleUseCase = updateDoctorScheduleUseCase;
        this.deleteDoctorScheduleUseCase = deleteDoctorScheduleUseCase;
        this.findDoctorScheduleUseCase = findDoctorScheduleUseCase;
        this.doctorGateway = doctorGateway;
    }

    @PostMapping("/medico")
    public ResponseEntity<DoctorScheduleResponse> criar(@Valid @RequestBody CreateDoctorScheduleRequest request) {
        var saved = createDoctorScheduleUseCase.execute(request);
        DoctorScheduleResponse response = DoctorScheduleMapper.toResponse(saved);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{crm}")
                .buildAndExpand(request.crm())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/medico/{crm}")
    public ResponseEntity<DoctorScheduleResponse> buscarPorMedico(@PathVariable String crm) {
        return doctorGateway.buscarPorCrm(crm)
                .flatMap(medicoId -> findDoctorScheduleUseCase.execute(medicoId))
                .map(schedule -> ResponseEntity.ok(DoctorScheduleMapper.toResponse(schedule)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/medico/{crm}")
    public ResponseEntity<DoctorScheduleResponse> atualizar(
            @PathVariable String crm,
            @Valid @RequestBody UpdateDoctorScheduleRequest request) {
        var updated = updateDoctorScheduleUseCase.execute(crm, request);
        return ResponseEntity.ok(DoctorScheduleMapper.toResponse(updated));
    }

    @DeleteMapping("/medico/{crm}")
    public ResponseEntity<Void> excluir(@PathVariable String crm) {
        deleteDoctorScheduleUseCase.execute(crm);
        return ResponseEntity.noContent().build();
    }
}
