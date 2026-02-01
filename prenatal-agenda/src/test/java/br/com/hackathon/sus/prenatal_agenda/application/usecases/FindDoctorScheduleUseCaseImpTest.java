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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindDoctorScheduleUseCaseImp")
class FindDoctorScheduleUseCaseImpTest {

    @Mock
    private DoctorScheduleGateway doctorScheduleGateway;

    private FindDoctorScheduleUseCaseImp useCase;

    private static final Long MEDICO_ID = 1L;
    private static final Set<Weekday> DIAS = Set.of(Weekday.SEGUNDA, Weekday.QUARTA);
    private static final LocalTime INICIO = LocalTime.of(8, 0);
    private static final LocalTime FIM = LocalTime.of(12, 0);

    @BeforeEach
    void setUp() {
        useCase = new FindDoctorScheduleUseCaseImp(doctorScheduleGateway);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando agenda não existe")
    void shouldReturnEmptyWhenNotFound() {
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.empty());

        Optional<DoctorSchedule> result = useCase.execute(MEDICO_ID);

        assertTrue(result.isEmpty());
        verify(doctorScheduleGateway).buscarPorMedicoId(MEDICO_ID);
    }

    @Test
    @DisplayName("Deve retornar agenda quando encontrada")
    void shouldReturnScheduleWhenFound() {
        DoctorSchedule schedule = new DoctorSchedule(100L, MEDICO_ID, 1L, DIAS, INICIO, FIM, 30);
        when(doctorScheduleGateway.buscarPorMedicoId(MEDICO_ID)).thenReturn(Optional.of(schedule));

        Optional<DoctorSchedule> result = useCase.execute(MEDICO_ID);

        assertTrue(result.isPresent());
        assertEquals(100L, result.get().getId());
        assertEquals(MEDICO_ID, result.get().getMedicoId());
        verify(doctorScheduleGateway).buscarPorMedicoId(MEDICO_ID);
    }
}
