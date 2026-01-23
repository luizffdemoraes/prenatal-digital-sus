package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.responses.HorarioDisponivelResponse;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.ConsultarDisponibilidadeUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/disponibilidade")
public class DisponibilidadeController {

    private final ConsultarDisponibilidadeUseCase consultarDisponibilidadeUseCase;

    public DisponibilidadeController(ConsultarDisponibilidadeUseCase consultarDisponibilidadeUseCase) {
        this.consultarDisponibilidadeUseCase = consultarDisponibilidadeUseCase;
    }

    @GetMapping
    public ResponseEntity<HorarioDisponivelResponse> consultar(
            @RequestParam Long medicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        
        List<java.time.LocalTime> horarios = consultarDisponibilidadeUseCase.execute(medicoId, data);
        
        HorarioDisponivelResponse response = new HorarioDisponivelResponse(
                medicoId,
                data.format(DateTimeFormatter.ISO_DATE),
                horarios
        );
        
        return ResponseEntity.ok(response);
    }
}
