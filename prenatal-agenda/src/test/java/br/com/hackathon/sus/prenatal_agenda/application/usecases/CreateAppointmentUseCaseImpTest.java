package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.CreateAppointmentRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Weekday;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AppointmentGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorScheduleGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.PatientGateway;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateAppointmentUseCaseImp")
class CreateAppointmentUseCaseImpTest {

    @Mock
    private PatientGateway patientGateway;
    @Mock
    private DoctorGateway doctorGateway;
    @Mock
    private AppointmentGateway appointmentGateway;
    @Mock
    private DoctorScheduleGateway doctorScheduleGateway;

    private CreateAppointmentUseCaseImp useCase;

    private static final Long UNIDADE_ID = 1L;
    private static final Long GESTANTE_ID = 10L;
    private static final Long MEDICO_ID = 20L;
    private static final LocalDate DATA = LocalDate.now().plusDays(1);
    private static final LocalTime HORARIO = LocalTime.of(9, 0);
    private static final CreateAppointmentRequest REQ = new CreateAppointmentRequest(
            "Maria", "12345678900", "Dr. João", "Obstetrícia", "CRM-SP 123",
            DATA, HORARIO);

    @BeforeEach
    void setUp() {
        useCase = new CreateAppointmentUseCaseImp(patientGateway, doctorGateway, appointmentGateway, doctorScheduleGateway);
    }

    @Nested
    @DisplayName("Validações iniciais")
    class Validacoes {

