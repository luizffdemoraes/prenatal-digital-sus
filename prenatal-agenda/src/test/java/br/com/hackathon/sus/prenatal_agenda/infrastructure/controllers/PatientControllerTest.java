package br.com.hackathon.sus.prenatal_agenda.infrastructure.controllers;

import br.com.hackathon.sus.prenatal_agenda.application.usecases.FindAppointmentsByPatientUseCase;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorInfo;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.PatientGateway;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientController")
class PatientControllerTest {

    private MockMvc mvc;

    @Mock
    private FindAppointmentsByPatientUseCase findAppointmentsByPatientUseCase;
    @Mock
    private PatientGateway patientGateway;
    @Mock
    private DoctorGateway doctorGateway;

    @BeforeEach
    void setUp() {
        PatientController controller = new PatientController(findAppointmentsByPatientUseCase, patientGateway, doctorGateway);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("GET /api/gestantes/consultas?cpf=")
    class BuscarPorCpf {

        @Test
        @DisplayName("Deve retornar 200 com lista de consultas quando CPF encontrado")
        void deveRetornar200QuandoCpfEncontrado() throws Exception {
            when(patientGateway.buscarPorCpf("12345678900")).thenReturn(Optional.of(10L));
            Appointment c = new Appointment(10L, 20L, 1L, LocalDate.now().plusDays(1), LocalTime.of(9, 0));
            c.setId(100L);
            c.setDataAgendamento(LocalDateTime.now());
            when(findAppointmentsByPatientUseCase.execute(10L)).thenReturn(List.of(c));
            when(patientGateway.findNameById(10L)).thenReturn(Optional.of("Maria"));
            when(doctorGateway.findById(20L)).thenReturn(Optional.of(new DoctorInfo("Dr. João", "Obstetrícia")));

            mvc.perform(get("/api/gestantes/consultas").param("cpf", "12345678900"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(100))
                    .andExpect(jsonPath("$[0].gestanteNome").value("Maria"))
                    .andExpect(jsonPath("$[0].medicoNome").value("Dr. João"))
                    .andExpect(jsonPath("$[0].especialidade").value("Obstetrícia"));
        }

        @Test
        @DisplayName("Deve retornar 404 quando CPF não encontrado")
        void shouldReturn404WhenCpfNotFound() throws Exception {
            when(patientGateway.buscarPorCpf("00000000000")).thenReturn(Optional.empty());

            mvc.perform(get("/api/gestantes/consultas").param("cpf", "00000000000"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/gestantes/{gestanteId}/consultas")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar 200 com lista de consultas")
        void deveRetornar200ComConsultas() throws Exception {
            Appointment c = new Appointment(5L, 20L, 1L, LocalDate.now().plusDays(1), LocalTime.of(10, 0));
            c.setId(50L);
            c.setDataAgendamento(LocalDateTime.now());
            when(findAppointmentsByPatientUseCase.execute(5L)).thenReturn(List.of(c));
            when(patientGateway.findNameById(5L)).thenReturn(Optional.of("Joana"));
            when(doctorGateway.findById(20L)).thenReturn(Optional.of(new DoctorInfo("Dr. João", "Obstetrícia")));

            mvc.perform(get("/api/gestantes/5/consultas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(50))
                    .andExpect(jsonPath("$[0].gestanteNome").value("Joana"))
                    .andExpect(jsonPath("$[0].medicoNome").value("Dr. João"))
                    .andExpect(jsonPath("$[0].especialidade").value("Obstetrícia"));
        }

        @Test
        @DisplayName("Deve retornar 200 com lista vazia quando sem consultas")
        void shouldReturn200WithEmptyList() throws Exception {
            when(findAppointmentsByPatientUseCase.execute(5L)).thenReturn(List.of());

            mvc.perform(get("/api/gestantes/5/consultas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }
}
