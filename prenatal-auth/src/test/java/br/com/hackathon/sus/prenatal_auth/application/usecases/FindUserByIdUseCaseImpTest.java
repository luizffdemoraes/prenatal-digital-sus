package br.com.hackathon.sus.prenatal_auth.application.usecases;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import br.com.hackathon.sus.prenatal_auth.domain.entities.User;
import br.com.hackathon.sus.prenatal_auth.domain.gateways.UserGateway;
import br.com.hackathon.sus.prenatal_auth.factories.TestDataFactory;
import br.com.hackathon.sus.prenatal_auth.infrastructure.config.mapper.UserMapper;
import br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions.BusinessException;


class FindUserByIdUseCaseImpTest {

    @InjectMocks
    private FindUserByIdUseCaseImp findUserByIdUseCaseImp;

    @Mock
    private UserGateway userGateway;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = UserMapper.toDomain(TestDataFactory.createUserRequest());
        user.setId(1);
    }

    @Test
    void execute_shouldReturnUser_whenUserFoundAndAuthorized() {

        when(userGateway.findUserById(1)).thenReturn(user);
        doNothing().when(userGateway).validateSelf(user.getId());

        User response = findUserByIdUseCaseImp.execute(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("R. Maria Marciana Dos Santos, Zona Rural Capão", response.getAddress().getStreet());
        verify(userGateway).findUserById(1);
        verify(userGateway).validateSelf(user.getId());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando não autorizado")
    void shouldThrowBusinessExceptionWhenNotAuthorized() {
        when(userGateway.findUserById(1)).thenReturn(user);
        doThrow(new BusinessException("Não autorizado")).when(userGateway).validateSelf(user.getId());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                findUserByIdUseCaseImp.execute(1)
        );
        assertEquals("Não autorizado", exception.getMessage());
        verify(userGateway).findUserById(1);
        verify(userGateway).validateSelf(user.getId());
    }
}
