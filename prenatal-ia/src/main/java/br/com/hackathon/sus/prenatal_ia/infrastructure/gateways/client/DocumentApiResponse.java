package br.com.hackathon.sus.prenatal_ia.infrastructure.gateways.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DocumentApiResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("gestanteCpf") String gestanteCpf,
        @JsonProperty("tipoDocumento") String tipoDocumento,
        @JsonProperty("dataCriacao") LocalDateTime dataCriacao
) {}
