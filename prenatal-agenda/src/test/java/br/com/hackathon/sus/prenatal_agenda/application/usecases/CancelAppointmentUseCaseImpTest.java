package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.AppointmentStatus;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.CancellationReason;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AppointmentGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CancelAppointmentUseCaseImp")
class CancelAppointmentUseCaseImpTest {

    @Mock
    private AppointmentGateway appointmentGateway;

    private CancelAppointmentUseCaseImp useCase;

    private static final Long CONSULTA_ID = 1L;

    @BeforeEach
    void setUp() {
        useCase = new CancelAppointmentUseCaseImp(appointmentGateway);
    }

    @Test
    @DisplayName("Deve lançar exceção quando consulta não encontrada")
    void shouldThrowWhenAppointmentNotFound() {
        when(appointmentGateway.buscarPorId(CONSULTA_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                useCase.execute(CONSULTA_ID, CancellationReason.GESTANTE_DESISTIU));
    }

    @Test
    @DisplayName("Deve cancelar consulta com sucesso e salvar")
    void shouldCancelSuccessfully() {
        Appointment consulta = new Appointment(10L, 20L, 30L, LocalDate.now().plusDays(1), LocalTime.of(9, 0));
        consulta.setId(CONSULTA_ID);
        when(appointmentGateway.buscarPorId(CONSULTA_ID)).thenReturn(Optional.of(consulta));
        when(appointmentGateway.salvar(any(Appointment.class))).thenAnswer(i -> i.getArgument(0));

        Appointment result = useCase.execute(CONSULTA_ID, CancellationReason.MEDICO_INDISPONIVEL);

        assertEquals(AppointmentStatus.CANCELADA, result.getStatus());
        assertEquals(CancellationReason.MEDICO_INDISPONIVEL, result.getMotivoCancelamento());
        verify(appointmentGateway).salvar(any(Appointment.class));
    }
}
