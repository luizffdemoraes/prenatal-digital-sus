package br.com.hackathon.sus.prenatal_documento.application.usecases;

import br.com.hackathon.sus.prenatal_documento.domain.models.Vaccine;
import br.com.hackathon.sus.prenatal_documento.domain.repositories.VaccineRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RegisterVaccineUseCaseImpl implements RegisterVaccineUseCase {

    private final VaccineRepository vaccineRepository;

    public RegisterVaccineUseCaseImpl(VaccineRepository vaccineRepository) {
        this.vaccineRepository = vaccineRepository;
    }

    @Override
    public Vaccine execute(String patientCpf, String vaccineType, LocalDate applicationDate) {
        if (patientCpf == null || patientCpf.isBlank()) {
            throw new IllegalArgumentException("CPF da paciente é obrigatório");
        }
        if (vaccineType == null || vaccineType.isBlank()) {
            throw new IllegalArgumentException("Tipo da vacina é obrigatório");
        }
        if (applicationDate == null) {
            throw new IllegalArgumentException("Data de aplicação é obrigatória");
        }
        String cpfDigits = patientCpf.replaceAll("\\D", "");
        if (cpfDigits.length() != 11) {
            throw new IllegalArgumentException("CPF inválido");
        }
        Vaccine vaccine = new Vaccine(cpfDigits, vaccineType.trim().toUpperCase(), applicationDate);
        return vaccineRepository.save(vaccine);
    }
}
