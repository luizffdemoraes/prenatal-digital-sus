package br.com.hackathon.sus.prenatal_agenda.domain.gateways;

import java.util.Optional;

public interface DoctorGateway {

    Optional<Long> buscarPorCrm(String crm);

    Optional<Long> buscarPorNome(String nome, Long unidadeId);

    Optional<Long> buscarPorEspecialidade(String especialidade, Long unidadeId);

    Optional<DoctorInfo> findById(Long id);
}
