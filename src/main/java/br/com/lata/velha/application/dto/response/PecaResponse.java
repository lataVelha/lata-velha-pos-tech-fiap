package br.com.lata.velha.application.dto.response;

import java.math.BigDecimal;

public record PecaResponse(
    Long id,
    String nome,
    String descricao,
    BigDecimal valor,
    boolean ativo
) {}
