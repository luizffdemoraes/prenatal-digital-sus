package br.com.hackathon.sus.prenatal_ia.infrastructure.gateways.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProntuarioApiResponse(
        @JsonProperty("id") String id,
        @JsonProperty("cpf") String cpf,
        @JsonProperty("nomeCompleto") String nomeCompleto,
        @JsonProperty("dataUltimaMenstruacao") LocalDate dataUltimaMenstruacao,
        @JsonProperty("idadeGestacionalSemanas") Integer idadeGestacionalSemanas,
        @JsonProperty("gestacaoAltoRisco") Boolean gestacaoAltoRisco,
        @JsonProperty("fatoresRisco") List<String> fatoresRisco
) {}
