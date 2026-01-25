package com.hackathon.sus.prenatal_prontuario.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Risk factors in pregnancy. JSON: "HIPERTENSAO", "DIABETES_GESTACIONAL", etc.
 */
public enum RiskFactor {
    HYPERTENSION("HIPERTENSAO"),
    GESTATIONAL_DIABETES("DIABETES_GESTACIONAL"),
    OBESITY("OBESIDADE"),
    ADVANCED_AGE("IDADE_AVANCADA"),
    TWIN_PREGNANCY("GEMELAR"),
    OTHER("OUTROS");

    private final String jsonValue;

    RiskFactor(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String toJson() {
        return jsonValue;
    }

    @JsonCreator
    public static RiskFactor fromJson(String v) {
        if (v == null) return null;
        for (RiskFactor f : values())
            if (f.jsonValue.equals(v)) return f;
        throw new IllegalArgumentException("Fator de risco inválido: " + v);
    }
}
