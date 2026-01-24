package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.usecases.ListAvailabilityUseCase;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorInfo;
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
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AvailabilityController")
class AvailabilityControllerTest {

    private MockMvc mvc;

    @Mock
    private ListAvailabilityUseCase listAvailabilityUseCase;

    @Mock
    private DoctorGateway doctorGateway;

    @BeforeEach
    void setUp() {
        AvailabilityController controller = new AvailabilityController(listAvailabilityUseCase, doctorGateway);
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
            when(doctorGateway.buscarPorCrm("12345")).thenReturn(Optional.of(1L));
            when(doctorGateway.findById(1L)).thenReturn(Optional.of(new DoctorInfo("Dr. João", "Obstetrícia")));
            when(listAvailabilityUseCase.execute(1L, data)).thenReturn(horarios);

            mvc.perform(get("/api/disponibilidade")
                            .param("crm", "12345")
                            .param("data", data.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.medicoNome").value("Dr. João"))
                    .andExpect(jsonPath("$.especialidade").value("Obstetrícia"))
                    .andExpect(jsonPath("$.data").value(data.toString()))
                    .andExpect(jsonPath("$.horariosDisponiveis").isArray())
                    .andExpect(jsonPath("$.horariosDisponiveis.length()").value(3));
        }

        @Test
        @DisplayName("deve retornar 400 quando use case lança exceção (agenda não encontrada)")
        void deveRetornar400QuandoAgendaNaoEncontrada() throws Exception {
            LocalDate data = LocalDate.now().plusDays(1);
            when(doctorGateway.buscarPorCrm("999")).thenReturn(Optional.of(999L));
            when(listAvailabilityUseCase.execute(999L, data))
                    .thenThrow(new IllegalArgumentException("Agenda não encontrada para o médico informado"));

            mvc.perform(get("/api/disponibilidade")
                            .param("crm", "999")
                            .param("data", data.toString()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Agenda não encontrada para o médico informado"));
        }

        @Test
        @DisplayName("deve retornar 404 quando CRM não encontrado")
        void deveRetornar404QuandoCrmNaoEncontrado() throws Exception {
            LocalDate data = LocalDate.now().plusDays(1);
            when(doctorGateway.buscarPorCrm("CRM-INVALIDO")).thenReturn(Optional.empty());

            mvc.perform(get("/api/disponibilidade")
                            .param("crm", "CRM-INVALIDO")
                            .param("data", data.toString()))
                    .andExpect(status().isNotFound());
        }
    }
}
