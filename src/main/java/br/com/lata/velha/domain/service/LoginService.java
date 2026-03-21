package br.com.lata.velha.domain.service;


import br.com.lata.velha.domain.entity.Role;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import br.com.lata.velha.domain.request.LoginRequest;
import br.com.lata.velha.domain.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginService{

    private final FuncionarioRepository funcionarioRepository;
    private final JwtEncoder jwtEncoder;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest) {
        var user = funcionarioRepository.findByUsername(loginRequest.username());

        if (user.isEmpty() || !user.get().isLoginCorrect(loginRequest, passwordEncoder)) {
            throw new BadCredentialsException("user or password is invalid!");
        }

        var now = Instant.now();
        var expiresIn = 300L;

        var scopes = user.get().getCargo().getRoles()
                .stream()
                .map(Role::getNome)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("mybackend")
                .subject(user.get().getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(300L))
                .claim("scope", scopes)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponse(jwtValue, expiresIn);
    }

}