package com.hackathon.sus.prenatal_prontuario.application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request to update clinical data. PUT /api/v1/prontuarios/{id}
 * JSON: usoVitaminas, usoAAS, observacoes. Null = do not change.
 */
public record UpdateMedicalRecordRequest(
        @JsonProperty("usoVitaminas")
        Boolean vitaminUse,

        @JsonProperty("usoAAS")
        Boolean aspirinUse,

        @JsonProperty("observacoes")
        String notes
) {}
