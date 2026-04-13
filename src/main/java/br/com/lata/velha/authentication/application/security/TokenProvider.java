package br.com.lata.velha.authentication.application.security;

import br.com.lata.velha.shared.domain.valueObjects.UserId;

/**
 * Contrato para geração de tokens.
 * A camada de aplicação usa esta interface sem saber que é JWT por baixo.
 * A implementação concreta fica em infrastructure.security.
 */
public interface TokenProvider {
    String generate(UserId userId, String scopes);
    long getExpiresIn();
}