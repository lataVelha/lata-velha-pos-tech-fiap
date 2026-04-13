package br.com.lata.velha.presentation.api;

import br.com.lata.velha.application.dto.request.LoginRequest;
import br.com.lata.velha.application.dto.response.LoginResponse;
import br.com.lata.velha.authentication.application.useCases.LoginUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de login e geração de token JWT")
public class LoginController {

    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    @Operation(
            summary = "Realizar login",
            description = "Autentica o funcionário e retorna um token JWT para acesso aos endpoints protegidos"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Credenciais inválidas"
    )
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        var input = LoginUseCase.Input.fromRequest(loginRequest);
        var output = loginUseCase.execute(input);
        var response = LoginResponse.fromLoginOutput(output);
        return ResponseEntity.ok(response);
    }
}