package br.com.hackathon.sus.prenatal_auth.application.dtos.responses;


import br.com.hackathon.sus.prenatal_auth.domain.entities.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public record UserResponse(
        @JsonProperty("id")
        Integer id,
        @JsonProperty("nome")
        String name,
        @JsonProperty("email")
        String email,
        @JsonProperty("login")
        String login,
        @JsonProperty("dataUltimaAtualizacao")
        Date lastUpdateDate,
        @JsonProperty("endereco")
        AddressResponse address) {
    public UserResponse(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getLastUpdateDate(),
                new AddressResponse(user.getAddress())
        );
    }
}
