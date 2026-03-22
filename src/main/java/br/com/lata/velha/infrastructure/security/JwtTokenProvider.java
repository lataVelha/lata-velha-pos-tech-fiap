package br.com.lata.velha.infrastructure.security;

import br.com.lata.velha.application.port.TokenProvider;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JwtTokenProvider implements TokenProvider {

    private static final long EXPIRES_IN_SECONDS = 300L;
    private static final String ISSUER = "mybackend";

    private final JwtEncoder jwtEncoder;

    public JwtTokenProvider(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public String generate(Long userId, String scopes) {
        var now = Instant.now();

        var claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject(userId.toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(EXPIRES_IN_SECONDS))
                .claim("scope", scopes)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public long getExpiresIn() {
        return EXPIRES_IN_SECONDS;
    }
}