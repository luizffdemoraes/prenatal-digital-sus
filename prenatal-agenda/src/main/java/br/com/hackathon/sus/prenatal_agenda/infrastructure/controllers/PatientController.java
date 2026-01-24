package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.AppointmentResponse;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.FindAppointmentsByPatientUseCase;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.PatientGateway;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.config.mapper.AppointmentMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gestantes")
public class PatientController {

    private final FindAppointmentsByPatientUseCase findAppointmentsByPatientUseCase;
    private final PatientGateway patientGateway;

    public PatientController(FindAppointmentsByPatientUseCase findAppointmentsByPatientUseCase,
                             PatientGateway patientGateway) {
        this.findAppointmentsByPatientUseCase = findAppointmentsByPatientUseCase;
        this.patientGateway = patientGateway;
    }

    @GetMapping("/consultas")
    public ResponseEntity<List<AppointmentResponse>> buscarConsultasPorCpf(@RequestParam String cpf) {
        Long gestanteId = patientGateway.buscarPorCpf(cpf).orElse(null);
        if (gestanteId == null) {
            return ResponseEntity.notFound().build();
        }
        List<AppointmentResponse> list = findAppointmentsByPatientUseCase.execute(gestanteId).stream()
                .map(AppointmentMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{gestanteId}/consultas")
    public ResponseEntity<List<AppointmentResponse>> buscarConsultasPorId(@PathVariable Long gestanteId) {
        List<AppointmentResponse> list = findAppointmentsByPatientUseCase.execute(gestanteId).stream()
                .map(AppointmentMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
