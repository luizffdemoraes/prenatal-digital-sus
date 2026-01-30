package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Weekday;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AppointmentGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorScheduleGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteDoctorScheduleUseCaseImp")
class DeleteDoctorScheduleUseCaseImpTest {

    @Mock
    private DoctorScheduleGateway doctorScheduleGateway;
    @Mock
    private AppointmentGateway appointmentGateway;
    @Mock
    private DoctorGateway doctorGateway;

    private DeleteDoctorScheduleUseCaseImp useCase;

    private static final Long MEDICO_ID = 1L;
    private static final Long AGENDA_ID = 10L;

    @BeforeEach
    void setUp() {
        useCase = new DeleteDoctorScheduleUseCaseImp(doctorScheduleGateway, appointmentGateway, doctorGateway);
    }

    @Test
    @DisplayName("Deve lançar exceção quando médico não encontrado")
    void deveLancarQuandoMedicoNaoEncontrado() {
        when(doctorGateway.buscarPorCrm("CRM-X")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute("CRM-X"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando agenda não encontrada")
    void shouldThrowWhenScheduleNotFound() {
        when(doctorGateway.buscarPorCrm("CRM-X")).thenReturn(Optional.of(MEDICO_ID));
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute("CRM-X"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando existem consultas agendadas para o médico")
    void deveLancarQuandoExistemConsultasAgendadas() {
        when(doctorGateway.buscarPorCrm("CRM-X")).thenReturn(Optional.of(MEDICO_ID));
        DoctorSchedule agenda = new DoctorSchedule(AGENDA_ID, MEDICO_ID, 2L, Set.of(Weekday.SEGUNDA), LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(agenda));
        when(appointmentGateway.existeAgendamentoPorMedico(MEDICO_ID)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> useCase.execute("CRM-X"));
    }

    @Test
    @DisplayName("Deve excluir agenda com sucesso quando não há consultas agendadas")
    void shouldDeleteSuccessfully() {
        when(doctorGateway.buscarPorCrm("CRM-X")).thenReturn(Optional.of(MEDICO_ID));
        DoctorSchedule agenda = new DoctorSchedule(AGENDA_ID, MEDICO_ID, 2L, Set.of(Weekday.SEGUNDA), LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(agenda));
        when(appointmentGateway.existeAgendamentoPorMedico(MEDICO_ID)).thenReturn(false);

        useCase.execute("CRM-X");

        verify(doctorScheduleGateway).excluirPorId(AGENDA_ID);
    }
}
