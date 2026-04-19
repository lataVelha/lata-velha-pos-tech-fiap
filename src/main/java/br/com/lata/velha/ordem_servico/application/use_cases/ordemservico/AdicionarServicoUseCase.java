package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.request.AddServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.use_cases.peca.BuscarPecaPorIdUseCase;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
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

        request.servicoRequests().forEach(servicoRequest -> {

            var servico = servicoRepository.findActiveById(servicoRequest.servicoId());
            var execucaoServico = new ExecucaoServico(servico, servicoRequest.valorMaoDeObra());

            servicoRequest.pecas().forEach(pecaRequest -> {
                var peca = buscarPecaPorIdUseCase.execute(pecaRequest.pecaId());
                execucaoServico.adicionarPeca(new PecaAlocada(peca.id(), pecaRequest.quantidade()));
            });

            ordemServico.adicionarServico(execucaoServico);
        });

        var saved = ordemServicoRepository.save(ordemServico);
        var proprietario = proprietarioRepository.getActiveById(saved.getProprietarioId());
        var veiculo = veiculoRepository.getActiveById(saved.getVeiculoId());

        return OrdemServicoResponse.from(saved,
                proprietario.getNome(),
                veiculo.getMarca() + " " + veiculo.getModelo(),
                null, null, null);
    }
}
