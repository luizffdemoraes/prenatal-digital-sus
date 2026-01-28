package com.hackathon.sus.prenatal_prontuario.infrastructure.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hackathon.sus.prenatal_prontuario.application.dtos.requests.CreateMedicalRecordRequest;
import com.hackathon.sus.prenatal_prontuario.application.dtos.requests.UpdateMedicalRecordRequest;
import com.hackathon.sus.prenatal_prontuario.application.dtos.requests.UpdateRiskFactorsRequest;
import com.hackathon.sus.prenatal_prontuario.application.usecases.CreateMedicalRecordUseCase;
import com.hackathon.sus.prenatal_prontuario.application.usecases.FindMedicalRecordByCpfUseCase;
import com.hackathon.sus.prenatal_prontuario.application.usecases.FindMedicalRecordHistoryUseCase;
import com.hackathon.sus.prenatal_prontuario.application.usecases.UpdateMedicalRecordUseCase;
import com.hackathon.sus.prenatal_prontuario.application.usecases.UpdateRiskFactorsUseCase;
import com.hackathon.sus.prenatal_prontuario.domain.entities.DeliveryType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecordHistory;
import com.hackathon.sus.prenatal_prontuario.domain.entities.PregnancyType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.RiskFactor;
import com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MedicalRecordController")
class MedicalRecordControllerTest {

    private MockMvc mvc;
    private ObjectMapper objectMapper;

    @Mock
    private CreateMedicalRecordUseCase createMedicalRecordUseCase;
    @Mock
    private FindMedicalRecordByCpfUseCase findMedicalRecordByCpfUseCase;
    @Mock
    private UpdateMedicalRecordUseCase updateMedicalRecordUseCase;
    @Mock
    private UpdateRiskFactorsUseCase updateRiskFactorsUseCase;
    @Mock
    private FindMedicalRecordHistoryUseCase findMedicalRecordHistoryUseCase;

    private MedicalRecord medicalRecord;
    private String cpf;

    @BeforeEach
    void setUp() {
        MedicalRecordController controller = new MedicalRecordController(
                createMedicalRecordUseCase,
                findMedicalRecordByCpfUseCase,
                updateMedicalRecordUseCase,
                updateRiskFactorsUseCase,
                findMedicalRecordHistoryUseCase
        );
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.disable(MapperFeature.REQUIRE_HANDLERS_FOR_JAVA8_TIMES);

        cpf = "12345678901";
        UUID recordId = UUID.randomUUID();

        medicalRecord = new MedicalRecord(
                recordId,
                cpf,
                "Maria Silva",
                LocalDate.of(1990, 1, 1),
                null,
                null,
                LocalDate.now().minusWeeks(20),
                20,
                PregnancyType.SINGLETON,
                1,
                1,
                0,
                false,
                null,
                List.of(RiskFactor.HYPERTENSION),
                true,
                false,
                "Observações",
                DeliveryType.NATURAL,
                null
        );
    }

    private Authentication createMockAuthentication(String subject, String role, String cpfClaim) {
        Authentication auth = mock(Authentication.class);
        Jwt jwt = mock(Jwt.class);
        lenient().when(auth.getPrincipal()).thenReturn(jwt);
        lenient().when(jwt.getSubject()).thenReturn(subject);
        if (cpfClaim != null) {
            lenient().when(jwt.getClaim("cpf")).thenReturn(cpfClaim);
        }
        List<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();
        if (role != null) {
            authorities.add(() -> role);
        }
        lenient().doReturn(authorities).when(auth).getAuthorities();
        return auth;
    }

    /** Garante SecurityContextHolder e request.getUserPrincipal() para o resolver injetar Authentication. */
    private RequestPostProcessor asPrincipal(Authentication auth) {
        return request -> {
            SecurityContextHolder.getContext().setAuthentication(auth);
            ((MockHttpServletRequest) request).setUserPrincipal(auth);
            return request;
        };
    }

    @Nested
    @DisplayName("POST /api/v1/prontuarios")
    class CriarProntuario {

