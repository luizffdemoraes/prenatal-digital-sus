package br.com.hackathon.sus.prenatal_agenda.domain.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;

import java.util.Optional;

public interface DoctorScheduleGateway {

    DoctorSchedule salvar(DoctorSchedule schedule);

    Optional<DoctorSchedule> buscarPorId(Long id);

    Optional<DoctorSchedule> buscarPorMedicoId(Long medicoId);

    void excluirPorId(Long id);
}
