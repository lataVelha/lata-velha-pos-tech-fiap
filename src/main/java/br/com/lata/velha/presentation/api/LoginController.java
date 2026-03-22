package br.com.lata.velha.presentation.api;

import br.com.lata.velha.application.dto.request.LoginRequest;
import br.com.lata.velha.application.dto.response.LoginResponse;
import br.com.lata.velha.application.usecase.LoginUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(loginUseCase.execute(loginRequest));
    }
}