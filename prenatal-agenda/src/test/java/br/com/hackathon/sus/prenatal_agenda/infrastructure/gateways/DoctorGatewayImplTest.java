package br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorGatewayImpl")
class DoctorGatewayImplTest {

    @InjectMocks
    private DoctorGatewayImpl gateway;

    @Test
    @DisplayName("buscarPorCrm retorna empty quando crm null")
    void buscarPorCrmNull() {
        assertTrue(gateway.buscarPorCrm(null).isEmpty());
    }

    @Test
    @DisplayName("buscarPorCrm retorna empty quando crm em branco")
    void buscarPorCrmBlank() {
        assertTrue(gateway.buscarPorCrm("   ").isEmpty());
    }

    @Test
    @DisplayName("buscarPorCrm retorna id quando CRM encontrado")
    void buscarPorCrmEncontrado() {
        assertEquals(Optional.of(1L), gateway.buscarPorCrm("12345"));
        assertEquals(Optional.of(1L), gateway.buscarPorCrm("123456"));
    }

    @Test
    @DisplayName("buscarPorCrm normaliza e encontra (trim, só dígitos)")
    void buscarPorCrmNormalizado() {
        assertEquals(Optional.of(1L), gateway.buscarPorCrm("  12345  "));
    }

    @Test
    @DisplayName("buscarPorCrm retorna empty quando CRM não existe")
    void buscarPorCrmNaoEncontrado() {
        assertTrue(gateway.buscarPorCrm("99999").isEmpty());
    }

    @Test
    @DisplayName("buscarPorNome retorna empty quando nome null ou blank")
    void buscarPorNomeNullOuBlank() {
        assertTrue(gateway.buscarPorNome(null, 1L).isEmpty());
        assertTrue(gateway.buscarPorNome("  ", 1L).isEmpty());
    }

    @Test
    @DisplayName("buscarPorNome retorna id quando nome encontrado")
    void buscarPorNomeEncontrado() {
        assertEquals(Optional.of(1L), gateway.buscarPorNome("dr. joão", 1L));
        assertEquals(Optional.of(1L), gateway.buscarPorNome("joão", 1L));
        assertEquals(Optional.of(1L), gateway.buscarPorNome("dr silva", 1L));
        assertEquals(Optional.of(1L), gateway.buscarPorNome("silva", 1L));
    }

    @Test
    @DisplayName("buscarPorNome ignora case")
    void buscarPorNomeCaseInsensitive() {
        assertEquals(Optional.of(1L), gateway.buscarPorNome("DR. JOÃO", 1L));
    }

    @Test
    @DisplayName("buscarPorEspecialidade retorna empty quando null ou blank")
    void buscarPorEspecialidadeNullOuBlank() {
        assertTrue(gateway.buscarPorEspecialidade(null, 1L).isEmpty());
        assertTrue(gateway.buscarPorEspecialidade("  ", 1L).isEmpty());
    }

    @Test
    @DisplayName("buscarPorEspecialidade retorna id quando encontrado")
    void buscarPorEspecialidadeEncontrado() {
        assertEquals(Optional.of(1L), gateway.buscarPorEspecialidade("obstetrícia", 1L));
        assertEquals(Optional.of(1L), gateway.buscarPorEspecialidade("obstetricia", 1L));
        assertEquals(Optional.of(1L), gateway.buscarPorEspecialidade("prenatal", 1L));
    }

    @Test
    @DisplayName("findById retorna empty quando id null")
    void findByIdNull() {
        assertTrue(gateway.findById(null).isEmpty());
    }

    @Test
    @DisplayName("findById retorna DoctorInfo quando id existe")
    void findByIdEncontrado() {
        Optional<DoctorInfo> info = gateway.findById(1L);
        assertTrue(info.isPresent());
        assertEquals("Dr. João", info.get().name());
        assertEquals("Obstetrícia", info.get().specialty());

        info = gateway.findById(2L);
        assertTrue(info.isPresent());
        assertEquals("Dr. Silva", info.get().name());
    }

    @Test
    @DisplayName("findById retorna empty quando id não existe")
    void findByIdNaoEncontrado() {
        assertTrue(gateway.findById(999L).isEmpty());
    }
}
