package br.com.lata.velha.ordem_servico.api.dtos.ordem_servico;

import br.com.lata.velha.ordem_servico.application.dtos.response.TotaisOrdemServicoResponse;

import java.util.List;

public record AprovarOrdemServicoResponse(
        Long idOs,
        String status,
        List<Servico> servicos,
        TotaisOrdemServicoResponse totais
) {
    public record Servico(Long idServicoOs, String statusServico) {}
}
