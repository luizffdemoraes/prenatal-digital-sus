package br.com.hackathon.sus.prenatal_auth.infrastructure.config.dependency;



import br.com.hackathon.sus.prenatal_auth.application.usecases.*;
import br.com.hackathon.sus.prenatal_auth.domain.gateways.RoleGateway;
import br.com.hackathon.sus.prenatal_auth.domain.gateways.UserGateway;
import br.com.hackathon.sus.prenatal_auth.infrastructure.controllers.UserController;
import br.com.hackathon.sus.prenatal_auth.infrastructure.gateways.RoleGatewayImpl;
import br.com.hackathon.sus.prenatal_auth.infrastructure.gateways.UserGatewayImpl;
import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.repository.RoleRepository;
import br.com.hackathon.sus.prenatal_auth.infrastructure.persistence.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DependencyInjectionConfig {

    // Controller
    @Bean
    public UserController userController(
            CreateUserUseCase createUserUseCase,
            FindUserByIdUseCase findUserByIdUseCase,
            UpdateUserUseCase updateUserUseCase,
            UpdatePasswordUseCase updatePasswordUseCase
    ) {
        return new UserController(
                createUserUseCase,
                findUserByIdUseCase,
                updateUserUseCase,
                updatePasswordUseCase
        );
    }


    // Gateways



    // User Use Cases

    @Bean
    public CreateUserUseCase createUserUseCase(UserGateway userGateway,
                                               RoleGateway roleGateway) {
        return new CreateUserUseCaseImp(userGateway, roleGateway);
    }

    @Bean
    public FindUserByIdUseCase findUserByIdUseCase(UserGateway userGateway) {
        return new FindUserByIdUseCaseImp(userGateway);
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(UserGateway userGateway) {
        return new UpdateUserUseCaseImp(userGateway);
    }

    @Bean
    public UpdatePasswordUseCase updatePasswordUseCase(UserGateway userGateway) {
        return new UpdatePasswordUseCaseImp(userGateway);
    }

    @Bean
    public UserGateway userGateway(UserRepository repository,
                                   PasswordEncoder passwordEncoder,
                                   RoleRepository roleRepository) {
        return new UserGatewayImpl(repository, passwordEncoder, roleRepository);
    }

    @Bean
    public RoleGateway roleGateway(RoleRepository repository) {
        return new RoleGatewayImpl(repository);
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByEmail(username)
                .or(() -> userRepository.findByLogin(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
