package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.AvailableTimeSlotResponse;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.ListAvailabilityUseCase;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorGateway;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Consulta disponibilidade por CRM (não por medicoId).
 * Response: medicoNome, especialidade, data, horariosDisponiveis.
 */
@RestController
@RequestMapping("/api/disponibilidade")
public class AvailabilityController {

    private final ListAvailabilityUseCase listAvailabilityUseCase;
    private final DoctorGateway doctorGateway;

    public AvailabilityController(ListAvailabilityUseCase listAvailabilityUseCase,
                                  DoctorGateway doctorGateway) {
        this.listAvailabilityUseCase = listAvailabilityUseCase;
        this.doctorGateway = doctorGateway;
    }

    @GetMapping
    public ResponseEntity<AvailableTimeSlotResponse> consultar(
            @RequestParam String crm,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        Long medicoId = doctorGateway.buscarPorCrm(crm)
                .orElse(null);
        if (medicoId == null) {
            return ResponseEntity.notFound().build();
        }

        List<java.time.LocalTime> horarios = listAvailabilityUseCase.execute(medicoId, data);
        var doctorInfo = doctorGateway.findById(medicoId).orElse(null);
        String doctorName = doctorInfo != null ? doctorInfo.name() : "";
        String specialty = doctorInfo != null ? doctorInfo.specialty() : "";

        AvailableTimeSlotResponse response = new AvailableTimeSlotResponse(
                doctorName,
                specialty,
                data.format(DateTimeFormatter.ISO_DATE),
                horarios
        );

        return ResponseEntity.ok(response);
    }
}
