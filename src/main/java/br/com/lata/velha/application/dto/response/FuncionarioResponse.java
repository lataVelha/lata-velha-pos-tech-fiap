package br.com.lata.velha.application.dto.response;

public record FuncionarioResponse(
    Long id,
    String nome,
    String username,
    boolean ativo,
    String cargoNome
) {}