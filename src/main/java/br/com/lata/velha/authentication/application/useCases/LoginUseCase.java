package br.com.lata.velha.authentication.application.useCases;

import br.com.lata.velha.application.dto.request.LoginRequest;
import br.com.lata.velha.authentication.application.security.TokenProvider;
import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoginUseCase {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public Output execute(Input input) {
        var user = userRepository.getByUsername(input.username());
        user.login(input.senha);

        String scopes = user.getRoles()
                .stream()
                .map(Role::getNome)
                .collect(Collectors.joining(" "));

        String token = tokenProvider.generate(user.getId(), scopes);

        return new Output(token, tokenProvider.getExpiresIn());
    }

    public record Input(String username, String senha) {
        public static Input fromRequest(LoginRequest request) {
            return new Input(request.username(), request.password());
        }
    }

    public record Output(String token, Long expiresIn){}
}