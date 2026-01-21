package br.com.hackathon.sus.prenatal_auth.application.usecases;


import br.com.hackathon.sus.prenatal_auth.domain.gateways.UserGateway;

public class UpdatePasswordUseCaseImp implements UpdatePasswordUseCase{

    private final UserGateway userGateway;

    public UpdatePasswordUseCaseImp(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    @Override
    public void execute(Integer id, String newPassword) {
        this.userGateway.validateSelf(id);
        this.userGateway.updateUserPassword(id, newPassword);
    }
}
