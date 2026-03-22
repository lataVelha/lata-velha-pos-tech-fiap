package br.com.lata.velha.application.dto.response;

public record LoginResponse(String token, Long expiresIn) {
}
