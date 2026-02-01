package br.com.hackathon.sus.prenatal_agenda.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Appointment - Entidade de domínio")
class AppointmentTest {

    private static final Long GESTANTE_ID = 1L;
    private static final Long MEDICO_ID = 2L;
    private static final Long UNIDADE_ID = 3L;
    private static final LocalDate DATA_FUTURA = LocalDate.now().plusDays(1);
    private static final LocalTime HORARIO = LocalTime.of(9, 0);

    @Nested
    @DisplayName("Criação e validação")
    class Criacao {

        @Test
        @DisplayName("Deve criar consulta com dados válidos")
        void deveCriarConsultaComDadosValidos() {
            Appointment consulta = new Appointment(GESTANTE_ID, null, MEDICO_ID, UNIDADE_ID, DATA_FUTURA, HORARIO);

            assertNotNull(consulta);
            assertEquals(GESTANTE_ID, consulta.getGestanteId());
            assertEquals(MEDICO_ID, consulta.getMedicoId());
            assertEquals(UNIDADE_ID, consulta.getUnidadeId());
            assertEquals(DATA_FUTURA, consulta.getData());
            assertEquals(HORARIO, consulta.getHorario());
            assertEquals(AppointmentStatus.AGENDADA, consulta.getStatus());
            assertTrue(consulta.estaAgendada());
            assertFalse(consulta.estaCancelada());
        }

        @Test
        @DisplayName("Deve permitir data de hoje")
        void shouldAllowTodayDate() {
            Appointment consulta = new Appointment(GESTANTE_ID, null, MEDICO_ID, UNIDADE_ID, LocalDate.now(), HORARIO);
            assertEquals(AppointmentStatus.AGENDADA, consulta.getStatus());
        }

        @Test
        @DisplayName("Deve lançar exceção quando gestanteId é nulo")
        void deveLancarQuandoGestanteIdNulo() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Appointment(null, null, MEDICO_ID, UNIDADE_ID, DATA_FUTURA, HORARIO));
        }

        @Test
        @DisplayName("Deve lançar exceção quando medicoId é nulo")
        void deveLancarQuandoMedicoIdNulo() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Appointment(GESTANTE_ID, null, null, UNIDADE_ID, DATA_FUTURA, HORARIO));
        }

        @Test
        @DisplayName("Deve lançar exceção quando unidadeId é nulo")
        void deveLancarQuandoUnidadeIdNulo() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Appointment(GESTANTE_ID, null, MEDICO_ID, null, DATA_FUTURA, HORARIO));
        }

        @Test
        @DisplayName("Deve lançar exceção quando data é nula")
        void shouldThrowWhenDateIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Appointment(GESTANTE_ID, null, MEDICO_ID, UNIDADE_ID, null, HORARIO));
        }

        @Test
        @DisplayName("Deve lançar exceção quando horário é nulo")
        void deveLancarQuandoHorarioNulo() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Appointment(GESTANTE_ID, null, MEDICO_ID, UNIDADE_ID, DATA_FUTURA, null));
        }

        @Test
        @DisplayName("Deve lançar exceção quando data é no passado")
        void shouldThrowWhenDateIsInPast() {
            LocalDate ontem = LocalDate.now().minusDays(1);
            assertThrows(IllegalArgumentException.class, () ->
                    new Appointment(GESTANTE_ID, null, MEDICO_ID, UNIDADE_ID, ontem, HORARIO));
        }
    }

    @Nested
    @DisplayName("Cancelamento")
    class Cancelamento {

        @Test
        @DisplayName("Deve cancelar consulta agendada com motivo")
        void deveCancelarComMotivo() {
            Appointment consulta = new Appointment(GESTANTE_ID, null, MEDICO_ID, UNIDADE_ID, DATA_FUTURA, HORARIO);
            consulta.setId(10L);

            consulta.cancelar(CancellationReason.GESTANTE_DESISTIU);

            assertEquals(AppointmentStatus.CANCELADA, consulta.getStatus());
            assertEquals(CancellationReason.GESTANTE_DESISTIU, consulta.getMotivoCancelamento());
            assertNotNull(consulta.getDataCancelamento());
            assertTrue(consulta.estaCancelada());
            assertFalse(consulta.estaAgendada());
        }

        @Test
        @DisplayName("Deve lançar exceção ao cancelar consulta já cancelada")
        void shouldThrowWhenCancellingAlreadyCancelled() {
            Appointment consulta = new Appointment(GESTANTE_ID, null, MEDICO_ID, UNIDADE_ID, DATA_FUTURA, HORARIO);
            consulta.setId(10L);
            consulta.cancelar(CancellationReason.MEDICO_INDISPONIVEL);

            assertThrows(IllegalStateException.class, () ->
                    consulta.cancelar(CancellationReason.OUTRO));
        }

        @Test
        @DisplayName("Deve lançar exceção ao cancelar consulta já realizada")
        void deveLancarAoCancelarJaRealizada() {
            Appointment consulta = new Appointment(1L, GESTANTE_ID, null, MEDICO_ID, UNIDADE_ID, DATA_FUTURA, HORARIO,
                    AppointmentStatus.REALIZADA, null, LocalDateTime.now(), null);

            assertThrows(IllegalStateException.class, () ->
                    consulta.cancelar(CancellationReason.OUTRO));
        }

        @Test
        @DisplayName("Deve lançar exceção quando motivo é nulo")
        void shouldThrowWhenReasonIsNull() {
            Appointment consulta = new Appointment(GESTANTE_ID, null, MEDICO_ID, UNIDADE_ID, DATA_FUTURA, HORARIO);

            assertThrows(IllegalArgumentException.class, () ->
                    consulta.cancelar(null));
        }
    }

    @Nested
    @DisplayName("estaAgendada e estaCancelada")
    class Status {

        @Test
        @DisplayName("estaAgendada retorna true para status AGENDADA")
        void estaAgendadaRetornaTrue() {
            Appointment consulta = new Appointment(GESTANTE_ID, null, MEDICO_ID, UNIDADE_ID, DATA_FUTURA, HORARIO);
            assertTrue(consulta.estaAgendada());
        }

        @Test
        @DisplayName("estaCancelada retorna true após cancelar")
        void shouldReturnTrueForCancelledAfterCancelling() {
            Appointment consulta = new Appointment(GESTANTE_ID, null, MEDICO_ID, UNIDADE_ID, DATA_FUTURA, HORARIO);
            consulta.setId(1L);
            consulta.cancelar(CancellationReason.EMERGENCIA);
            assertTrue(consulta.estaCancelada());
        }
    }
}
