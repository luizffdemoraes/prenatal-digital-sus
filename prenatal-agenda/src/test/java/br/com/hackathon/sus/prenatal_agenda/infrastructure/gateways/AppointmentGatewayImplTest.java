package br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.AppointmentStatus;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity.AppointmentEntity;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.repository.AppointmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentGatewayImpl")
class AppointmentGatewayImplTest {

    @Mock
    private AppointmentRepository repository;

    @InjectMocks
    private AppointmentGatewayImpl gateway;

    @Test
    @DisplayName("salvar persiste e retorna domínio mapeado")
    void salvar() {
        Appointment dom = new Appointment(10L, "12345678900", 1L, 1L,
                LocalDate.now().plusDays(1), LocalTime.of(9, 0));
        dom.setId(null);
        AppointmentEntity entitySalva = new AppointmentEntity(50L, 10L, "12345678900", 1L, 1L,
                LocalDate.now().plusDays(1), LocalTime.of(9, 0), AppointmentStatus.AGENDADA, null,
                LocalDateTime.now(), null);
        when(repository.save(any(AppointmentEntity.class))).thenReturn(entitySalva);

        Appointment result = gateway.salvar(dom);

        assertNotNull(result);
        assertEquals(50L, result.getId());
        assertEquals(10L, result.getGestanteId());
        assertEquals(1L, result.getMedicoId());
        assertEquals(AppointmentStatus.AGENDADA, result.getStatus());
        ArgumentCaptor<AppointmentEntity> captor = ArgumentCaptor.forClass(AppointmentEntity.class);
        verify(repository).save(captor.capture());
        assertEquals(10L, captor.getValue().getGestanteId());
    }

    @Test
    @DisplayName("buscarPorId retorna Optional vazio quando não existe")
    void buscarPorIdVazio() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(gateway.buscarPorId(99L).isEmpty());
    }

    @Test
    @DisplayName("buscarPorId retorna Optional com Appointment quando existe")
    void buscarPorIdPresente() {
        AppointmentEntity entity = new AppointmentEntity(1L, 10L, "123", 1L, 1L,
                LocalDate.now().plusDays(1), LocalTime.of(9, 0), AppointmentStatus.AGENDADA, null,
                LocalDateTime.now(), null);
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<Appointment> opt = gateway.buscarPorId(1L);

        assertTrue(opt.isPresent());
        assertEquals(1L, opt.get().getId());
        assertEquals(10L, opt.get().getGestanteId());
    }

    @Test
    @DisplayName("buscarPorGestanteId retorna lista mapeada")
    void buscarPorGestanteId() {
        AppointmentEntity e1 = new AppointmentEntity(1L, 5L, "111", 1L, 1L,
                LocalDate.now().plusDays(1), LocalTime.of(9, 0), AppointmentStatus.AGENDADA, null,
                LocalDateTime.now(), null);
        when(repository.findByGestanteId(5L)).thenReturn(List.of(e1));

        List<Appointment> result = gateway.buscarPorGestanteId(5L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(5L, result.get(0).getGestanteId());
    }

    @Test
    @DisplayName("buscarConsultasAgendadas chama repositório com status AGENDADA")
    void buscarConsultasAgendadas() {
        LocalDate data = LocalDate.now().plusDays(1);
        LocalTime horario = LocalTime.of(10, 0);
        when(repository.findByMedicoIdAndDataAndHorarioAndStatus(1L, data, horario, AppointmentStatus.AGENDADA))
                .thenReturn(List.of());

        List<Appointment> result = gateway.buscarConsultasAgendadas(1L, data, horario);

        assertTrue(result.isEmpty());
        verify(repository).findByMedicoIdAndDataAndHorarioAndStatus(1L, data, horario, AppointmentStatus.AGENDADA);
    }

    @Test
    @DisplayName("buscarConsultasAgendadasPorMedicoEData chama repositório com status AGENDADA")
    void buscarConsultasAgendadasPorMedicoEData() {
        LocalDate data = LocalDate.now().plusDays(1);
        AppointmentEntity e = new AppointmentEntity(2L, 10L, "222", 1L, 1L, data, LocalTime.of(14, 0),
                AppointmentStatus.AGENDADA, null, LocalDateTime.now(), null);
        when(repository.findByMedicoIdAndDataAndStatus(1L, data, AppointmentStatus.AGENDADA)).thenReturn(List.of(e));

        List<Appointment> result = gateway.buscarConsultasAgendadasPorMedicoEData(1L, data);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    @DisplayName("existeAgendamentoPorMedico retorna true quando existe")
    void existeAgendamentoPorMedicoTrue() {
        when(repository.existsByMedicoIdAndStatus(1L, AppointmentStatus.AGENDADA)).thenReturn(true);

        assertTrue(gateway.existeAgendamentoPorMedico(1L));
    }

    @Test
    @DisplayName("existeAgendamentoPorMedico retorna false quando não existe")
    void existeAgendamentoPorMedicoFalse() {
        when(repository.existsByMedicoIdAndStatus(2L, AppointmentStatus.AGENDADA)).thenReturn(false);

        assertFalse(gateway.existeAgendamentoPorMedico(2L));
    }
}
