package br.com.hackathon.sus.prenatal_documento.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_documento.application.dtos.responses.VaccineResponse;
import br.com.hackathon.sus.prenatal_documento.application.usecases.RegisterVaccineUseCase;
import br.com.hackathon.sus.prenatal_documento.domain.models.Vaccine;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class VaccineController {

    private final RegisterVaccineUseCase registerVaccineUseCase;

    public VaccineController(RegisterVaccineUseCase registerVaccineUseCase) {
        this.registerVaccineUseCase = registerVaccineUseCase;
    }

    @PostMapping("/prenatal-records/{cpf}/vacinas")
    @PreAuthorize("hasAnyRole('PATIENT', 'NURSE', 'DOCTOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public VaccineResponse register(
            @PathVariable String cpf,
            @RequestParam("tipoVacina") @NotBlank String vaccineType,
            @RequestParam("dataAplicacao") @NotNull LocalDate applicationDate) {
        Vaccine vaccine = registerVaccineUseCase.execute(cpf, vaccineType, applicationDate);
        return VaccineResponse.from(vaccine);
    }
}
