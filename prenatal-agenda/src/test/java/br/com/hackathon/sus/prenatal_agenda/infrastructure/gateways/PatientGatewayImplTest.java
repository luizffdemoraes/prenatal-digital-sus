package br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientGatewayImpl")
class PatientGatewayImplTest {

    @InjectMocks
    private PatientGatewayImpl gateway;

    @Test
    @DisplayName("buscarPorCpf retorna empty quando cpf null")
    void buscarPorCpfNull() {
        assertTrue(gateway.buscarPorCpf(null).isEmpty());
    }

    @Test
    @DisplayName("buscarPorCpf retorna empty quando cpf em branco")
    void buscarPorCpfBlank() {
        assertTrue(gateway.buscarPorCpf("   ").isEmpty());
    }

    @Test
    @DisplayName("buscarPorCpf retorna id quando CPF encontrado")
    void buscarPorCpfEncontrado() {
        assertEquals(Optional.of(1L), gateway.buscarPorCpf("11111111111"));
        assertEquals(Optional.of(1L), gateway.buscarPorCpf("111.111.111-11"));
    }

    @Test
    @DisplayName("buscarPorCpf normaliza (trim, só dígitos)")
    void buscarPorCpfNormalizado() {
        assertEquals(Optional.of(1L), gateway.buscarPorCpf("  111.111.111-11  "));
    }

    @Test
    @DisplayName("buscarPorCpf retorna empty quando CPF não existe")
    void buscarPorCpfNaoEncontrado() {
        assertTrue(gateway.buscarPorCpf("99999999999").isEmpty());
    }

    @Test
    @DisplayName("findNameById retorna empty quando id null")
    void findNameByIdNull() {
        assertTrue(gateway.findNameById(null).isEmpty());
    }

    @Test
    @DisplayName("findNameById retorna nome quando id existe")
    void findNameByIdEncontrado() {
        assertEquals(Optional.of("Maria"), gateway.findNameById(1L));
        assertEquals(Optional.of("Ana"), gateway.findNameById(2L));
        assertEquals(Optional.of("Joana"), gateway.findNameById(5L));
    }

    @Test
    @DisplayName("findNameById retorna empty quando id não existe")
    void findNameByIdNaoEncontrado() {
        assertTrue(gateway.findNameById(999L).isEmpty());
    }
}
