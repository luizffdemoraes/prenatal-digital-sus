package br.com.hackathon.sus.prenatal_auth.application.usecases;

import br.com.hackathon.sus.prenatal_auth.domain.entities.User;
import br.com.hackathon.sus.prenatal_auth.domain.gateways.UserGateway;
import br.com.hackathon.sus.prenatal_auth.factories.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FindUserByCpfUseCaseImpTest {

    @InjectMocks
    private FindUserByCpfUseCaseImp findUserByCpfUseCaseImp;

    @Mock
    private UserGateway userGateway;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = TestDataFactory.createUser();
    }

    @Test
    @DisplayName("Deve retornar usuário quando encontrado por CPF")
    void execute_shouldReturnUser_whenUserFound() {
        when(userGateway.findUserByCpf("12345678901")).thenReturn(Optional.of(user));

        Optional<User> result = findUserByCpfUseCaseImp.execute("12345678901");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("12345678901", result.get().getCpf());
        assertEquals("Test User", result.get().getName());
        verify(userGateway).findUserByCpf("12345678901");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando usuário não encontrado")
    void execute_shouldReturnEmpty_whenUserNotFound() {
        when(userGateway.findUserByCpf("99999999999")).thenReturn(Optional.empty());

        Optional<User> result = findUserByCpfUseCaseImp.execute("99999999999");

        assertTrue(result.isEmpty());
        verify(userGateway).findUserByCpf("99999999999");
    }
}
