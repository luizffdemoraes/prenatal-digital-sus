package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.UpdateDoctorScheduleRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Weekday;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateDoctorScheduleUseCaseImp")
class UpdateDoctorScheduleUseCaseImpTest {

    @Mock
    private DoctorScheduleGateway doctorScheduleGateway;
    @Mock
    private DoctorGateway doctorGateway;

    private UpdateDoctorScheduleUseCaseImp useCase;

    private static final Long MEDICO_ID = 1L;
    private static final Long UNIDADE_ID = 2L;
    private static final Set<Weekday> DIAS_NOVOS = Set.of(Weekday.SEGUNDA, Weekday.TERCA, Weekday.QUARTA);
    private static final LocalTime INICIO_NOVO = LocalTime.of(7, 30);
    private static final LocalTime FIM_NOVO = LocalTime.of(11, 30);
    private static final Integer DURACAO_NOVA = 20;

    @BeforeEach
    void setUp() {
        useCase = new UpdateDoctorScheduleUseCaseImp(doctorScheduleGateway, doctorGateway);
    }

    @Test
    @DisplayName("deve lançar exceção quando médico não encontrado")
    void deveLancarQuandoMedicoNaoEncontrado() {
        UpdateDoctorScheduleRequest request = new UpdateDoctorScheduleRequest(UNIDADE_ID, DIAS_NOVOS, INICIO_NOVO, FIM_NOVO, DURACAO_NOVA);
        when(doctorGateway.buscarPorCrm("CRM-INVALIDO")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute("CRM-INVALIDO", request));
    }

    @Test
    @DisplayName("deve lançar exceção quando agenda não encontrada")
    void deveLancarQuandoAgendaNaoEncontrada() {
        UpdateDoctorScheduleRequest request = new UpdateDoctorScheduleRequest(UNIDADE_ID, DIAS_NOVOS, INICIO_NOVO, FIM_NOVO, DURACAO_NOVA);
        when(doctorGateway.buscarPorCrm("CRM-X")).thenReturn(Optional.of(MEDICO_ID));
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute("CRM-X", request));
    }

    @Test
    @DisplayName("deve atualizar agenda com sucesso")
    void deveAtualizarComSucesso() {
        UpdateDoctorScheduleRequest request = new UpdateDoctorScheduleRequest(UNIDADE_ID, DIAS_NOVOS, INICIO_NOVO, FIM_NOVO, DURACAO_NOVA);
        when(doctorGateway.buscarPorCrm("CRM-X")).thenReturn(Optional.of(MEDICO_ID));

        DoctorSchedule existente = new DoctorSchedule(50L, MEDICO_ID, UNIDADE_ID, Set.of(Weekday.SEGUNDA), LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(existente));

        DoctorSchedule atualizada = new DoctorSchedule(50L, MEDICO_ID, UNIDADE_ID, DIAS_NOVOS, INICIO_NOVO, FIM_NOVO, DURACAO_NOVA);
        when(doctorScheduleGateway.salvar(any(DoctorSchedule.class))).thenReturn(atualizada);

        DoctorSchedule result = useCase.execute("CRM-X", request);

        assertNotNull(result);
        assertEquals(DIAS_NOVOS, result.getDiasAtendimento());
        assertEquals(INICIO_NOVO, result.getHorarioInicio());
        assertEquals(FIM_NOVO, result.getHorarioFim());
        assertEquals(DURACAO_NOVA, result.getDuracaoConsultaMinutos());
        verify(doctorScheduleGateway).salvar(any(DoctorSchedule.class));
    }
}
