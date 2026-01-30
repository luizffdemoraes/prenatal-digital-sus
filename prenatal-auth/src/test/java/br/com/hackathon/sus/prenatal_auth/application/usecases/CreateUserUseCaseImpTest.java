package br.com.hackathon.sus.prenatal_auth.application.usecases;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import br.com.hackathon.sus.prenatal_auth.domain.entities.Role;
import br.com.hackathon.sus.prenatal_auth.domain.entities.User;
import br.com.hackathon.sus.prenatal_auth.domain.gateways.RoleGateway;
import br.com.hackathon.sus.prenatal_auth.domain.gateways.UserGateway;
import br.com.hackathon.sus.prenatal_auth.factories.TestDataFactory;
import br.com.hackathon.sus.prenatal_auth.infrastructure.config.mapper.UserMapper;
import br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions.BusinessException;

class CreateUserUseCaseImpTest {

    @InjectMocks
    private CreateUserUseCaseImp createUserUseCaseImp;

    @Mock
    private UserGateway userGateway;

    @Mock
    private RoleGateway roleGateway;

    private User userRequestAdmin;
    private User userRequestClient;
    private Role roleAdmin;
    private Role roleClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // cria duas instâncias separadas a partir do factory (não usa construtor de cópia)
        userRequestAdmin = UserMapper.toDomain(TestDataFactory.createUserRequest());
        userRequestAdmin.setName("Admin");
        userRequestAdmin.setEmail("admin@restaurantsync.com");
        userRequestAdmin.setLogin("adminUser");
        userRequestAdmin.setPassword("adminPass");
        userRequestAdmin.getRoles().clear();
        userRequestAdmin.addRole(new Role(null, "ROLE_DOCTOR"));

        userRequestClient = UserMapper.toDomain(TestDataFactory.createUserRequest());
        userRequestClient.setName("Client");
        userRequestClient.setEmail("client@example.com");
        userRequestClient.setLogin("clientUser");
        userRequestClient.setPassword("clientPass");
        userRequestClient.getRoles().clear();
        userRequestClient.addRole(new Role(null, "ROLE_PATIENT"));

        roleAdmin = new Role(1, "ROLE_DOCTOR");
        roleClient = new Role(2, "ROLE_NURSE");
    }

    @Test
    void execute_shouldCreateAdminUser_whenEmailEndsWithRestaurantsync() {
        when(userGateway.existsUserByEmail(userRequestAdmin.getEmail())).thenReturn(false);
        when(userGateway.existsByCpf(userRequestAdmin.getCpf())).thenReturn(false);
        when(roleGateway.findByAuthority("ROLE_DOCTOR")).thenReturn(Optional.of(roleAdmin));

        when(userGateway.saveUser(ArgumentMatchers.any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1);
            return user;
        });

        User response = createUserUseCaseImp.execute(userRequestAdmin);

        assertNotNull(response);
        assertEquals(1, response.getId());
        verify(userGateway).existsUserByEmail(userRequestAdmin.getEmail());
        verify(roleGateway).findByAuthority("ROLE_DOCTOR");
        verify(userGateway).saveUser(ArgumentMatchers.any(User.class));
    }

    @Test
    @DisplayName("Deve criar usuário cliente quando e-mail não termina com restaurantsync")
    void shouldCreateClientUserWhenEmailDoesNotEndWithRestaurantsync() {
        when(userGateway.existsUserByEmail(userRequestClient.getEmail())).thenReturn(false);
        when(userGateway.existsByCpf(userRequestClient.getCpf())).thenReturn(false);
        when(roleGateway.findByAuthority("ROLE_PATIENT")).thenReturn(Optional.of(roleClient));

        when(userGateway.saveUser(ArgumentMatchers.any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2);
            return user;
        });

        User response = createUserUseCaseImp.execute(userRequestClient);

        assertNotNull(response);
        assertEquals(2, response.getId());
        verify(userGateway).existsUserByEmail(userRequestClient.getEmail());
        verify(roleGateway).findByAuthority("ROLE_PATIENT");
        verify(userGateway).saveUser(ArgumentMatchers.any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando e-mail já cadastrado")
    void shouldThrowExceptionWhenEmailAlreadyRegistered() {
        when(userGateway.existsUserByEmail(userRequestAdmin.getEmail())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> createUserUseCaseImp.execute(userRequestAdmin));
        assertEquals("user.email.exists", exception.getMessage());
        assertEquals("user.email.exists", exception.getMessageKey());
    }

    @Test
    void execute_shouldThrowException_whenCpfAlreadyRegistered() {
        when(userGateway.existsUserByEmail(userRequestAdmin.getEmail())).thenReturn(false);
        when(userGateway.existsByCpf(userRequestAdmin.getCpf())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> createUserUseCaseImp.execute(userRequestAdmin));
        assertEquals("user.cpf.exists", exception.getMessage());
        assertEquals("user.cpf.exists", exception.getMessageKey());
    }

    @Test
    @DisplayName("Deve lançar exceção quando role não encontrada")
    void shouldThrowExceptionWhenRoleNotFound() {
        when(userGateway.existsUserByEmail(userRequestAdmin.getEmail())).thenReturn(false);
        when(userGateway.existsByCpf(userRequestAdmin.getCpf())).thenReturn(false);
        when(roleGateway.findByAuthority("ROLE_DOCTOR")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> createUserUseCaseImp.execute(userRequestAdmin));
        assertEquals("error.role.invalid: ROLE_DOCTOR", exception.getMessage());
    }
}
