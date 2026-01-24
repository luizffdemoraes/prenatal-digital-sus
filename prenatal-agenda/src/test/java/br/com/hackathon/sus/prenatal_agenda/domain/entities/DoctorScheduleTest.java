package br.com.hackathon.sus.prenatal_agenda.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DoctorSchedule - Entidade de domínio")
class DoctorScheduleTest {

    private static final Long MEDICO_ID = 1L;
    private static final Long UNIDADE_ID = 2L;
    private static final Set<Weekday> DIAS = Set.of(Weekday.SEGUNDA, Weekday.QUARTA, Weekday.SEXTA);
    private static final LocalTime INICIO = LocalTime.of(8, 0);
    private static final LocalTime FIM = LocalTime.of(12, 0);
    private static final Integer DURACAO = 30;

    @Nested
    @DisplayName("Criação e validação")
    class Criacao {

        @Test
        @DisplayName("deve criar agenda com dados válidos")
        void deveCriarAgendaComDadosValidos() {
            DoctorSchedule agenda = new DoctorSchedule(MEDICO_ID, UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);

            assertNotNull(agenda);
            assertEquals(MEDICO_ID, agenda.getMedicoId());
            assertEquals(UNIDADE_ID, agenda.getUnidadeId());
            assertEquals(DIAS, agenda.getDiasAtendimento());
            assertEquals(INICIO, agenda.getHorarioInicio());
            assertEquals(FIM, agenda.getHorarioFim());
            assertEquals(DURACAO, agenda.getDuracaoConsultaMinutos());
        }

        @Test
        @DisplayName("deve lançar exceção quando medicoId é nulo")
        void deveLancarQuandoMedicoIdNulo() {
            assertThrows(IllegalArgumentException.class, () ->
                    new DoctorSchedule(null, UNIDADE_ID, DIAS, INICIO, FIM, DURACAO));
        }

        @Test
        @DisplayName("deve lançar exceção quando unidadeId é nulo")
        void deveLancarQuandoUnidadeIdNulo() {
            assertThrows(IllegalArgumentException.class, () ->
                    new DoctorSchedule(MEDICO_ID, null, DIAS, INICIO, FIM, DURACAO));
        }

        @Test
        @DisplayName("deve lançar exceção quando diasAtendimento é nulo ou vazio")
        void deveLancarQuandoDiasNuloOuVazio() {
            assertThrows(IllegalArgumentException.class, () ->
                    new DoctorSchedule(MEDICO_ID, UNIDADE_ID, null, INICIO, FIM, DURACAO));

            assertThrows(IllegalArgumentException.class, () ->
                    new DoctorSchedule(MEDICO_ID, UNIDADE_ID, Set.of(), INICIO, FIM, DURACAO));
        }

        @Test
        @DisplayName("deve lançar exceção quando horário de início é após o fim")
        void deveLancarQuandoInicioAposFim() {
            assertThrows(IllegalArgumentException.class, () ->
                    new DoctorSchedule(MEDICO_ID, UNIDADE_ID, DIAS, LocalTime.of(14, 0), LocalTime.of(10, 0), DURACAO));
        }

        @Test
        @DisplayName("deve lançar exceção quando horário de início igual ao fim")
        void deveLancarQuandoInicioIgualFim() {
            LocalTime t = LocalTime.of(9, 0);
            assertThrows(IllegalArgumentException.class, () ->
                    new DoctorSchedule(MEDICO_ID, UNIDADE_ID, DIAS, t, t, DURACAO));
        }

        @Test
        @DisplayName("deve lançar exceção quando duração é nula ou <= 0")
        void deveLancarQuandoDuracaoInvalida() {
            assertThrows(IllegalArgumentException.class, () ->
                    new DoctorSchedule(MEDICO_ID, UNIDADE_ID, DIAS, INICIO, FIM, null));

            assertThrows(IllegalArgumentException.class, () ->
                    new DoctorSchedule(MEDICO_ID, UNIDADE_ID, DIAS, INICIO, FIM, 0));
        }
    }

    @Nested
    @DisplayName("atendeNoDia")
    class AtendeNoDia {

        @Test
        @DisplayName("retorna true para dia da semana configurado")
        void retornaTrueParaDiaConfigurado() {
            DoctorSchedule agenda = new DoctorSchedule(MEDICO_ID, UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);
            assertTrue(agenda.atendeNoDia(Weekday.SEGUNDA));
            assertTrue(agenda.atendeNoDia(Weekday.QUARTA));
            assertTrue(agenda.atendeNoDia(Weekday.SEXTA));
        }

        @Test
        @DisplayName("retorna false para dia da semana não configurado")
        void retornaFalseParaDiaNaoConfigurado() {
            DoctorSchedule agenda = new DoctorSchedule(MEDICO_ID, UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);
            assertFalse(agenda.atendeNoDia(Weekday.TERCA));
            assertFalse(agenda.atendeNoDia(Weekday.DOMINGO));
        }
    }

    @Nested
    @DisplayName("horarioDentroDoPeriodo")
    class HorarioDentroDoPeriodo {

        @Test
        @DisplayName("retorna true para horário dentro do período")
        void retornaTrueParaHorarioDentro() {
            DoctorSchedule agenda = new DoctorSchedule(MEDICO_ID, UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);
            assertTrue(agenda.horarioDentroDoPeriodo(LocalTime.of(8, 0)));
            assertTrue(agenda.horarioDentroDoPeriodo(LocalTime.of(10, 0)));
            assertTrue(agenda.horarioDentroDoPeriodo(LocalTime.of(12, 0)));
        }

        @Test
        @DisplayName("retorna false para horário antes do início")
        void retornaFalseParaHorarioAntes() {
            DoctorSchedule agenda = new DoctorSchedule(MEDICO_ID, UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);
            assertFalse(agenda.horarioDentroDoPeriodo(LocalTime.of(7, 59)));
            assertFalse(agenda.horarioDentroDoPeriodo(LocalTime.of(7, 0)));
        }

        @Test
        @DisplayName("retorna false para horário após o fim")
        void retornaFalseParaHorarioApos() {
            DoctorSchedule agenda = new DoctorSchedule(MEDICO_ID, UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);
            assertFalse(agenda.horarioDentroDoPeriodo(LocalTime.of(12, 1)));
            assertFalse(agenda.horarioDentroDoPeriodo(LocalTime.of(14, 0)));
        }
    }
}
