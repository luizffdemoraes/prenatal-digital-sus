package br.com.hackathon.sus.prenatal_documento.infrastructure.persistence.repositories;

import br.com.hackathon.sus.prenatal_documento.domain.models.Vaccine;
import br.com.hackathon.sus.prenatal_documento.domain.repositories.VaccineRepository;
import br.com.hackathon.sus.prenatal_documento.infrastructure.persistence.entities.VaccineEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class VaccineRepositoryImpl implements VaccineRepository {

    private final JpaVaccineRepository jpaRepository;

    public VaccineRepositoryImpl(JpaVaccineRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Vaccine save(Vaccine vaccine) {
        VaccineEntity entity = toEntity(vaccine);
        VaccineEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Vaccine> findByPatientCpf(String patientCpf) {
        return jpaRepository.findByPatientCpfOrderByApplicationDateDesc(patientCpf)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private VaccineEntity toEntity(Vaccine domain) {
        VaccineEntity entity = new VaccineEntity();
        entity.setId(domain.getId());
        entity.setPatientCpf(domain.getPatientCpf());
        entity.setVaccineType(domain.getVaccineType());
        entity.setApplicationDate(domain.getApplicationDate());
        return entity;
    }

    private Vaccine toDomain(VaccineEntity entity) {
        Vaccine domain = new Vaccine();
        domain.setId(entity.getId());
        domain.setPatientCpf(entity.getPatientCpf());
        domain.setVaccineType(entity.getVaccineType());
        domain.setApplicationDate(entity.getApplicationDate());
        return domain;
    }
}
