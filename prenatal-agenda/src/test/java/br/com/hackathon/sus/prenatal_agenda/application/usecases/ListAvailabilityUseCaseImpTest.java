package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.AppointmentStatus;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Weekday;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AppointmentGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorScheduleGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListAvailabilityUseCaseImp")
class ListAvailabilityUseCaseImpTest {

    @Mock
    private DoctorScheduleGateway doctorScheduleGateway;
    @Mock
    private AppointmentGateway appointmentGateway;

    private ListAvailabilityUseCaseImp useCase;

    private static final Long MEDICO_ID = 1L;

    @BeforeEach
    void setUp() {
        useCase = new ListAvailabilityUseCaseImp(doctorScheduleGateway, appointmentGateway);
    }

    @Test
    @DisplayName("Deve lançar exceção quando agenda não encontrada")
    void shouldThrowWhenScheduleNotFound() {
        LocalDate data = LocalDate.now().plusDays(1);
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(MEDICO_ID, data));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando médico não atende no dia")
    void shouldReturnEmptyWhenDoctorNotAvailableOnDay() {
        LocalDate domingo = LocalDate.now();
        while (domingo.getDayOfWeek().getValue() != 7) { domingo = domingo.plusDays(1); }
        DoctorSchedule agenda = new DoctorSchedule(MEDICO_ID, 2L, Set.of(Weekday.SEGUNDA, Weekday.TERCA),
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(agenda));

        List<LocalTime> result = useCase.execute(MEDICO_ID, domingo);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar slots disponíveis excluindo horários ocupados")
    void deveRetornarSlotsExcluindoOcupados() {
        LocalDate data = LocalDate.now().plusDays(1);
        while (data.getDayOfWeek().getValue() > 5) { data = data.plusDays(1); }

        DoctorSchedule agenda = new DoctorSchedule(MEDICO_ID, 2L, Set.of(Weekday.SEGUNDA, Weekday.TERCA, Weekday.QUARTA, Weekday.QUINTA, Weekday.SEXTA),
                LocalTime.of(8, 0), LocalTime.of(10, 0), 30);
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(agenda));

        Appointment ocupada = new Appointment(1L, 10L, MEDICO_ID, 3L, data, LocalTime.of(8, 30), AppointmentStatus.AGENDADA, null, LocalDateTime.now(), null);
        when(appointmentGateway.buscarConsultasAgendadasPorMedicoEData(MEDICO_ID, data)).thenReturn(List.of(ocupada));

        List<LocalTime> result = useCase.execute(MEDICO_ID, data);

        assertNotNull(result);
        assertTrue(result.contains(LocalTime.of(8, 0)));
        assertFalse(result.contains(LocalTime.of(8, 30)));
        assertTrue(result.contains(LocalTime.of(9, 0)));
        assertTrue(result.contains(LocalTime.of(9, 30)));
    }

    @Test
    @DisplayName("Deve retornar todos os slots quando não há consultas agendadas")
    void shouldReturnAllSlotsWhenNoAppointments() {
        LocalDate data = LocalDate.now().plusDays(1);
        while (data.getDayOfWeek().getValue() > 5) { data = data.plusDays(1); }

        DoctorSchedule agenda = new DoctorSchedule(MEDICO_ID, 2L, Set.of(Weekday.SEGUNDA, Weekday.TERCA, Weekday.QUARTA, Weekday.QUINTA, Weekday.SEXTA),
                LocalTime.of(8, 0), LocalTime.of(9, 0), 30);
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(agenda));
        when(appointmentGateway.buscarConsultasAgendadasPorMedicoEData(MEDICO_ID, data)).thenReturn(List.of());

        List<LocalTime> result = useCase.execute(MEDICO_ID, data);

        assertEquals(List.of(LocalTime.of(8, 0), LocalTime.of(8, 30)), result);
    }
}