        @Test
        @DisplayName("Deve lançar exceção quando unidadeId é nulo")
        void shouldThrowWhenUnitIdIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    useCase.execute(REQ, null));
        }

        @Test
        @DisplayName("Deve lançar exceção quando CPF da gestante é vazio")
        void shouldThrowWhenCpfIsEmpty() {
            CreateAppointmentRequest req = new CreateAppointmentRequest("Maria", "  ", "Dr. João", null, "CRM-X",
                    DATA, HORARIO);

            assertThrows(IllegalArgumentException.class, () ->
                    useCase.execute(req, UNIDADE_ID));
        }

        @Test
        @DisplayName("Deve lançar exceção quando gestante não encontrada")
        void shouldThrowWhenPatientNotFound() {
            when(patientGateway.buscarPorCpf("12345678900")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () ->
                    useCase.execute(REQ, UNIDADE_ID));
        }

        @Test
        @DisplayName("Deve lançar exceção quando nenhum identificador do médico informado")
        void shouldThrowWhenNoDoctorIdentifierProvided() {
            CreateAppointmentRequest req = new CreateAppointmentRequest("Maria", "12345678900", null, null, null, DATA, HORARIO);
            when(patientGateway.buscarPorCpf("12345678900")).thenReturn(Optional.of(GESTANTE_ID));

            assertThrows(IllegalArgumentException.class, () ->
                    useCase.execute(req, UNIDADE_ID));
        }
    }

    @Nested
    @DisplayName("Resolução por CRM")
    class PorCrm {

        @Test
        @DisplayName("Deve agendar com sucesso quando médico identificado por CRM")
        void deveAgendarPorCrm() {
            when(patientGateway.buscarPorCpf("12345678900")).thenReturn(Optional.of(GESTANTE_ID));
            when(doctorGateway.buscarPorCrm("CRM-SP 123")).thenReturn(Optional.of(MEDICO_ID));

            LocalDate data = LocalDate.now().plusDays(1);
            while (data.getDayOfWeek().getValue() > 5) { data = data.plusDays(1); }
            DoctorSchedule agenda = new DoctorSchedule(MEDICO_ID, UNIDADE_ID, Set.of(Weekday.SEGUNDA, Weekday.TERCA, Weekday.QUARTA, Weekday.QUINTA, Weekday.SEXTA),
                    LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
            when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(agenda));
            when(appointmentGateway.buscarConsultasAgendadas(MEDICO_ID, data, HORARIO)).thenReturn(List.of());

            CreateAppointmentRequest req = new CreateAppointmentRequest("Maria", "12345678900", "Dr. João", "Obstetrícia", "CRM-SP 123", data, HORARIO);
            Appointment consultaSalva = new Appointment(GESTANTE_ID, "12345678900", MEDICO_ID, UNIDADE_ID, data, HORARIO);
            consultaSalva.setId(100L);
            when(appointmentGateway.salvar(any(Appointment.class))).thenReturn(consultaSalva);

            Appointment result = useCase.execute(req, UNIDADE_ID);

            assertNotNull(result);
            assertEquals(100L, result.getId());
            verify(appointmentGateway).salvar(any(Appointment.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando médico não encontrado por CRM")
        void shouldThrowWhenDoctorNotFoundByCrm() {
            when(patientGateway.buscarPorCpf("12345678900")).thenReturn(Optional.of(GESTANTE_ID));
            when(doctorGateway.buscarPorCrm("CRM-INVALIDO")).thenReturn(Optional.empty());

            CreateAppointmentRequest req = new CreateAppointmentRequest("Maria", "12345678900", null, null, "CRM-INVALIDO", DATA, HORARIO);

            assertThrows(IllegalArgumentException.class, () ->
                    useCase.execute(req, UNIDADE_ID));
        }
    }

    @Nested
    @DisplayName("Regras de agenda")
    class RegrasAgenda {

        @Test
        @DisplayName("Deve lançar exceção quando agenda não encontrada para o médico")
        void deveLancarQuandoAgendaNaoEncontrada() {
            when(patientGateway.buscarPorCpf("12345678900")).thenReturn(Optional.of(GESTANTE_ID));
            when(doctorGateway.buscarPorCrm("CRM-SP 123")).thenReturn(Optional.of(MEDICO_ID));
            when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () ->
                    useCase.execute(REQ, UNIDADE_ID));
        }

        @Test
        @DisplayName("Deve lançar exceção quando médico não atende no dia da semana")
        void shouldThrowWhenDoctorNotAvailableOnDay() {
            when(patientGateway.buscarPorCpf("12345678900")).thenReturn(Optional.of(GESTANTE_ID));
            when(doctorGateway.buscarPorCrm("CRM-SP 123")).thenReturn(Optional.of(MEDICO_ID));
            DoctorSchedule agenda = new DoctorSchedule(MEDICO_ID, UNIDADE_ID, Set.of(Weekday.TERCA, Weekday.QUARTA),
                    LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
            when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(agenda));

            LocalDate segunda = LocalDate.now();
            while (segunda.getDayOfWeek() != java.time.DayOfWeek.MONDAY) {
                segunda = segunda.plusDays(1);
            }
            CreateAppointmentRequest reqSeg = new CreateAppointmentRequest("Maria", "12345678900", null, null, "CRM-SP 123", segunda, HORARIO);

            assertThrows(IllegalArgumentException.class, () ->
                    useCase.execute(reqSeg, UNIDADE_ID));
        }

        @Test
        @DisplayName("Deve lançar exceção quando horário fora do período")
        void deveLancarQuandoHorarioForaDoPeriodo() {
            when(patientGateway.buscarPorCpf("12345678900")).thenReturn(Optional.of(GESTANTE_ID));
            when(doctorGateway.buscarPorCrm("CRM-SP 123")).thenReturn(Optional.of(MEDICO_ID));
            DoctorSchedule agenda = new DoctorSchedule(MEDICO_ID, UNIDADE_ID, Set.of(Weekday.SEGUNDA, Weekday.TERCA, Weekday.QUARTA, Weekday.QUINTA, Weekday.SEXTA),
                    LocalTime.of(8, 0), LocalTime.of(10, 0), 30);
            when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(agenda));

            LocalDate segunda = LocalDate.now().plusDays(1);
            while (segunda.getDayOfWeek().getValue() > 5) { segunda = segunda.plusDays(1); }
            LocalTime horarioFora = LocalTime.of(11, 0);
            CreateAppointmentRequest req = new CreateAppointmentRequest("Maria", "12345678900", null, null, "CRM-SP 123", segunda, horarioFora);

            assertThrows(IllegalArgumentException.class, () ->
                    useCase.execute(req, UNIDADE_ID));
        }

        @Test
        @DisplayName("Deve lançar exceção quando horário já ocupado")
        void shouldThrowWhenTimeSlotOccupied() {
            LocalDate data = LocalDate.now().plusDays(1);
            while (data.getDayOfWeek().getValue() > 5) { data = data.plusDays(1); }
            CreateAppointmentRequest req = new CreateAppointmentRequest("Maria", "12345678900", "Dr. João", "Obstetrícia", "CRM-SP 123", data, HORARIO);

            when(patientGateway.buscarPorCpf("12345678900")).thenReturn(Optional.of(GESTANTE_ID));
            when(doctorGateway.buscarPorCrm("CRM-SP 123")).thenReturn(Optional.of(MEDICO_ID));
            DoctorSchedule agenda = new DoctorSchedule(MEDICO_ID, UNIDADE_ID, Set.of(Weekday.SEGUNDA, Weekday.TERCA, Weekday.QUARTA, Weekday.QUINTA, Weekday.SEXTA),
                    LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
            when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(agenda));

            Appointment existente = new Appointment(99L, GESTANTE_ID, null, MEDICO_ID, UNIDADE_ID, data, HORARIO, br.com.hackathon.sus.prenatal_agenda.domain.entities.AppointmentStatus.AGENDADA, null, java.time.LocalDateTime.now(), null);
            when(appointmentGateway.buscarConsultasAgendadas(MEDICO_ID, data, HORARIO)).thenReturn(List.of(existente));

            assertThrows(IllegalArgumentException.class, () ->
                    useCase.execute(req, UNIDADE_ID));
        }
    }
}
