package br.com.lata.velha.domain.controller;

import br.com.lata.velha.domain.request.LoginRequest;
import br.com.lata.velha.domain.response.LoginResponse;
import br.com.lata.velha.domain.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        return ResponseEntity.ok(loginService.login(loginRequest));
    }

}