package br.com.hackathon.sus.prenatal_auth.application.dtos.requests;


import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.entity.UserEntity;
import br.com.hackathon.sus.prenatal_auth.infrastructure.validations.UniqueValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record UserRequest(

        @JsonProperty("nome")
        @NotBlank(message = "{user.name.required}")
        String name,

        @JsonProperty("email")
        @NotBlank(message = "{user.email.required}")
        @Email(message = "{user.email.invalid}")
        @UniqueValue(domainClass = UserEntity.class, fieldName = "email", message = "{user.email.exists}")
        String email,

        @JsonProperty("login")
        @NotBlank(message = "{user.login.required}")
        String login,

        @JsonProperty("senha")
        @NotBlank(message = "{user.password.required}")
        String password,

        @JsonProperty("perfil")
        @NotBlank(message = "{user.role.required}")
        @Pattern(
                regexp = "^(ROLE_DOCTOR|ROLE_NURSE|ROLE_PATIENT)$",
                message = "{user.role.invalid}"
        )
        String role,

        @JsonProperty("endereco")
        @Valid
        @NotNull(message = "{address.required}")
        AddressRequest address) {

    public UserRequest(String name, String email, String login, String password, String role, AddressRequest address) {
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.role = role;
        this.address = address;
    }
}