        @Test
        @DisplayName("deve retornar 201 ao criar prontuário com sucesso")
        void deveRetornar201AoCriarProntuario() throws Exception {
            String lmp = LocalDate.now().minusWeeks(20).toString();
            String createBody = """
                    {
                      "cpf": "%s",
                      "nomeCompleto": "Maria Silva",
                      "dataNascimento": "1990-01-01",
                      "dataUltimaMenstruacao": "%s",
                      "tipoGestacao": "UNICA",
                      "numeroGestacoesAnteriores": 1,
                      "numeroPartos": 1,
                      "numeroAbortos": 0,
                      "gestacaoAltoRisco": false,
                      "fatoresRisco": ["HIPERTENSAO"],
                      "usoVitaminas": true,
                      "usoAAS": false,
                      "observacoes": "Observações",
                      "tipoParto": "PARTO_NATURAL"
                    }
                    """.formatted(cpf, lmp).replaceAll("\\s+", " ").trim();

            when(createMedicalRecordUseCase.execute(any(CreateMedicalRecordRequest.class), eq("user-123")))
                    .thenReturn(medicalRecord);

            Authentication auth = createMockAuthentication("user-123", null, null);

            mvc.perform(post("/api/v1/prontuarios")
                            .with(asPrincipal(auth))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(createBody))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/api/v1/prontuarios/cpf/" + cpf)))
                    .andExpect(jsonPath("$.id").value(medicalRecord.getId().toString()))
                    .andExpect(jsonPath("$.cpf").value(cpf))
                    .andExpect(jsonPath("$.nomeCompleto").value("Maria Silva"));

            verify(createMedicalRecordUseCase).execute(any(CreateMedicalRecordRequest.class), eq("user-123"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/prontuarios/cpf/{cpf}")
    class BuscarPorCpf {

        @Test
        @DisplayName("deve retornar 200 ao buscar prontuário por CPF")
        void deveRetornar200AoBuscarPorCpf() throws Exception {
            when(findMedicalRecordByCpfUseCase.execute(cpf)).thenReturn(Optional.of(medicalRecord));

            Authentication auth = createMockAuthentication("user-123", null, null);

            mvc.perform(get("/api/v1/prontuarios/cpf/{cpf}", cpf)
                            .with(asPrincipal(auth)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(medicalRecord.getId().toString()))
                    .andExpect(jsonPath("$.cpf").value(cpf));

            verify(findMedicalRecordByCpfUseCase).execute(cpf);
        }

        @Test
        @DisplayName("deve retornar 404 quando prontuário não encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            when(findMedicalRecordByCpfUseCase.execute(cpf)).thenReturn(Optional.empty());

            Authentication auth = createMockAuthentication("user-123", null, null);

            mvc.perform(get("/api/v1/prontuarios/cpf/{cpf}", cpf)
                            .with(asPrincipal(auth)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Prontuário não encontrado para o CPF informado."));

            verify(findMedicalRecordByCpfUseCase).execute(cpf);
        }

        @Test
        @DisplayName("deve permitir acesso quando gestante acessa próprio prontuário")
        void devePermitirAcessoQuandoGestanteAcessaProprioProntuario() throws Exception {
            when(findMedicalRecordByCpfUseCase.execute(cpf)).thenReturn(Optional.of(medicalRecord));

            Authentication auth = createMockAuthentication("gestante-123", "ROLE_GESTANTE", cpf);

            mvc.perform(get("/api/v1/prontuarios/cpf/{cpf}", cpf)
                            .with(asPrincipal(auth)))
                    .andExpect(status().isOk());

            verify(findMedicalRecordByCpfUseCase).execute(cpf);
        }

        @Test
        @DisplayName("deve negar acesso quando gestante tenta acessar outro prontuário")
        void deveNegarAcessoQuandoGestanteTentaAcessarOutroProntuario() throws Exception {
            String differentCpf = "98765432100";

            Authentication auth = createMockAuthentication("gestante-123", "ROLE_GESTANTE", cpf);

            mvc.perform(get("/api/v1/prontuarios/cpf/{cpf}", differentCpf)
                            .with(asPrincipal(auth)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("Gestante só pode acessar o próprio prontuário."));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/prontuarios/cpf/{cpf}")
    class AtualizarProntuario {

        @Test
        @DisplayName("deve retornar 200 ao atualizar prontuário com sucesso")
        void deveRetornar200AoAtualizarProntuario() throws Exception {
            UpdateMedicalRecordRequest request = new UpdateMedicalRecordRequest(
                    true,
                    true,
                    "Observações atualizadas",
                    DeliveryType.CESAREAN
            );

            MedicalRecord updatedRecord = new MedicalRecord(
                    medicalRecord.getId(),
                    cpf,
                    medicalRecord.getFullName(),
                    medicalRecord.getDateOfBirth(),
                    null,
                    null,
                    medicalRecord.getLastMenstrualPeriod(),
                    medicalRecord.getGestationalAgeWeeks(),
                    medicalRecord.getPregnancyType(),
                    medicalRecord.getPreviousPregnancies(),
                    medicalRecord.getPreviousDeliveries(),
                    medicalRecord.getPreviousAbortions(),
                    medicalRecord.getHighRiskPregnancy(),
                    medicalRecord.getHighRiskReason(),
                    medicalRecord.getRiskFactors(),
                    true,
                    true,
                    "Observações atualizadas",
                    DeliveryType.CESAREAN,
                    null
            );

            when(updateMedicalRecordUseCase.execute(eq(cpf), any(UpdateMedicalRecordRequest.class), eq("user-123")))
                    .thenReturn(updatedRecord);

            Authentication auth = createMockAuthentication("user-123", null, null);

            mvc.perform(put("/api/v1/prontuarios/cpf/{cpf}", cpf)
                            .with(asPrincipal(auth))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.usoVitaminas").value(true))
                    .andExpect(jsonPath("$.usoAAS").value(true))
                    .andExpect(jsonPath("$.observacoes").value("Observações atualizadas"));

            verify(updateMedicalRecordUseCase).execute(eq(cpf), any(UpdateMedicalRecordRequest.class), eq("user-123"));
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/prontuarios/cpf/{cpf}/fatores-risco")
    class AtualizarFatoresDeRisco {

        @Test
        @DisplayName("deve retornar 200 ao atualizar fatores de risco com sucesso")
        void deveRetornar200AoAtualizarFatoresDeRisco() throws Exception {
            UpdateRiskFactorsRequest request = new UpdateRiskFactorsRequest(
                    List.of(RiskFactor.HYPERTENSION, RiskFactor.GESTATIONAL_DIABETES)
            );

            MedicalRecord updatedRecord = new MedicalRecord(
                    medicalRecord.getId(),
                    cpf,
                    medicalRecord.getFullName(),
                    medicalRecord.getDateOfBirth(),
                    null,
                    null,
                    medicalRecord.getLastMenstrualPeriod(),
                    medicalRecord.getGestationalAgeWeeks(),
                    medicalRecord.getPregnancyType(),
                    medicalRecord.getPreviousPregnancies(),
                    medicalRecord.getPreviousDeliveries(),
                    medicalRecord.getPreviousAbortions(),
                    medicalRecord.getHighRiskPregnancy(),
                    medicalRecord.getHighRiskReason(),
                    List.of(RiskFactor.HYPERTENSION, RiskFactor.GESTATIONAL_DIABETES),
                    medicalRecord.getVitaminUse(),
                    medicalRecord.getAspirinUse(),
                    medicalRecord.getNotes(),
                    medicalRecord.getDeliveryType(),
                    null
            );

            when(updateRiskFactorsUseCase.execute(eq(cpf), any(UpdateRiskFactorsRequest.class), eq("user-123")))
                    .thenReturn(updatedRecord);

            Authentication auth = createMockAuthentication("user-123", null, null);

            mvc.perform(patch("/api/v1/prontuarios/cpf/{cpf}/fatores-risco", cpf)
                            .with(asPrincipal(auth))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fatoresRisco").isArray())
                    .andExpect(jsonPath("$.fatoresRisco.length()").value(2));

            verify(updateRiskFactorsUseCase).execute(eq(cpf), any(UpdateRiskFactorsRequest.class), eq("user-123"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/prontuarios/cpf/{cpf}/historico")
    class BuscarHistorico {

        @Test
        @DisplayName("deve retornar 200 ao buscar histórico com sucesso")
        void deveRetornar200AoBuscarHistorico() throws Exception {
            List<MedicalRecordHistory> history = List.of(
                    new MedicalRecordHistory(medicalRecord.getId(), "user-1", "Prontuário criado"),
                    new MedicalRecordHistory(medicalRecord.getId(), "user-2", "Dados atualizados")
            );

            when(findMedicalRecordHistoryUseCase.execute(cpf)).thenReturn(history);

            Authentication auth = createMockAuthentication("user-123", null, null);

            mvc.perform(get("/api/v1/prontuarios/cpf/{cpf}/historico", cpf)
                            .with(asPrincipal(auth)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2));

            verify(findMedicalRecordHistoryUseCase).execute(cpf);
        }
    }
}
