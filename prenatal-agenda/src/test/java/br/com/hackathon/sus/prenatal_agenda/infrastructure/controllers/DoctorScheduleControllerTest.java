package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.usecases.CreateDoctorScheduleUseCase;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.DeleteDoctorScheduleUseCase;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.FindDoctorScheduleUseCase;
import br.com.hackathon.sus.prenatal_agenda.application.usecases.UpdateDoctorScheduleUseCase;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Weekday;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorGateway;
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

import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorScheduleController")
class DoctorScheduleControllerTest {

    private MockMvc mvc;

    @Mock
    private CreateDoctorScheduleUseCase createDoctorScheduleUseCase;
    @Mock
    private UpdateDoctorScheduleUseCase updateDoctorScheduleUseCase;
    @Mock
    private DeleteDoctorScheduleUseCase deleteDoctorScheduleUseCase;
    @Mock
    private FindDoctorScheduleUseCase findDoctorScheduleUseCase;
    @Mock
    private DoctorGateway doctorGateway;

    @BeforeEach
    void setUp() {
        DoctorScheduleController controller = new DoctorScheduleController(
                createDoctorScheduleUseCase, updateDoctorScheduleUseCase, deleteDoctorScheduleUseCase,
                findDoctorScheduleUseCase, doctorGateway);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private static final Set<Weekday> DIAS = Set.of(Weekday.SEGUNDA, Weekday.QUARTA);
    private static final String CRIAR_BODY = """
            {"crm":"CRM-SP 123","medicoNome":"Dr. João","especialidade":"Obstetrícia","unidadeId":1,
            "diasAtendimento":["SEGUNDA","QUARTA"],"horarioInicio":"08:00","horarioFim":"12:00","duracaoConsultaMinutos":30}
            """;
    private static final String ATUALIZAR_BODY = """
            {"unidadeId":1,"diasAtendimento":["SEGUNDA","TERCA","QUARTA"],"horarioInicio":"07:30","horarioFim":"11:30","duracaoConsultaMinutos":20}
            """;

    @Nested
    @DisplayName("POST /api/agendas/medico")
    class Criar {

        @Test
        @DisplayName("Deve retornar 201 e Location ao criar agenda")
        void deveRetornar201AoCriar() throws Exception {
            DoctorSchedule agenda = new DoctorSchedule(50L, 10L, 1L, DIAS, LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
            when(createDoctorScheduleUseCase.execute(any())).thenReturn(agenda);

            mvc.perform(post("/api/agendas/medico")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CRIAR_BODY))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/agendas/medico/")))
                    .andExpect(jsonPath("$.id").value(50))
                    .andExpect(jsonPath("$.medicoId").value(10))
                    .andExpect(jsonPath("$.duracaoConsultaMinutos").value(30));

            verify(createDoctorScheduleUseCase).execute(any());
        }
    }

    @Nested
    @DisplayName("GET /api/agendas/medico/{crm}")
    class Buscar {

        @Test
        @DisplayName("Deve retornar 200 e agenda quando encontrada")
        void shouldReturn200WhenFound() throws Exception {
            when(doctorGateway.buscarPorCrm("CRM-X")).thenReturn(Optional.of(10L));
            DoctorSchedule agenda = new DoctorSchedule(50L, 10L, 1L, DIAS, LocalTime.of(8, 0), LocalTime.of(12, 0), 30);
            when(findDoctorScheduleUseCase.execute(10L)).thenReturn(Optional.of(agenda));

            mvc.perform(get("/api/agendas/medico/CRM-X"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(50))
                    .andExpect(jsonPath("$.medicoId").value(10));
        }

        @Test
        @DisplayName("Deve retornar 404 quando médico não encontrado")
        void deveRetornar404QuandoMedicoNaoEncontrado() throws Exception {
            when(doctorGateway.buscarPorCrm("CRM-INVALIDO")).thenReturn(Optional.empty());

            mvc.perform(get("/api/agendas/medico/CRM-INVALIDO"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 404 quando agenda não encontrada para o médico")
        void shouldReturn404WhenScheduleNotFound() throws Exception {
            when(doctorGateway.buscarPorCrm("CRM-X")).thenReturn(Optional.of(10L));
            when(findDoctorScheduleUseCase.execute(10L)).thenReturn(Optional.empty());

            mvc.perform(get("/api/agendas/medico/CRM-X"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/agendas/medico/{crm}")
    class Atualizar {

        @Test
        @DisplayName("Deve retornar 200 ao atualizar")
        void deveRetornar200AoAtualizar() throws Exception {
            DoctorSchedule atualizada = new DoctorSchedule(50L, 10L, 1L, Set.of(Weekday.SEGUNDA, Weekday.TERCA, Weekday.QUARTA), LocalTime.of(7, 30), LocalTime.of(11, 30), 20);
            when(updateDoctorScheduleUseCase.execute(eq("CRM-X"), any())).thenReturn(atualizada);

            mvc.perform(put("/api/agendas/medico/CRM-X")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(ATUALIZAR_BODY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.duracaoConsultaMinutos").value(20));

            verify(updateDoctorScheduleUseCase).execute(eq("CRM-X"), any());
        }
    }

    @Nested
    @DisplayName("DELETE /api/agendas/medico/{crm}")
    class Excluir {

        @Test
        @DisplayName("Deve retornar 204 ao excluir")
        void shouldReturn204WhenDeleting() throws Exception {
            doNothing().when(deleteDoctorScheduleUseCase).execute("CRM-X");

            mvc.perform(delete("/api/agendas/medico/CRM-X"))
                    .andExpect(status().isNoContent());

            verify(deleteDoctorScheduleUseCase).execute("CRM-X");
        }
    }
}
