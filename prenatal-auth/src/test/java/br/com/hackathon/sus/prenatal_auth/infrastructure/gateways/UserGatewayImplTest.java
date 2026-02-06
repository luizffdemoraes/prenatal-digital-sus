package br.com.hackathon.sus.prenatal_auth.infrastructure.gateways;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import br.com.hackathon.sus.prenatal_auth.domain.entities.User;
import br.com.hackathon.sus.prenatal_auth.factories.TestDataFactory;
import br.com.hackathon.sus.prenatal_auth.infrastructure.config.mapper.UserMapper;
import br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions.BusinessException;
import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.entity.RoleEntity;
import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.entity.UserEntity;
import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.repository.RoleRepository;
import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.repository.UserRepository;

class UserGatewayImplTest {

    @InjectMocks
    private UserGatewayImpl userGateway;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User domainUser;
    private UserEntity userEntity;
    private RoleEntity rolePatientEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // User de domínio via Factory
        domainUser = TestDataFactory.createUser(); // id=1, role ROLE_PATIENT (id=1)
        userEntity = UserMapper.fromDomain(domainUser);

        rolePatientEntity = new RoleEntity(1, "ROLE_PATIENT");

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
        when(roleRepository.getReferenceById(1)).thenReturn(rolePatientEntity);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthenticated(UserEntity entity) {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("username")).thenReturn(entity.getEmail());

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(jwt);

        SecurityContext sc = mock(SecurityContext.class);
        when(sc.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(sc);
        when(userRepository.findByEmail(entity.getEmail())).thenReturn(Optional.of(entity));
    }

