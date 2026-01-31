package br.com.hackathon.sus.prenatal_ia.domain.gateways;

import java.util.Optional;

public interface AuthGateway {
    Optional<String> findEmailByCpf(String cpf);
}
