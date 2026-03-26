package br.com.lata.velha.application.usecase.auth;

import br.com.lata.velha.application.dto.request.LoginRequest;
import br.com.lata.velha.application.dto.response.LoginResponse;
import br.com.lata.velha.application.port.TokenProvider;
import br.com.lata.velha.domain.exception.InvalidLoginException;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.model.Role;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
@Component
@RequiredArgsConstructor
@Service
public class LoginUseCase {

    private final FuncionarioRepository funcionarioRepository;
    private final TokenProvider tokenProvider;

    public LoginResponse execute(LoginRequest request) {

        Funcionario funcionario = funcionarioRepository.buscarPorNome(request.username())
                .orElseThrow(() -> new InvalidLoginException());

        if (!funcionario.autenticar(request.password())) {
            throw new InvalidLoginException();
        }

        String scopes = funcionario.getCargo().getRoles()
                .stream()
                .map(Role::getNome)
                .collect(Collectors.joining(" "));

        String token = tokenProvider.generate(funcionario.getId(), scopes);

        return new LoginResponse(token, tokenProvider.getExpiresIn());
    }
}