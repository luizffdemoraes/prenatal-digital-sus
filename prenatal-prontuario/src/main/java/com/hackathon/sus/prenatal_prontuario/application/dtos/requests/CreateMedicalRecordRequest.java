package com.hackathon.sus.prenatal_prontuario.application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackathon.sus.prenatal_prontuario.domain.entities.DeliveryType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.PregnancyType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.RiskFactor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * Request para criar prontuário na primeira consulta. POST /api/v1/prontuarios
 * Identificação por CPF. Idade gestacional calculada a partir de dataUltimaMenstruacao.
 */
public record CreateMedicalRecordRequest(
        @JsonProperty("cpf")
        @NotBlank(message = "cpf é obrigatório")
        @Size(min = 11, max = 11, message = "CPF deve conter 11 dígitos")
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter apenas números")
        String cpf,

        @JsonProperty("nomeCompleto")
        String fullName,

        @JsonProperty("dataNascimento")
        LocalDate dateOfBirth,

        @JsonProperty("dataUltimaMenstruacao")
        @NotNull(message = "dataUltimaMenstruacao é obrigatória")
        LocalDate lastMenstrualPeriod,

        @JsonProperty("tipoGestacao")
        PregnancyType pregnancyType,

        @JsonProperty("numeroGestacoesAnteriores")
        Integer previousPregnancies,

        @JsonProperty("numeroPartos")
        Integer previousDeliveries,

        @JsonProperty("numeroAbortos")
        Integer previousAbortions,

        @JsonProperty("gestacaoAltoRisco")
        Boolean highRiskPregnancy,

        @JsonProperty("motivoAltoRisco")
        String highRiskReason,

        @JsonProperty("fatoresRisco")
        List<RiskFactor> riskFactors,

        @JsonProperty("usoVitaminas")
        Boolean vitaminUse,

        @JsonProperty("usoAAS")
        Boolean aspirinUse,

        @JsonProperty("observacoes")
        String notes,

        @JsonProperty("tipoParto")
        DeliveryType deliveryType,

        /** Opcional. Se informada, a idade gestacional é calculada entre dataUltimaMenstruacao e esta data. Útil para retroativo e testes. */
        @JsonProperty("dataConsulta")
        LocalDate consultationDate
) {
    public CreateMedicalRecordRequest {
        previousPregnancies = previousPregnancies != null ? previousPregnancies : 0;
        previousDeliveries = previousDeliveries != null ? previousDeliveries : 0;
        previousAbortions = previousAbortions != null ? previousAbortions : 0;
        highRiskPregnancy = highRiskPregnancy != null ? highRiskPregnancy : false;
        vitaminUse = vitaminUse != null ? vitaminUse : false;
        aspirinUse = aspirinUse != null ? aspirinUse : false;
    }
}
