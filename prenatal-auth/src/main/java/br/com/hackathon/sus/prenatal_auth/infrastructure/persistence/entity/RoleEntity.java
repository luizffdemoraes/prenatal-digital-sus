package br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.entity;


import br.com.hackathon.sus.prenatal_auth.domain.entities.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;


@Entity
@Table(name = "roles", schema = "auth")
public class RoleEntity implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String authority;

    public RoleEntity() {
    }

    public RoleEntity(Integer id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    public Role toDomain() {
        return new Role(this.id, this.authority);
    }

    public static RoleEntity fromDomain(Role role) {
        return new RoleEntity(role.getId(), role.getAuthority());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        RoleEntity roleEntity = (RoleEntity) o;
        return id.equals(roleEntity.id) && authority.equals(roleEntity.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authority);
    }
}
