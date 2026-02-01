package br.com.hackathon.sus.prenatal_documento.infrastructure.persistence.repositories;

import br.com.hackathon.sus.prenatal_documento.infrastructure.persistence.entities.VaccineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaVaccineRepository extends JpaRepository<VaccineEntity, UUID> {
    List<VaccineEntity> findByPatientCpfOrderByApplicationDateDesc(String patientCpf);
}
