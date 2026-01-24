package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.usecases.ListAvailabilityUseCase;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.exceptions.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AvailabilityController")
class AvailabilityControllerTest {

    private MockMvc mvc;

    @Mock
    private ListAvailabilityUseCase listAvailabilityUseCase;

    @BeforeEach
    void setUp() {
        AvailabilityController controller = new AvailabilityController(listAvailabilityUseCase);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("GET /api/disponibilidade")
    class Consultar {

        @Test
        @DisplayName("deve retornar 200 com horários disponíveis")
        void deveRetornar200ComHorarios() throws Exception {
            LocalDate data = LocalDate.now().plusDays(1);
            List<LocalTime> horarios = List.of(LocalTime.of(8, 0), LocalTime.of(8, 30), LocalTime.of(9, 0));
            when(listAvailabilityUseCase.execute(1L, data)).thenReturn(horarios);

            mvc.perform(get("/api/disponibilidade")
                            .param("medicoId", "1")
                            .param("data", data.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.medicoId").value(1))
                    .andExpect(jsonPath("$.data").value(data.toString()))
                    .andExpect(jsonPath("$.horariosDisponiveis").isArray())
                    .andExpect(jsonPath("$.horariosDisponiveis.length()").value(3));
        }

        @Test
        @DisplayName("deve retornar 400 quando use case lança exceção")
        void deveRetornar400QuandoAgendaNaoEncontrada() throws Exception {
            LocalDate data = LocalDate.now().plusDays(1);
            when(listAvailabilityUseCase.execute(999L, data))
                    .thenThrow(new IllegalArgumentException("Agenda não encontrada para o médico informado"));

            mvc.perform(get("/api/disponibilidade")
                            .param("medicoId", "999")
                            .param("data", data.toString()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Agenda não encontrada para o médico informado"));
        }
    }
}
