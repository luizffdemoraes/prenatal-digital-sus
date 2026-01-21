package br.com.hackathon.sus.prenatal_auth.infrastructure.config.mapper;


import br.com.hackathon.sus.prenatal_auth.application.dtos.requests.AddressRequest;
import br.com.hackathon.sus.prenatal_auth.domain.entities.Address;
import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.entity.AddressEntity;

public class AddressMapper {

    public static AddressEntity fromDomain(Address address) {
        if (address == null) return null;
        return new AddressEntity(
                address.getStreet(),
                address.getNumber(),
                address.getCity(),
                address.getState(),
                address.getZipCode()
        );
    }

    public static Address toDomain(AddressRequest address) {
        if (address == null) return null;
        return new Address(
                address.street(),
                address.number(),
                address.city(),
                address.state(),
                address.zipCode()
        );
    }

    public static Address toDomain(AddressEntity address) {
        if (address == null) return null;
        return new Address(
                address.getStreet(),
                address.getNumber(),
                address.getCity(),
                address.getState(),
                address.getZipCode()
        );
    }
}
