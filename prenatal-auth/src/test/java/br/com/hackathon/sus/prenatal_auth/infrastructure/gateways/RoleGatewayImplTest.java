package br.com.hackathon.sus.prenatal_auth.infrastructure.gateways;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import br.com.hackathon.sus.prenatal_auth.domain.entities.Role;
import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.entity.RoleEntity;
import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.repository.RoleRepository;

class RoleGatewayImplTest {

    @InjectMocks
    private RoleGatewayImpl roleGateway;

    @Mock
    private RoleRepository roleRepository;

    private RoleEntity doctorRoleEntity;
    private RoleEntity nurseRoleEntity;
    private RoleEntity patientRoleEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorRoleEntity = new RoleEntity(1, "ROLE_DOCTOR");
        nurseRoleEntity = new RoleEntity(2, "ROLE_NURSE");
        patientRoleEntity = new RoleEntity(3, "ROLE_PATIENT");
    }

    @Test
    void findByAuthority_shouldReturnDoctorRole_whenExists() {
        when(roleRepository.findByAuthority("ROLE_DOCTOR")).thenReturn(Optional.of(doctorRoleEntity));
        Optional<Role> role = roleGateway.findByAuthority("ROLE_DOCTOR");
        assertTrue(role.isPresent());
        assertEquals("ROLE_DOCTOR", role.get().getAuthority());
        verify(roleRepository).findByAuthority("ROLE_DOCTOR");
    }

    @Test
    void findByAuthority_shouldReturnNurseRole_whenExists() {
        when(roleRepository.findByAuthority("ROLE_NURSE")).thenReturn(Optional.of(nurseRoleEntity));
        Optional<Role> role = roleGateway.findByAuthority("ROLE_NURSE");
        assertTrue(role.isPresent());
        assertEquals("ROLE_NURSE", role.get().getAuthority());
        verify(roleRepository).findByAuthority("ROLE_NURSE");
    }

    @Test
    void findByAuthority_shouldReturnPatientRole_whenExists() {
        when(roleRepository.findByAuthority("ROLE_PATIENT")).thenReturn(Optional.of(patientRoleEntity));
        Optional<Role> role = roleGateway.findByAuthority("ROLE_PATIENT");
        assertTrue(role.isPresent());
        assertEquals("ROLE_PATIENT", role.get().getAuthority());
        verify(roleRepository).findByAuthority("ROLE_PATIENT");
    }

    @Test
    void findByAuthority_shouldReturnEmptyOptional_whenNotFound() {
        when(roleRepository.findByAuthority("ROLE_UNKNOWN")).thenReturn(Optional.empty());
        Optional<Role> role = roleGateway.findByAuthority("ROLE_UNKNOWN");
        assertFalse(role.isPresent());
        verify(roleRepository).findByAuthority("ROLE_UNKNOWN");
    }
}
