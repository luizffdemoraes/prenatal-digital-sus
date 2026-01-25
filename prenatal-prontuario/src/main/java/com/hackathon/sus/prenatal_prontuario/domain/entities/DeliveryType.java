package com.hackathon.sus.prenatal_prontuario.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Tipo de parto previsto. JSON: "PARTO_NATURAL" | "CESARIANA" | "NAO_DEFINIDO".
 */
public enum DeliveryType {
    NATURAL("PARTO_NATURAL"),
    CESAREAN("CESARIANA"),
    UNDECIDED("NAO_DEFINIDO");

    private final String jsonValue;

    DeliveryType(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String toJson() {
        return jsonValue;
    }

    @JsonCreator
    public static DeliveryType fromJson(String v) {
        if (v == null) return null;
        return switch (v) {
            case "PARTO_NATURAL" -> NATURAL;
            case "CESARIANA" -> CESAREAN;
            case "NAO_DEFINIDO" -> UNDECIDED;
            default -> throw new IllegalArgumentException("Tipo de parto inválido: " + v);
        };
    }

    /** Usado pelo converter JPA para persistir no banco. */
    public String toDbValue() { return jsonValue; }

    public static DeliveryType fromDbValue(String v) { return fromJson(v); }
}
