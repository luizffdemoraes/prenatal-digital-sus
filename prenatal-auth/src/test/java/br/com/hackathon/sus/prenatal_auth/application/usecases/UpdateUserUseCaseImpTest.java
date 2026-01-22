package br.com.hackathon.sus.prenatal_auth.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import br.com.hackathon.sus.prenatal_auth.domain.entities.User;
import br.com.hackathon.sus.prenatal_auth.domain.gateways.UserGateway;
import br.com.hackathon.sus.prenatal_auth.factories.TestDataFactory;
import br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions.BusinessException;

class UpdateUserUseCaseImpTest {
    
    @InjectMocks
    private UpdateUserUseCaseImp updateUserUseCaseImp;

    @Mock
    private UserGateway userGateway;

    private User userRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userRequest = TestDataFactory.createUser();
    }

    @Test
    void execute_shouldUpdateUser_whenAuthorized() {
        Integer userId = 1;

        doNothing().when(userGateway).validateSelf(userId);
        when(userGateway.updateUser(eq(userId), any(User.class))).thenReturn(userRequest);

        User response = updateUserUseCaseImp.execute(userId, userRequest);

        assertNotNull(response);
        assertEquals(userRequest.getName(), response.getName());
        verify(userGateway).validateSelf(userId);
        verify(userGateway).updateUser(eq(userId), any(User.class));
    }

    @Test
    void execute_shouldThrowBusinessException_whenNotAuthorized() {
        Integer userId = 1;
        doThrow(new BusinessException("Não autorizado")).when(userGateway).validateSelf(userId);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                updateUserUseCaseImp.execute(userId, userRequest)
        );
        assertEquals("Não autorizado", exception.getMessage());

        verify(userGateway).validateSelf(userId);
        verify(userGateway, never()).updateUser(anyInt(), any(User.class));
    }
}
