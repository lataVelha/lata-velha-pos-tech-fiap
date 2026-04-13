package br.com.lata.velha.authentication.domain.services;

import br.com.lata.velha.authentication.domain.valueObjects.Credential;
import br.com.lata.velha.authentication.domain.valueObjects.Senha;

public interface PasswordHasher {
    String hashSenha(Senha senha);
    boolean match(Credential credential, String rawPassword);
}
