package br.com.hackathon.sus.prenatal_auth.domain.gateways;


import br.com.hackathon.sus.prenatal_auth.domain.entities.Role;

import java.util.Optional;

public interface RoleGateway {
    Optional<Role> findByAuthority(String authority);
}
