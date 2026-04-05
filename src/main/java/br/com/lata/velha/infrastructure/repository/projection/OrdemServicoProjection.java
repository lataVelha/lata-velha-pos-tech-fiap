package br.com.lata.velha.infrastructure.repository.projection;

import java.time.LocalDateTime;

public interface OrdemServicoProjection {


    Long getId();

    Long getAtendenteInicioId();
    String getAtendenteNome();

    Long getVeiculoId();
    String getVeiculoDescricao();

    Long getProprietarioId();
    String getProprietarioNome();

    Long getMecanicoFinalId();
    String getMecanicoNome();

    String getStatus();
    String getReclamacaoCliente();

    LocalDateTime getIniciadoEm();
    LocalDateTime getFinalizadoEm();
    LocalDateTime getEntregueEm();
    LocalDateTime getAtualizadoEm();

    String getServicos();
}