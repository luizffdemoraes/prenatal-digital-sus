package br.com.hackathon.sus.prenatal_auth.application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record AddressRequest(
        @JsonProperty("rua")
        @NotBlank(message = "{address.street.required}")
        String street,

        @JsonProperty("numero")
        @NotNull(message = "{address.number.required}")
        Long number,

        @JsonProperty("cidade")
        @NotBlank(message = "{address.city.required}")
        String city,

        @JsonProperty("estado")
        @NotBlank(message = "{address.state.required}")
        String state,

        @JsonProperty("cep")
        @NotBlank(message = "{address.zipcode.required}")
        String zipCode) {

    public AddressRequest(String street, Long number, String city, String state, String zipCode) {
        this.street = street;
        this.number = number;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }
}
