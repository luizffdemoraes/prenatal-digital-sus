package com.hackathon.sus.prenatal_prontuario.infrastructure.controllers;

import com.hackathon.sus.prenatal_prontuario.application.dtos.requests.CreateMedicalRecordRequest;
import com.hackathon.sus.prenatal_prontuario.application.dtos.requests.UpdateMedicalRecordRequest;
import com.hackathon.sus.prenatal_prontuario.application.dtos.requests.UpdateRiskFactorsRequest;
import com.hackathon.sus.prenatal_prontuario.application.dtos.responses.MedicalRecordHistoryResponse;
import com.hackathon.sus.prenatal_prontuario.application.dtos.responses.MedicalRecordResponse;
import com.hackathon.sus.prenatal_prontuario.application.usecases.*;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecordHistory;
import com.hackathon.sus.prenatal_prontuario.infrastructure.config.mapper.MedicalRecordHistoryMapper;
import com.hackathon.sus.prenatal_prontuario.infrastructure.config.mapper.MedicalRecordMapper;
import com.hackathon.sus.prenatal_prontuario.infrastructure.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/prontuarios")
public class MedicalRecordController {

    private final CreateMedicalRecordUseCase createMedicalRecordUseCase;
    private final FindMedicalRecordByCpfUseCase findMedicalRecordByCpfUseCase;
    private final UpdateMedicalRecordUseCase updateMedicalRecordUseCase;
    private final UpdateRiskFactorsUseCase updateRiskFactorsUseCase;
    private final FindMedicalRecordHistoryUseCase findMedicalRecordHistoryUseCase;

    public MedicalRecordController(CreateMedicalRecordUseCase createMedicalRecordUseCase,
                                   FindMedicalRecordByCpfUseCase findMedicalRecordByCpfUseCase,
                                   UpdateMedicalRecordUseCase updateMedicalRecordUseCase,
                                   UpdateRiskFactorsUseCase updateRiskFactorsUseCase,
                                   FindMedicalRecordHistoryUseCase findMedicalRecordHistoryUseCase) {
        this.createMedicalRecordUseCase = createMedicalRecordUseCase;
        this.findMedicalRecordByCpfUseCase = findMedicalRecordByCpfUseCase;
        this.updateMedicalRecordUseCase = updateMedicalRecordUseCase;
        this.updateRiskFactorsUseCase = updateRiskFactorsUseCase;
        this.findMedicalRecordHistoryUseCase = findMedicalRecordHistoryUseCase;
    }

    @PostMapping
    public ResponseEntity<MedicalRecordResponse> create(@Valid @RequestBody CreateMedicalRecordRequest request,
                                                        Authentication auth) {
        String professionalUserId = extractSubject(auth);
        var saved = createMedicalRecordUseCase.execute(request, professionalUserId);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/cpf/{cpf}").buildAndExpand(saved.getCpf()).toUri();
        return ResponseEntity.created(uri).body(MedicalRecordMapper.toResponse(saved));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<MedicalRecordResponse> getByCpf(@PathVariable String cpf, Authentication auth) {
        ensurePregnantWomanAccessesOwnByCpf(cpf, auth);
        var m = findMedicalRecordByCpfUseCase.execute(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado para o CPF informado."));
        return ResponseEntity.ok(MedicalRecordMapper.toResponse(m));
    }

    @PutMapping("/cpf/{cpf}")
    public ResponseEntity<MedicalRecordResponse> update(@PathVariable String cpf,
                                                        @Valid @RequestBody UpdateMedicalRecordRequest request,
                                                        Authentication auth) {
        String professionalUserId = extractSubject(auth);
        var updated = updateMedicalRecordUseCase.execute(cpf, request, professionalUserId);
        return ResponseEntity.ok(MedicalRecordMapper.toResponse(updated));
    }

    @PatchMapping("/cpf/{cpf}/fatores-risco")
    public ResponseEntity<MedicalRecordResponse> updateRiskFactors(@PathVariable String cpf,
                                                                   @Valid @RequestBody UpdateRiskFactorsRequest request,
                                                                   Authentication auth) {
        String professionalUserId = extractSubject(auth);
        var updated = updateRiskFactorsUseCase.execute(cpf, request, professionalUserId);
        return ResponseEntity.ok(MedicalRecordMapper.toResponse(updated));
    }

    @GetMapping("/cpf/{cpf}/historico")
    public ResponseEntity<List<MedicalRecordHistoryResponse>> getHistory(@PathVariable String cpf,
                                                                         Authentication auth) {
        ensurePregnantWomanAccessesOwnByCpf(cpf, auth);
        List<MedicalRecordHistory> list = findMedicalRecordHistoryUseCase.execute(cpf);
        List<MedicalRecordHistoryResponse> resp = list.stream()
                .map(MedicalRecordHistoryMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    private String extractSubject(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) return "sistema";
        return jwt.getSubject();
    }

    /** Gestante/paciente só acessa o prontuário cujo CPF do path é o mesmo do JWT (claim cpf). */
    private void ensurePregnantWomanAccessesOwnByCpf(String cpfPath, Authentication auth) {
        if (!hasAuthority(auth, "ROLE_GESTANTE") && !hasAuthority(auth, "ROLE_PATIENT")) return;
        String jwtCpf = getCpfClaim(auth);
        if (jwtCpf != null && cpfPath != null && cpfPath.equals(jwtCpf)) return;
        throw new AccessDeniedException("Gestante só pode acessar o próprio prontuário.");
    }

    private String getCpfClaim(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) return null;
        Object c = jwt.getClaim("cpf");
        return c != null ? c.toString() : null;
    }

    private boolean hasAuthority(Authentication auth, String role) {
        if (auth == null) return false;
        Collection<? extends GrantedAuthority> auths = auth.getAuthorities();
        return auths != null && auths.stream().anyMatch(a -> role.equals(a.getAuthority()));
    }
}
