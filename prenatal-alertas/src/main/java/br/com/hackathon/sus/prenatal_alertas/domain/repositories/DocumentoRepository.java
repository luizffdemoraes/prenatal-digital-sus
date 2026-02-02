package br.com.hackathon.sus.prenatal_alertas.domain.repositories;

import br.com.hackathon.sus.prenatal_alertas.domain.entities.ExamRecord;
import br.com.hackathon.sus.prenatal_alertas.domain.entities.VaccineRecord;

import java.util.List;

public interface DocumentoRepository {
    List<ExamRecord> findExamsByCpf(String cpf);
    List<VaccineRecord> findVaccinesByCpf(String cpf);
}
