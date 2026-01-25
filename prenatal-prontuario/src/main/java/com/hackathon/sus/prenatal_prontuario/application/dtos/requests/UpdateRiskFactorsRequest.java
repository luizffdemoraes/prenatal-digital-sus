package com.hackathon.sus.prenatal_prontuario.application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackathon.sus.prenatal_prontuario.domain.entities.RiskFactor;

import java.util.List;

/**
 * Request to update risk factors. PATCH /api/v1/prontuarios/{id}/fatores-risco
 * JSON: fatoresRisco. Replaces the full list.
 */
public record UpdateRiskFactorsRequest(
        @JsonProperty("fatoresRisco")
        List<RiskFactor> riskFactors
) {
    public UpdateRiskFactorsRequest {
        riskFactors = riskFactors != null ? riskFactors : List.of();
    }
}
