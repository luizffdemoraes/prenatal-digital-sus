package br.com.hackathon.sus.prenatal_ia.infrastructure.gateways.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserApiResponse(
        @JsonProperty("id") Integer id,
        @JsonProperty("nome") String nome,
        @JsonProperty("email") String email,
        @JsonProperty("cpf") String cpf
) {}
