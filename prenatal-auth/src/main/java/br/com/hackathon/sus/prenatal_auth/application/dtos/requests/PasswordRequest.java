package br.com.hackathon.sus.prenatal_auth.application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record PasswordRequest(
        @JsonProperty("senha")
        @NotBlank(message = "{password.required}")
        String password
) {
    public PasswordRequest(String password) {
        this.password = password;
    }
}
