package br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Weekday;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity.DoctorScheduleEntity;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.repository.DoctorScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorScheduleGatewayImpl")
class DoctorScheduleGatewayImplTest {

    @Mock
    private DoctorScheduleRepository repository;

    @InjectMocks
    private DoctorScheduleGatewayImpl gateway;

    @Test
    @DisplayName("salvar persiste e retorna domínio mapeado")
    void salvar() {
        DoctorSchedule dom = new DoctorSchedule(1L, 1L, Set.of(Weekday.SEGUNDA, Weekday.QUARTA),
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        DoctorScheduleEntity entitySalva = new DoctorScheduleEntity(10L, 1L, 1L, Set.of(Weekday.SEGUNDA, Weekday.QUARTA),
                LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
        when(repository.save(any(DoctorScheduleEntity.class))).thenReturn(entitySalva);

        DoctorSchedule result = gateway.salvar(dom);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(1L, result.getMedicoId());
        assertEquals(30, result.getDuracaoConsultaMinutos());
        ArgumentCaptor<DoctorScheduleEntity> captor = ArgumentCaptor.forClass(DoctorScheduleEntity.class);
        verify(repository).save(captor.capture());
        assertEquals(1L, captor.getValue().getMedicoId());
    }

    @Test
    @DisplayName("buscarPorId retorna Optional vazio quando não existe")
    void buscarPorIdVazio() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(gateway.buscarPorId(99L).isEmpty());
    }

    @Test
    @DisplayName("buscarPorId retorna Optional com DoctorSchedule quando existe")
    void buscarPorIdPresente() {
        DoctorScheduleEntity entity = new DoctorScheduleEntity(5L, 1L, 1L, Set.of(Weekday.TERCA),
                LocalTime.of(9, 0), LocalTime.of(17, 0), 20);
        when(repository.findById(5L)).thenReturn(Optional.of(entity));

        Optional<DoctorSchedule> opt = gateway.buscarPorId(5L);

        assertTrue(opt.isPresent());
        assertEquals(5L, opt.get().getId());
        assertEquals(1L, opt.get().getMedicoId());
        assertEquals(20, opt.get().getDuracaoConsultaMinutos());
    }

    @Test
    @DisplayName("buscarPorMedicoId retorna Optional vazio quando não existe")
    void buscarPorMedicoIdVazio() {
        when(repository.findByMedicoId(2L)).thenReturn(Optional.empty());

        assertTrue(gateway.buscarPorMedicoId(2L).isEmpty());
    }

    @Test
    @DisplayName("buscarPorMedicoId retorna Optional com agenda quando existe")
    void buscarPorMedicoIdPresente() {
        DoctorScheduleEntity entity = new DoctorScheduleEntity(3L, 1L, 1L, Set.of(Weekday.QUINTA),
                LocalTime.of(8, 0), LocalTime.of(18, 0), 40);
        when(repository.findByMedicoId(1L)).thenReturn(Optional.of(entity));

        Optional<DoctorSchedule> opt = gateway.buscarPorMedicoId(1L);

        assertTrue(opt.isPresent());
        assertEquals(3L, opt.get().getId());
        assertEquals(1L, opt.get().getMedicoId());
    }

    @Test
    @DisplayName("excluirPorId chama deleteById")
    void excluirPorId() {
        gateway.excluirPorId(7L);
        verify(repository).deleteById(7L);
    }
}
