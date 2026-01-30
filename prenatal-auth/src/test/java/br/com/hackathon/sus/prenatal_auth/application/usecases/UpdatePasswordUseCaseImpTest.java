package br.com.hackathon.sus.prenatal_auth.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;

import br.com.hackathon.sus.prenatal_auth.domain.gateways.UserGateway;
import br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions.BusinessException;


class UpdatePasswordUseCaseImpTest {
   
    @InjectMocks
    private UpdatePasswordUseCaseImp updatePasswordUseCaseImp;

    @Mock
    private UserGateway userGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_shouldUpdatePassword_whenUserIsAuthorized() {
        Integer userId = 1;
        String newPassword = "novaSenha123";

        doNothing().when(userGateway).validateSelf(userId);
        doNothing().when(userGateway).updateUserPassword(userId, newPassword);

        updatePasswordUseCaseImp.execute(userId, newPassword);

        verify(userGateway).validateSelf(userId);
        verify(userGateway).updateUserPassword(userId, newPassword);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário não autorizado")
    void shouldThrowBusinessExceptionWhenUserNotAuthorized() {
        Integer userId = 1;
        String newPassword = "novaSenha123";

        doThrow(new BusinessException("Não autorizado")).when(userGateway).validateSelf(userId);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> updatePasswordUseCaseImp.execute(userId, newPassword));

        assertEquals("Não autorizado", ex.getMessage());
        verify(userGateway).validateSelf(userId);
        verify(userGateway, never()).updateUserPassword(anyInt(), anyString());
    }
}
