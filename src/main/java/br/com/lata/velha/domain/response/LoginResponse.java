package br.com.lata.velha.domain.response;

public record LoginResponse(String token, Long expiresIn) {
}
