package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.CreateDoctorScheduleRequest;
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
@DisplayName("CreateDoctorScheduleUseCaseImp")
class CreateDoctorScheduleUseCaseImpTest {

    @Mock
    private DoctorScheduleGateway doctorScheduleGateway;
    @Mock
    private DoctorGateway doctorGateway;

    private CreateDoctorScheduleUseCaseImp useCase;

    private static final Long MEDICO_ID = 1L;
    private static final Long UNIDADE_ID = 2L;
    private static final Set<Weekday> DIAS = Set.of(Weekday.SEGUNDA, Weekday.QUARTA);
    private static final LocalTime INICIO = LocalTime.of(8, 0);
    private static final LocalTime FIM = LocalTime.of(12, 0);
    private static final Integer DURACAO = 30;

    @BeforeEach
    void setUp() {
        useCase = new CreateDoctorScheduleUseCaseImp(doctorScheduleGateway, doctorGateway);
    }

    @Test
    @DisplayName("deve lançar exceção quando médico não encontrado por CRM")
    void deveLancarQuandoMedicoNaoEncontrado() {
        CreateDoctorScheduleRequest request = new CreateDoctorScheduleRequest("CRM-X", "Dr. João", "Cardio", UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);
        when(doctorGateway.buscarPorCrm("CRM-X")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(request),
                "Médico não encontrado para o CRM informado: CRM-X");
    }

    @Test
    @DisplayName("deve lançar exceção quando já existe agenda para o médico")
    void deveLancarQuandoAgendaJaExiste() {
        CreateDoctorScheduleRequest request = new CreateDoctorScheduleRequest("CRM-X", "Dr. João", "Cardio", UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);
        when(doctorGateway.buscarPorCrm("CRM-X")).thenReturn(Optional.of(MEDICO_ID));
        DoctorSchedule existente = new DoctorSchedule(MEDICO_ID, UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(existente));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(request));
    }

    @Test
    @DisplayName("deve criar agenda com sucesso")
    void deveCriarComSucesso() {
        CreateDoctorScheduleRequest request = new CreateDoctorScheduleRequest("CRM-X", "Dr. João", "Cardio", UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);
        when(doctorGateway.buscarPorCrm("CRM-X")).thenReturn(Optional.of(MEDICO_ID));
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.empty());

        DoctorSchedule agendaSalva = new DoctorSchedule(100L, MEDICO_ID, UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);
        when(doctorScheduleGateway.salvar(any(DoctorSchedule.class))).thenReturn(agendaSalva);

        DoctorSchedule result = useCase.execute(request);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(MEDICO_ID, result.getMedicoId());
        verify(doctorScheduleGateway).salvar(any(DoctorSchedule.class));
    }
}
