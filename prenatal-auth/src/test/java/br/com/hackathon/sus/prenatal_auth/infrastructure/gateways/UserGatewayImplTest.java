package br.com.hackathon.sus.prenatal_auth.infrastructure.gateways;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
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
}
