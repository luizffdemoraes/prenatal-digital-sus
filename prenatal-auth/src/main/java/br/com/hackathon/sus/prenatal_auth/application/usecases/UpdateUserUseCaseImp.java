package br.com.hackathon.sus.prenatal_auth.application.usecases;


import br.com.hackathon.sus.prenatal_auth.domain.entities.User;
import br.com.hackathon.sus.prenatal_auth.domain.gateways.UserGateway;

public class UpdateUserUseCaseImp implements UpdateUserUseCase{

    private final UserGateway userGateway;

    public UpdateUserUseCaseImp(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public User execute(Integer id, User user) {
        this.userGateway.validateSelf(id);
        return this.userGateway.updateUser(id, user);
    }
}
