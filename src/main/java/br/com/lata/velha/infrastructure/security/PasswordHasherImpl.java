package br.com.lata.velha.infrastructure.security;

import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.domain.valueObjects.Credential;
import br.com.lata.velha.authentication.domain.valueObjects.Senha;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordHasherImpl implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String hashSenha(Senha senha) {
        return passwordEncoder.encode(senha.getValor());
    }

    @Override
    public boolean match(Credential credential, String rawPassword) {
        return passwordEncoder.matches(rawPassword, credential.getHash());
    }
}
