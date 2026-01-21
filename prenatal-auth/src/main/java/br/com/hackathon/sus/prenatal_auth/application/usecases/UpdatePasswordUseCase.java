package br.com.hackathon.sus.prenatal_auth.application.usecases;

public interface UpdatePasswordUseCase {
    void execute(Integer id, String password);
}
