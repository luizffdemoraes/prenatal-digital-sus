package br.com.hackathon.sus.prenatal_ia.domain.gateways;

import br.com.hackathon.sus.prenatal_ia.domain.entities.ExamRecord;
import br.com.hackathon.sus.prenatal_ia.domain.entities.VaccineRecord;

import java.util.List;

public interface DocumentoGateway {
    List<ExamRecord> findExamsByCpf(String cpf);
    List<VaccineRecord> findVaccinesByCpf(String cpf);
}
