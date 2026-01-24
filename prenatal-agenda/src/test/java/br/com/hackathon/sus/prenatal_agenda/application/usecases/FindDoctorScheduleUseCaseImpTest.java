package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Weekday;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindDoctorScheduleUseCaseImp")
class FindDoctorScheduleUseCaseImpTest {

    @Mock
    private DoctorScheduleGateway doctorScheduleGateway;

    private FindDoctorScheduleUseCaseImp useCase;

    private static final Long MEDICO_ID = 1L;

    @BeforeEach
    void setUp() {
        useCase = new FindDoctorScheduleUseCaseImp(doctorScheduleGateway);
    }

    @Test
    @DisplayName("deve retornar agenda quando encontrada")
    void deveRetornarAgendaQuandoEncontrada() {
        DoctorSchedule agenda = new DoctorSchedule(10L, MEDICO_ID, 2L, Set.of(Weekday.SEGUNDA), LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(agenda));

        Optional<DoctorSchedule> result = useCase.execute(MEDICO_ID);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
        assertEquals(MEDICO_ID, result.get().getMedicoId());
    }

    @Test
    @DisplayName("deve retornar vazio quando agenda não encontrada")
    void deveRetornarVazioQuandoNaoEncontrada() {
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.empty());

        Optional<DoctorSchedule> result = useCase.execute(MEDICO_ID);

        assertTrue(result.isEmpty());
    }
}
