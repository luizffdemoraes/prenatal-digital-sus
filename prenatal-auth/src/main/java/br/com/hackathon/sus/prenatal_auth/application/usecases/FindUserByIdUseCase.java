package br.com.hackathon.sus.prenatal_auth.application.usecases;


import br.com.hackathon.sus.prenatal_auth.domain.entities.User;

public interface FindUserByIdUseCase {
    User execute(Integer id);
}
