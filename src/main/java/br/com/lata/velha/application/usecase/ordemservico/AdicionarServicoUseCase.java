package br.com.lata.velha.application.usecase.ordemservico;

import br.com.lata.velha.application.assembler.OrdemServicoAssembler;
import br.com.lata.velha.application.dto.request.AddServicoOsRequest;
import br.com.lata.velha.application.dto.response.OrdemServicoResponse;
import br.com.lata.velha.application.usecase.peca.BuscarPecaPorIdUseCase;
import br.com.lata.velha.domain.entities.PecaAlocada;
import br.com.lata.velha.domain.entities.ServicoOS;
import br.com.lata.velha.domain.repository.OrdemServicoRepository;
import br.com.lata.velha.domain.repository.ServicoRepository;
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

        return ordemServicoAssembler.toResponse(ordemServicoRepository.save(os), null, null);
    }
}