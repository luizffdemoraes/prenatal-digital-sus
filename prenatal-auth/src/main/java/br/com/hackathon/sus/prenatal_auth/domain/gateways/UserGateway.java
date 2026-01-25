package br.com.hackathon.sus.prenatal_auth.domain.gateways;


import br.com.hackathon.sus.prenatal_auth.domain.entities.User;

public interface UserGateway {
    User saveUser(User user);
    boolean existsUserByEmail(String email);
    boolean existsByCpf(String cpf);
    boolean existsByCpfExcludingId(String cpf, Integer excludeId);
    User findUserById(Integer id);
    User updateUser(Integer id, User user);
    void updateUserPassword(Integer id, String newPassword);
    User authenticated();
    User findUserOrThrow(Integer id);
    void validateSelf(Integer userId);
}