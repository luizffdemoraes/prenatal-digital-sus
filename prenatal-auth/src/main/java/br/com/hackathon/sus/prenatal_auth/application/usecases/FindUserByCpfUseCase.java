package br.com.hackathon.sus.prenatal_auth.application.usecases;

import br.com.hackathon.sus.prenatal_auth.domain.entities.User;

import java.util.Optional;

public interface FindUserByCpfUseCase {
    Optional<User> execute(String cpf);
}
