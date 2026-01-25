package com.hackathon.sus.prenatal_prontuario.application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackathon.sus.prenatal_prontuario.domain.entities.DeliveryType;

/**
 * Request to update clinical data. PUT /api/v1/prontuarios/cpf/{cpf}
 * JSON: usoVitaminas, usoAAS, observacoes, tipoParto. Null = do not change.
 */
public record UpdateMedicalRecordRequest(
        @JsonProperty("usoVitaminas")
        Boolean vitaminUse,

        @JsonProperty("usoAAS")
        Boolean aspirinUse,

        @JsonProperty("observacoes")
        String notes,

        @JsonProperty("tipoParto")
        DeliveryType deliveryType
) {}
