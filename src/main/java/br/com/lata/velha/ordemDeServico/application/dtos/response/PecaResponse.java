package br.com.lata.velha.ordemDeServico.application.dtos.response;

import java.math.BigDecimal;

public record PecaResponse(
    Long id,
    String nome,
    String descricao,
    BigDecimal valor,
    boolean ativo
) {}
