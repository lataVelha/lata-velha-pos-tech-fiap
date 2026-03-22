package br.com.lata.velha.application.port;

/**
 * Contrato para geração de tokens.
 * A camada de aplicação usa esta interface sem saber que é JWT por baixo.
 * A implementação concreta fica em infrastructure.security.
 */
public interface TokenProvider {

    String generate(Long userId, String scopes);

    long getExpiresIn();
}