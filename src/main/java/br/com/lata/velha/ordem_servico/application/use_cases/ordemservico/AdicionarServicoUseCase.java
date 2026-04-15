package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.assemblers.OrdemServicoAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.request.AddServicoOsRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.use_cases.peca.BuscarPecaPorIdUseCase;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.ServicoOS;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AdicionarServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoAssembler ordemServicoAssembler;
    private final BuscarPecaPorIdUseCase buscarPecaPorIdUseCase;
    private final ServicoRepository servicoRepository;

    public OrdemServicoResponse execute(AddServicoOsRequest request) {

        var os = ordemServicoRepository.findById(request.idOs());
        request.servicoOSRequests().stream().forEach(so -> {
            var servico = servicoRepository.findActiveById(so.servicoId());
            var servicoOs = new ServicoOS(servico, so.valorMaoDeObra());
            so.pecas().forEach(p -> {
                var peca = buscarPecaPorIdUseCase.execute(p.pecaId());
                var pecaAlocada = new PecaAlocada( peca.id(), servicoOs.getId(), p.quantidade());
                servicoOs.adicionarPeca(pecaAlocada);

            });
            os.adicionarServico(servicoOs);
        });

        return ordemServicoAssembler.toResponse(ordemServicoRepository.save(os), null, null,null,null,null);
    }
}