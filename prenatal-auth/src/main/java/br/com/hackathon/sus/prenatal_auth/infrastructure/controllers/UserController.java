package br.com.hackathon.sus.prenatal_auth.infrastructure.controllers;


import br.com.hackathon.sus.prenatal_auth.application.dtos.requests.PasswordRequest;
import br.com.hackathon.sus.prenatal_auth.application.dtos.requests.UserRequest;
import br.com.hackathon.sus.prenatal_auth.application.dtos.responses.UserResponse;
import br.com.hackathon.sus.prenatal_auth.application.usecases.CreateUserUseCase;
import br.com.hackathon.sus.prenatal_auth.application.usecases.FindUserByCpfUseCase;
import br.com.hackathon.sus.prenatal_auth.application.usecases.FindUserByIdUseCase;
import br.com.hackathon.sus.prenatal_auth.application.usecases.UpdatePasswordUseCase;
import br.com.hackathon.sus.prenatal_auth.application.usecases.UpdateUserUseCase;
import br.com.hackathon.sus.prenatal_auth.domain.entities.User;
import br.com.hackathon.sus.prenatal_auth.infrastructure.config.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/v1/usuarios")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final FindUserByCpfUseCase findUserByCpfUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UpdatePasswordUseCase updatePasswordUseCase;

    public UserController(CreateUserUseCase createUserUseCase,
                          FindUserByIdUseCase findUserByIdUseCase,
                          FindUserByCpfUseCase findUserByCpfUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          UpdatePasswordUseCase updatePasswordUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.findUserByIdUseCase = findUserByIdUseCase;
        this.findUserByCpfUseCase = findUserByCpfUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.updatePasswordUseCase = updatePasswordUseCase;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequest request) {
        User user = UserMapper.toDomain(request);
        User userSave = this.createUserUseCase.execute(user);
        UserResponse response = UserMapper.toResponse(userSave);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Object> findUserByCpf(@PathVariable String cpf) {
        Optional<User> user = this.findUserByCpfUseCase.execute(cpf);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        UserResponse response = UserMapper.toResponse(user.get());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable Integer id) {
        User user = this.findUserByIdUseCase.execute(id);
        UserResponse response = UserMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Integer id, @Valid @RequestBody UserRequest request) {
        User user = UserMapper.toDomain(request);
        User responseUpdate = updateUserUseCase.execute(id, user);
        UserResponse response = UserMapper.toResponse(responseUpdate);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}/senha")
    public ResponseEntity<Void> updatePassword(@PathVariable Integer id, @Valid @RequestBody PasswordRequest request) {
        this.updatePasswordUseCase.execute(id, request.password());
        return ResponseEntity.noContent().build();
    }
}
