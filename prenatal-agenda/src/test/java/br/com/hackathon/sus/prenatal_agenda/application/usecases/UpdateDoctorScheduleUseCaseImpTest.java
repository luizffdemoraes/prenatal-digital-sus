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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateDoctorScheduleUseCaseImp")
class UpdateDoctorScheduleUseCaseImpTest {

    @Mock
    private DoctorScheduleGateway doctorScheduleGateway;
    @Mock
    private DoctorGateway doctorGateway;

    private UpdateDoctorScheduleUseCaseImp useCase;

    private static final String CRM = "12345";
    private static final Long MEDICO_ID = 1L;
    private static final Long UNIDADE_ID = 2L;
    private static final Set<Weekday> DIAS = Set.of(Weekday.SEGUNDA, Weekday.TERCA, Weekday.QUARTA);
    private static final LocalTime INICIO = LocalTime.of(9, 0);
    private static final LocalTime FIM = LocalTime.of(17, 0);
    private static final Integer DURACAO = 30;

    @BeforeEach
    void setUp() {
        useCase = new UpdateDoctorScheduleUseCaseImp(doctorScheduleGateway, doctorGateway);
    }

    @Test
    @DisplayName("Deve lançar exceção quando médico não encontrado por CRM")
    void shouldThrowWhenDoctorNotFound() {
        UpdateDoctorScheduleRequest request = new UpdateDoctorScheduleRequest(UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);
        when(doctorGateway.buscarPorCrm(CRM)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(CRM, request));
        verify(doctorScheduleGateway, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando agenda não encontrada")
    void shouldThrowWhenScheduleNotFound() {
        UpdateDoctorScheduleRequest request = new UpdateDoctorScheduleRequest(UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);
        when(doctorGateway.buscarPorCrm(CRM)).thenReturn(Optional.of(MEDICO_ID));
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(CRM, request));
        verify(doctorScheduleGateway, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve atualizar agenda com sucesso")
    void shouldUpdateSuccessfully() {
        DoctorSchedule existente = new DoctorSchedule(100L, MEDICO_ID, 1L, Set.of(Weekday.SEGUNDA), LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        DoctorSchedule atualizada = new DoctorSchedule(100L, MEDICO_ID, UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);
        UpdateDoctorScheduleRequest request = new UpdateDoctorScheduleRequest(UNIDADE_ID, DIAS, INICIO, FIM, DURACAO);

        when(doctorGateway.buscarPorCrm(CRM)).thenReturn(Optional.of(MEDICO_ID));
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(existente));
        when(doctorScheduleGateway.salvar(any(DoctorSchedule.class))).thenReturn(atualizada);

        DoctorSchedule result = useCase.execute(CRM, request);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(UNIDADE_ID, result.getUnidadeId());
        assertEquals(INICIO, result.getHorarioInicio());
        assertEquals(FIM, result.getHorarioFim());
        verify(doctorScheduleGateway).salvar(any(DoctorSchedule.class));
    }
}
