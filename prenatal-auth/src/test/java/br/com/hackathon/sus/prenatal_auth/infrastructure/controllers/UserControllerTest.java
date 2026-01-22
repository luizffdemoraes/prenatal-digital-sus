package br.com.hackathon.sus.prenatal_auth.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_auth.application.dtos.requests.PasswordRequest;
import br.com.hackathon.sus.prenatal_auth.application.dtos.requests.UserRequest;
import br.com.hackathon.sus.prenatal_auth.application.dtos.responses.UserResponse;
import br.com.hackathon.sus.prenatal_auth.application.usecases.CreateUserUseCase;
import br.com.hackathon.sus.prenatal_auth.application.usecases.FindUserByIdUseCase;
import br.com.hackathon.sus.prenatal_auth.application.usecases.UpdatePasswordUseCase;
import br.com.hackathon.sus.prenatal_auth.application.usecases.UpdateUserUseCase;
import br.com.hackathon.sus.prenatal_auth.domain.entities.User;
import br.com.hackathon.sus.prenatal_auth.factories.TestDataFactory;
import br.com.hackathon.sus.prenatal_auth.infrastructure.config.mapper.UserMapper;
import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.sql.init.mode=always",
    "spring.sql.init.schema-locations=classpath:schema-h2.sql",
    // opcional: banco isolado se algum bean for usar DataSource
    "spring.datasource.url=jdbc:h2:mem:controllerTestDb;MODE=PostgreSQL",
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.jpa.properties.hibernate.default_schema=auth",
    "spring.main.allow-bean-definition-overriding=true",
    "spring.h2.console.enabled=true"
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @MockitoBean
    private FindUserByIdUseCase findUserByIdUseCase;

    @MockitoBean
    private UpdateUserUseCase updateUserUseCase;

    @MockitoBean
    private UpdatePasswordUseCase updatePasswordUseCase;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateUserSuccess() throws Exception {
        // usa TestDataFactory do projeto para criar request (mesmo padrão do exemplo)
        UserRequest createRequest = TestDataFactory.createUserRequest();
        User userDomain = UserMapper.toDomain(createRequest);
        UserResponse userResponse = UserMapper.toResponse(userDomain);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(createUserUseCase.execute(any(User.class))).thenReturn(userDomain);

        mockMvc.perform(post("/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponse.id()))
                .andExpect(jsonPath("$.nome").value(userResponse.name()))
                .andExpect(jsonPath("$.email").value(userResponse.email()))
                .andExpect(jsonPath("$.login").value(userResponse.login()));
    }

    @Test
    void testCreateUserBadRequest() throws Exception {
        mockMvc.perform(post("/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testFindUserByIdSuccess() throws Exception {
        Integer userId = 1;
        User userDomain = UserMapper.toDomain(TestDataFactory.createUserRequest());

        when(findUserByIdUseCase.execute(userId)).thenReturn(userDomain);

        mockMvc.perform(get("/v1/usuarios/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDomain.getId()))
                .andExpect(jsonPath("$.nome").value(userDomain.getName()));
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        Integer userId = 1;
        UserRequest updateRequest = TestDataFactory.createUserRequest();
        User userDomain = UserMapper.toDomain(updateRequest);

        when(updateUserUseCase.execute(eq(userId), any(User.class))).thenReturn(userDomain);

        mockMvc.perform(put("/v1/usuarios/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userDomain.getId()))
                .andExpect(jsonPath("$.nome").value(userDomain.getName()));
    }

    @Test
    void testUpdatePasswordSuccess() throws Exception {
        Integer userId = 1;
        PasswordRequest passwordRequest = new PasswordRequest("novaSenha123");

        doNothing().when(updatePasswordUseCase).execute(userId, passwordRequest.password());

        mockMvc.perform(patch("/v1/usuarios/{id}/senha", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordRequest)))
                .andExpect(status().isNoContent());
    }
}
