package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.AvailableTimeSlotResponse;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.ListAvailabilityUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/disponibilidade")
public class AvailabilityController {

    private final ListAvailabilityUseCase listAvailabilityUseCase;

    public AvailabilityController(ListAvailabilityUseCase listAvailabilityUseCase) {
        this.listAvailabilityUseCase = listAvailabilityUseCase;
    }

    @GetMapping
    public ResponseEntity<AvailableTimeSlotResponse> consultar(
            @RequestParam Long medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        List<java.time.LocalTime> horarios = listAvailabilityUseCase.execute(medicoId, data);

        AvailableTimeSlotResponse response = new AvailableTimeSlotResponse(
                medicoId,
                data.format(DateTimeFormatter.ISO_DATE),
                horarios
        );

        return ResponseEntity.ok(response);
    }
}
