package br.com.hackathon.sus.prenatal_auth.application.dtos.responses;


import br.com.hackathon.sus.prenatal_auth.domain.entities.Address;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AddressResponse(
        @JsonProperty("rua")
        String street,
        @JsonProperty("numero")
        Long number,
        @JsonProperty("cidade")
        String city,
        @JsonProperty("estado")
        String state,
        @JsonProperty("cep")
        String zipCode) {
    public AddressResponse(Address address) {
        this(
                address.getStreet(),
                address.getNumber(),
                address.getCity(),
                address.getState(),
                address.getZipCode()
        );
    }
}