    @Test
    @DisplayName("Deve persistir usuário com senha codificada e associar role gerenciada")
    void shouldPersistUserWithEncodedPasswordAndManagedRole() {
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity e = inv.getArgument(0);
            e.setId(domainUser.getId());
            return e;
        });

        User saved = userGateway.saveUser(domainUser);

        assertNotNull(saved);
        assertEquals(domainUser.getId(), saved.getId());
        assertEquals(domainUser.getName(), saved.getName());
        assertEquals(domainUser.getRoles().size(), saved.getRoles().size());
        verify(passwordEncoder).encode(domainUser.getPassword());
        verify(roleRepository).getReferenceById(1);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void existsUserByEmail_deveRetornarVerdadeiro() {
        when(userRepository.existsByEmail(domainUser.getEmail())).thenReturn(true);
        assertTrue(userGateway.existsUserByEmail(domainUser.getEmail()));
        verify(userRepository).existsByEmail(domainUser.getEmail());
    }

    @Test
    @DisplayName("Deve retornar usuário quando encontrado por ID")
    void shouldReturnUserWhenFoundById() {
        when(userRepository.findById(domainUser.getId())).thenReturn(Optional.of(userEntity));
        User u = userGateway.findUserById(domainUser.getId());
        assertEquals(domainUser.getId(), u.getId());
    }

    @Test
    void findUserById_deveLancarExcecaoQuandoNaoEncontrado() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> userGateway.findUserById(999));
    }

    @Test
    @DisplayName("Deve negar atualização quando é outro usuário")
    void shouldDenyUpdateWhenOtherUser() {
        User otherDomain = TestDataFactory.createUser();
        otherDomain.setId(50);
        otherDomain.setEmail("outro@teste.com");
        UserEntity otherEntity = UserMapper.fromDomain(otherDomain);
        mockAuthenticated(otherEntity);

        when(userRepository.findById(domainUser.getId())).thenReturn(Optional.of(userEntity));

        assertThrows(BusinessException.class,
                () -> userGateway.updateUser(domainUser.getId(), domainUser));
    }

    @Test
    void updateUserPassword_deveAtualizarSenha() {
        when(userRepository.findById(domainUser.getId())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(passwordEncoder.encode("novaSenha")).thenReturn("encodedNova");

        userGateway.updateUserPassword(domainUser.getId(), "novaSenha");

        verify(passwordEncoder).encode("novaSenha");
        verify(userRepository).save(argThat(e -> "encodedNova".equals(e.getPassword())));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe na atualização de senha")
    void shouldThrowExceptionWhenUserDoesNotExistOnPasswordUpdate() {
        when(userRepository.findById(123)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class,
                () -> userGateway.updateUserPassword(123, "x"));
    }

    @Test
    void authenticated_deveRetornarUsuario() {
        mockAuthenticated(userEntity);
        User u = userGateway.authenticated();
        assertEquals(userEntity.getEmail(), u.getEmail());
        verify(userRepository).findByEmail(userEntity.getEmail());
    }

    @Test
    @DisplayName("Deve lançar exceção quando autenticação é inválida")
    void shouldThrowExceptionWhenAuthenticationIsInvalid() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("username")).thenReturn("nao@existe.com");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(jwt);

        SecurityContext sc = mock(SecurityContext.class);
        when(sc.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(sc);

        when(userRepository.findByEmail("nao@existe.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userGateway.authenticated());
    }

    @Test
    void findUserOrThrow_deveRetornar() {
        when(userRepository.findById(domainUser.getId())).thenReturn(Optional.of(userEntity));
        User u = userGateway.findUserOrThrow(domainUser.getId());
        assertEquals(domainUser.getId(), u.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção em findUserOrThrow quando usuário não existe")
    void shouldThrowExceptionInFindUserOrThrowWhenUserDoesNotExist() {
        when(userRepository.findById(777)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> userGateway.findUserOrThrow(777));
    }

    @Test
    void validateSelf_devePassarQuandoMesmoUsuario() {
        mockAuthenticated(userEntity);
        assertDoesNotThrow(() -> userGateway.validateSelf(domainUser.getId()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando é outro usuário na validação")
    void shouldThrowExceptionWhenOtherUserOnValidation() {
        mockAuthenticated(userEntity);
        assertThrows(BusinessException.class, () -> userGateway.validateSelf(999));
    }

    @Test
    @DisplayName("existsByCpf deve retornar false quando CPF é null")
    void existsByCpf_shouldReturnFalseWhenCpfIsNull() {
        assertFalse(userGateway.existsByCpf(null));
    }

    @Test
    @DisplayName("existsByCpf deve retornar true quando CPF existe")
    void existsByCpf_shouldReturnTrueWhenCpfExists() {
        when(userRepository.existsByCpf("12345678901")).thenReturn(true);
        assertTrue(userGateway.existsByCpf("12345678901"));
        verify(userRepository).existsByCpf("12345678901");
    }

    @Test
    @DisplayName("existsByCpfExcludingId deve retornar false quando CPF é null")
    void existsByCpfExcludingId_shouldReturnFalseWhenCpfIsNull() {
        assertFalse(userGateway.existsByCpfExcludingId(null, 1));
    }

    @Test
    @DisplayName("existsByCpfExcludingId deve retornar true quando outro usuário tem o CPF")
    void existsByCpfExcludingId_shouldReturnTrueWhenOtherUserHasCpf() {
        when(userRepository.existsByCpfAndIdNot("12345678901", 1)).thenReturn(true);
        assertTrue(userGateway.existsByCpfExcludingId("12345678901", 1));
        verify(userRepository).existsByCpfAndIdNot("12345678901", 1);
    }

    @Test
    @DisplayName("findUserByCpf deve retornar Optional vazio quando CPF é null")
    void findUserByCpf_shouldReturnEmptyWhenCpfIsNull() {
        assertEquals(Optional.empty(), userGateway.findUserByCpf(null));
    }

    @Test
    @DisplayName("findUserByCpf deve retornar Optional vazio quando CPF é em branco")
    void findUserByCpf_shouldReturnEmptyWhenCpfIsBlank() {
        assertEquals(Optional.empty(), userGateway.findUserByCpf("   "));
    }

    @Test
    @DisplayName("findUserByCpf deve retornar Optional vazio quando CPF tem menos de 11 dígitos")
    void findUserByCpf_shouldReturnEmptyWhenCpfLengthNot11() {
        assertEquals(Optional.empty(), userGateway.findUserByCpf("123"));
    }

    @Test
    @DisplayName("findUserByCpf deve retornar usuário quando CPF existe")
    void findUserByCpf_shouldReturnUserWhenCpfExists() {
        when(userRepository.findByCpf("12345678901")).thenReturn(Optional.of(userEntity));
        Optional<User> result = userGateway.findUserByCpf("123.456.789-01");
        assertTrue(result.isPresent());
        assertEquals(domainUser.getId(), result.get().getId());
        verify(userRepository).findByCpf("12345678901");
    }

    @Test
    @DisplayName("findUserByCpf deve retornar Optional vazio quando CPF não existe")
    void findUserByCpf_shouldReturnEmptyWhenCpfNotFound() {
        when(userRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), userGateway.findUserByCpf("12345678901"));
    }

    @Test
    @DisplayName("authenticated deve lançar quando principal não é Jwt")
    void authenticated_shouldThrowWhenPrincipalIsNotJwt() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("not-a-jwt");
        SecurityContext sc = mock(SecurityContext.class);
        when(sc.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(sc);
        assertThrows(UsernameNotFoundException.class, () -> userGateway.authenticated());
    }

    @Test
    @DisplayName("updateUser deve manter senha quando nova senha é null")
    void updateUser_shouldKeepPasswordWhenNewPasswordIsNull() {
        mockAuthenticated(userEntity);
        when(userRepository.findById(domainUser.getId())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        User updateRequest = TestDataFactory.createUser();
        updateRequest.setPassword(null);
        updateRequest.setCpf(domainUser.getCpf());
        when(userRepository.existsByCpfAndIdNot(anyString(), anyInt())).thenReturn(false);
        User saved = userGateway.updateUser(domainUser.getId(), updateRequest);
        assertNotNull(saved);
        verify(userRepository).save(argThat(e -> e.getPassword() != null));
    }

    @Test
    @DisplayName("updateUser deve lançar quando CPF alterado já existe para outro usuário")
    void updateUser_shouldThrowWhenNewCpfExistsForOtherUser() {
        mockAuthenticated(userEntity);
        when(userRepository.findById(domainUser.getId())).thenReturn(Optional.of(userEntity));
        when(userRepository.existsByCpfAndIdNot("99999999999", domainUser.getId())).thenReturn(true);
        User updateRequest = TestDataFactory.createUser();
        updateRequest.setCpf("99999999999");
        updateRequest.setPassword("");
        assertThrows(BusinessException.class, () -> userGateway.updateUser(domainUser.getId(), updateRequest));
    }

    @Test
    @DisplayName("updateUser deve codificar nova senha quando não vazia")
    void updateUser_shouldEncodeNewPasswordWhenNotEmpty() {
        mockAuthenticated(userEntity);
        when(userRepository.findById(domainUser.getId())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> {
            UserEntity e = inv.getArgument(0);
            e.setId(domainUser.getId());
            return e;
        });
        when(userRepository.existsByCpfAndIdNot(anyString(), anyInt())).thenReturn(false);
        User updateRequest = TestDataFactory.createUser();
        updateRequest.setPassword("novaSenha123");
        updateRequest.setCpf(domainUser.getCpf());
        userGateway.updateUser(domainUser.getId(), updateRequest);
        verify(passwordEncoder).encode("novaSenha123");
    }
}
