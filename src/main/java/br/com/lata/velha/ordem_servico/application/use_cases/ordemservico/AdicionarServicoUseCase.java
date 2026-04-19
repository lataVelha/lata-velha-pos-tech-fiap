package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.request.AddServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.use_cases.peca.BuscarPecaPorIdUseCase;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdicionarServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final BuscarPecaPorIdUseCase buscarPecaPorIdUseCase;
    private final ServicoRepository servicoRepository;

    public OrdemServicoResponse execute(AddServicoRequest request) {
        var ordemServico = ordemServicoRepository.findById(request.idOs());

        request.servicoRequests().forEach(servicoRequest -> {
            var servico = servicoRepository.findActiveById(servicoRequest.servicoId());
            var execucaoServico = new ExecucaoServico(servico, servicoRequest.valorMaoDeObra());

            servicoRequest.pecas().forEach(pecaRequest -> {
                var peca = buscarPecaPorIdUseCase.execute(pecaRequest.pecaId());
                execucaoServico.adicionarPeca(new PecaAlocada(peca.id(), pecaRequest.quantidade()));
            });

            ordemServico.adicionarServico(execucaoServico);
        });

        return OrdemServicoResponse.from(ordemServicoRepository.save(ordemServico), null, null, null, null, null);
    }
}
