package br.com.lata.velha.ordem_servico.api.dtos.ordem_servico;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.AprovarOrdemServicoUseCase;

import java.util.List;

public record AprovarOrdemServicoResponse(
        Long idOs,
        String status,
        List<Servico> servicos
) {
    public static AprovarOrdemServicoResponse fromOutput(AprovarOrdemServicoUseCase.Output output) {
        List<Servico> servicos = output.servicos().stream()
                .map(s -> new Servico(s.idServicoOs(), s.statusServico()))
                .toList();
        return new AprovarOrdemServicoResponse(output.idOs(), output.status(), servicos);
    }

    public record Servico(Long idServicoOs, String statusServico) {}
}
