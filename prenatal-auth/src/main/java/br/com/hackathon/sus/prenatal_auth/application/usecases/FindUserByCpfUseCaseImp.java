package br.com.hackathon.sus.prenatal_auth.application.usecases;

import br.com.hackathon.sus.prenatal_auth.domain.entities.User;
import br.com.hackathon.sus.prenatal_auth.domain.gateways.UserGateway;

import java.util.Optional;

public class FindUserByCpfUseCaseImp implements FindUserByCpfUseCase {

    private final UserGateway userGateway;

    public FindUserByCpfUseCaseImp(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public Optional<User> execute(String cpf) {
        return userGateway.findUserByCpf(cpf);
    }
}
