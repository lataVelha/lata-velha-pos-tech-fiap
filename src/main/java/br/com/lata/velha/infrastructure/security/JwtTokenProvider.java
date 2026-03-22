package br.com.lata.velha.infrastructure.security;

import br.com.lata.velha.application.port.TokenProvider;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;

@Component
public class JwtTokenProvider implements TokenProvider {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long expiresIn;

    public JwtTokenProvider(
            JwtEncoder jwtEncoder,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.expires-in}") long expiresIn) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expiresIn = expiresIn;
    }

    @Override
    public String generate(Long userId, String scopes) {
        var now = Instant.now();

        var claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(userId.toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public long getExpiresIn() {
        return expiresIn;
    }
}