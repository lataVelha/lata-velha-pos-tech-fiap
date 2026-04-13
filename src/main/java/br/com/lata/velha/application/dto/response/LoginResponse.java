package br.com.lata.velha.application.dto.response;

import br.com.lata.velha.authentication.application.useCases.LoginUseCase;

public record LoginResponse(String token, Long expiresIn) {
    public static LoginResponse fromLoginOutput(LoginUseCase.Output output) {
        return new LoginResponse(output.token(), output.expiresIn());
    }
}
