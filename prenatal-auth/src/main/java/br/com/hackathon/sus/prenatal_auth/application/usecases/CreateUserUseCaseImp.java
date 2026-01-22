package br.com.hackathon.sus.prenatal_auth.application.usecases;


import br.com.hackathon.sus.prenatal_auth.domain.entities.Role;
import br.com.hackathon.sus.prenatal_auth.domain.entities.User;
import br.com.hackathon.sus.prenatal_auth.domain.gateways.RoleGateway;
import br.com.hackathon.sus.prenatal_auth.domain.gateways.UserGateway;
import br.com.hackathon.sus.prenatal_auth.infrastructure.exceptions.BusinessException;

public class CreateUserUseCaseImp implements CreateUserUseCase {

    private final UserGateway userGateway;
    private final RoleGateway roleGateway;

    public CreateUserUseCaseImp(UserGateway userGateway,
                                RoleGateway roleGateway) {
        this.userGateway = userGateway;
        this.roleGateway = roleGateway;
    }

    @Override
    public User execute(User user) {
        if (userGateway.existsUserByEmail(user.getEmail())) {
            throw new BusinessException("user.email.exists", new Object[0]);
        }
        // Sempre pega o authority do "request"
        String authority = user.getRoles().iterator().next().getAuthority();
        Role role = roleGateway.findByAuthority(authority)
                .orElseThrow(() -> new IllegalArgumentException("error.role.invalid: " + authority));

        // Atualize o usuário para portar apenas o papel real do banco
        user.getRoles().clear();
        user.addRole(role);

        // Aqui você tem certeza que o usuário só referencia papéis já cadastrados e com id correto!
        return userGateway.saveUser(user);
    }
}
