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

    private static final String CRM = "12345";
    private static final Long MEDICO_ID = 1L;
    private static final Set<Weekday> DIAS = Set.of(Weekday.SEGUNDA, Weekday.QUARTA);
    private static final LocalTime INICIO = LocalTime.of(8, 0);
    private static final LocalTime FIM = LocalTime.of(12, 0);

    @BeforeEach
    void setUp() {
        useCase = new DeleteDoctorScheduleUseCaseImp(doctorScheduleGateway, appointmentGateway, doctorGateway);
    }

    @Test
    @DisplayName("Deve lançar exceção quando médico não encontrado por CRM")
    void shouldThrowWhenDoctorNotFound() {
        when(doctorGateway.buscarPorCrm(CRM)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> useCase.execute(CRM));
        assertTrue(ex.getMessage().contains("Médico não encontrado"));
        verify(doctorScheduleGateway, never()).excluirPorId(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando agenda não encontrada")
    void shouldThrowWhenScheduleNotFound() {
        when(doctorGateway.buscarPorCrm(CRM)).thenReturn(Optional.of(MEDICO_ID));
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(CRM));
        verify(doctorScheduleGateway, never()).excluirPorId(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando existem consultas agendadas")
    void shouldThrowWhenAppointmentsExist() {
        DoctorSchedule schedule = new DoctorSchedule(100L, MEDICO_ID, 1L, DIAS, INICIO, FIM, 30);
        when(doctorGateway.buscarPorCrm(CRM)).thenReturn(Optional.of(MEDICO_ID));
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(schedule));
        when(appointmentGateway.existeAgendamentoPorMedico(MEDICO_ID)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> useCase.execute(CRM));
        assertTrue(ex.getMessage().contains("consultas agendadas"));
        verify(doctorScheduleGateway, never()).excluirPorId(any());
    }

    @Test
    @DisplayName("Deve excluir agenda com sucesso")
    void shouldDeleteSuccessfully() {
        DoctorSchedule schedule = new DoctorSchedule(100L, MEDICO_ID, 1L, DIAS, INICIO, FIM, 30);
        when(doctorGateway.buscarPorCrm(CRM)).thenReturn(Optional.of(MEDICO_ID));
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(schedule));
        when(appointmentGateway.existeAgendamentoPorMedico(MEDICO_ID)).thenReturn(false);

        useCase.execute(CRM);

        verify(doctorScheduleGateway).excluirPorId(100L);
    }
}
