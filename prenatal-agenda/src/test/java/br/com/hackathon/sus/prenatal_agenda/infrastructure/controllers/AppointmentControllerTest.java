package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.usecases.CancelAppointmentUseCase;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.CreateAppointmentUseCase;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.CancellationReason;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.exceptions.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentController")
class AppointmentControllerTest {

    private MockMvc mvc;

    @Mock
    private CreateAppointmentUseCase createAppointmentUseCase;
    @Mock
    private CancelAppointmentUseCase cancelAppointmentUseCase;

    @BeforeEach
    void setUp() {
        AppointmentController controller = new AppointmentController(createAppointmentUseCase, cancelAppointmentUseCase);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("POST /api/consultas/agendar")
    class Agendar {

        @Test
        @DisplayName("deve retornar 201 e URI ao agendar com sucesso")
        void deveRetornar201AoAgendar() throws Exception {
            String body = """
                    {"gestanteNome":"Maria","gestanteCpf":"12345678900","medicoNome":"Dr. João",
                    "data":"%s","horario":"09:00"}
                    """.formatted(LocalDate.now().plusDays(1));

            Appointment salva = new Appointment(10L, 20L, 1L, LocalDate.now().plusDays(1), LocalTime.of(9, 0));
            salva.setId(100L);
            salva.setDataAgendamento(LocalDateTime.now());
            when(createAppointmentUseCase.execute(any(), eq(1L))).thenReturn(salva);

            mvc.perform(post("/api/consultas/agendar")
                            .header("X-Unidade-Id", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/consultas")))
                    .andExpect(jsonPath("$.id").value(100))
                    .andExpect(jsonPath("$.status").value("AGENDADA"));

            verify(createAppointmentUseCase).execute(any(), eq(1L));
        }

        @Test
        @DisplayName("deve retornar 400 quando use case lança IllegalArgumentException")
        void deveRetornar400QuandoArgumentoInvalido() throws Exception {
            String body = """
                    {"gestanteNome":"Maria","gestanteCpf":"12345678900","crm":"CRM-X",
                    "data":"%s","horario":"09:00"}
                    """.formatted(LocalDate.now().plusDays(1));
            when(createAppointmentUseCase.execute(any(), eq(1L)))
                    .thenThrow(new IllegalArgumentException("Horário já está ocupado"));

            mvc.perform(post("/api/consultas/agendar")
                            .header("X-Unidade-Id", "1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Horário já está ocupado"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/consultas/{id}/cancelar")
    class Cancelar {

        @Test
        @DisplayName("deve retornar 204 ao cancelar com sucesso")
        void deveRetornar204AoCancelar() throws Exception {
            when(cancelAppointmentUseCase.execute(1L, CancellationReason.GESTANTE_DESISTIU)).thenReturn(null);

            mvc.perform(delete("/api/consultas/1/cancelar")
                            .param("motivo", "GESTANTE_DESISTIU"))
                    .andExpect(status().isNoContent());

            verify(cancelAppointmentUseCase).execute(1L, CancellationReason.GESTANTE_DESISTIU);
        }

        @Test
        @DisplayName("deve retornar 400 quando consulta não encontrada")
        void deveRetornar400QuandoNaoEncontrada() throws Exception {
            doThrow(new IllegalArgumentException("Consulta não encontrada"))
                    .when(cancelAppointmentUseCase).execute(999L, CancellationReason.OUTRO);

            mvc.perform(delete("/api/consultas/999/cancelar")
                            .param("motivo", "OUTRO"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Consulta não encontrada"));
        }
    }
}
