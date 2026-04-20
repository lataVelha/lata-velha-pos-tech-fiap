package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.request.AddServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.use_cases.peca.BuscarPecaPorIdUseCase;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdicionarServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final BuscarPecaPorIdUseCase buscarPecaPorIdUseCase;
    private final ServicoRepository servicoRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoRepository veiculoRepository;

    public OrdemServicoResponse execute(AddServicoRequest request) {
        var ordemServico = ordemServicoRepository.getById(request.idOs());

        // Fase 1: adiciona os serviços sem peças para obter os IDs após o save
        request.servicoRequests().forEach(servicoRequest -> {
            var servico = servicoRepository.findActiveById(servicoRequest.servicoId());
            ordemServico.adicionarServico(new ExecucaoServico(servico, servicoRequest.valorMaoDeObra()));
        });

        var savedComServicos = ordemServicoRepository.save(ordemServico);

        // Fase 2: com os IDs disponíveis, adiciona as peças a cada serviço
        request.servicoRequests().forEach(servicoRequest -> {
            if (servicoRequest.pecas() == null || servicoRequest.pecas().isEmpty()) return;

            var execucao = savedComServicos.getExecucaoServicos().stream()
                    .filter(e -> e.getServico().getId().equals(servicoRequest.servicoId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "Serviço não encontrado após save: " + servicoRequest.servicoId()));

            servicoRequest.pecas().forEach(pecaRequest -> {
                var peca = buscarPecaPorIdUseCase.execute(pecaRequest.pecaId());
                execucao.adicionarPeca(PecaAlocada.create(peca.id(), execucao.getId(), pecaRequest.quantidade()));
            });
        });

        var saved = ordemServicoRepository.save(savedComServicos);
        var proprietario = proprietarioRepository.getActiveById(saved.getProprietarioId());
        var veiculo = veiculoRepository.getActiveById(saved.getVeiculoId());

        return OrdemServicoResponse.from(saved,
                null,
                null,
                proprietario.getNome(),
                veiculo.getMarca() + " " + veiculo.getModelo(),
                null, null, null);
    }
}
