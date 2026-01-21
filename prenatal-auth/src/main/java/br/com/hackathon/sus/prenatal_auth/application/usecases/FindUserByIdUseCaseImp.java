package br.com.hackathon.sus.prenatal_auth.application.usecases;


import br.com.hackathon.sus.prenatal_auth.domain.entities.User;
import br.com.hackathon.sus.prenatal_auth.domain.gateways.UserGateway;

public class FindUserByIdUseCaseImp implements FindUserByIdUseCase{

    private final UserGateway userGateway;

    public FindUserByIdUseCaseImp(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public User execute(Integer id) {
        User user = this.userGateway.findUserById(id);
        this.userGateway.validateSelf(user.getId());
        return user;
    }
}
