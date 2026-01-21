package br.com.hackathon.sus.prenatal_auth.infrastructure.gateways;


import br.com.hackathon.sus.prenatal_auth.domain.entities.Role;
import br.com.hackathon.sus.prenatal_auth.domain.gateways.RoleGateway;
import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.entity.RoleEntity;
import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.repository.RoleRepository;

import java.util.Optional;

public class RoleGatewayImpl implements RoleGateway {

    private final RoleRepository roleRepository;

    public RoleGatewayImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findByAuthority(String authority) {
        return roleRepository.findByAuthority(authority)
                .map(RoleEntity::toDomain);
    }
}
