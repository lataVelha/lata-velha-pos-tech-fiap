package br.com.lata.velha.ordem_servico.api.dtos.ordem_servico;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.CriarOrdemServicoUseCase;

public record CriarOrdemServicoResponse(Long ordemServicoId) {
    public static CriarOrdemServicoResponse fromCriarOsUseCaseOutput(CriarOrdemServicoUseCase.Output data) {
        return new CriarOrdemServicoResponse(data.ordemServicoId());
    }
}
