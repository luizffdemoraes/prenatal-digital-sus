package br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.repository;


import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
    Optional<RoleEntity> findByAuthority(String roleAdmin);
}
