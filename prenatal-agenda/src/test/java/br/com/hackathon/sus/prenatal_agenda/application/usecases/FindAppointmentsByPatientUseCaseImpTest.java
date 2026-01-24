package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AppointmentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindAppointmentsByPatientUseCaseImp")
class FindAppointmentsByPatientUseCaseImpTest {

    @Mock
    private AppointmentGateway appointmentGateway;

    private FindAppointmentsByPatientUseCaseImp useCase;

    private static final Long GESTANTE_ID = 1L;

    @BeforeEach
    void setUp() {
        useCase = new FindAppointmentsByPatientUseCaseImp(appointmentGateway);
    }

    @Test
    @DisplayName("deve retornar lista de consultas da gestante")
    void deveRetornarConsultas() {
        Appointment c = new Appointment(GESTANTE_ID, 2L, 3L, LocalDate.now().plusDays(1), LocalTime.of(9, 0));
        c.setId(10L);
        when(appointmentGateway.buscarPorGestanteId(GESTANTE_ID)).thenReturn(List.of(c));

        List<Appointment> result = useCase.execute(GESTANTE_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(GESTANTE_ID, result.get(0).getGestanteId());
    }

    @Test
    @DisplayName("deve retornar lista vazia quando gestante não tem consultas")
    void deveRetornarListaVazia() {
        when(appointmentGateway.buscarPorGestanteId(GESTANTE_ID)).thenReturn(List.of());

        List<Appointment> result = useCase.execute(GESTANTE_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
