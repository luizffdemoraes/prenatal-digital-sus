package com.hackathon.sus.prenatal_prontuario.domain.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Pregnancy type (singleton or twin). JSON: "UNICA" | "GEMELAR".
 */
public enum PregnancyType {
    SINGLETON("UNICA"),
    TWIN("GEMELAR");

    private final String jsonValue;

    PregnancyType(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @JsonValue
    public String toJson() {
        return jsonValue;
    }

    @JsonCreator
    public static PregnancyType fromJson(String v) {
        if (v == null) return null;
        return switch (v) {
            case "UNICA" -> SINGLETON;
            case "GEMELAR" -> TWIN;
            default -> throw new IllegalArgumentException("Tipo de gestação inválido: " + v);
        };
    }

    /** Used by JPA converter to persist "UNICA"/"GEMELAR" in DB. */
    public String toDbValue() { return jsonValue; }

    public static PregnancyType fromDbValue(String v) { return fromJson(v); }
}
