package br.com.lata.velha.ordemDeServico.application.useCases.pecaalocada;

import br.com.lata.velha.ordemDeServico.application.assemblers.PecaAlocadaAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.request.AlocarPecaRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Peca;
import br.com.lata.velha.ordemDeServico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaRepository;
import br.com.lata.velha.ordemDeServico.domain.repositories.ServicoOSRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AlocarPecaUseCase {

    private final PecaAlocadaRepository pecaAlocadaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaRepository pecaRepository;
    private final ServicoOSRepository servicoOSRepository;

    @Transactional
    public PecaAlocadaResponse execute(AlocarPecaRequest request) {
        
        var servicoOS = servicoOSRepository.findById(request.servicoOsId());
        if (servicoOS == null) {
            throw new IllegalArgumentException("Serviço OS não encontrado");
        }

        Peca peca = pecaRepository.findActiveById(request.pecaId());
        if (peca == null) {
            throw new IllegalArgumentException("Peça não encontrada");
        }

        var estoque = pecaEstoqueRepository.findByPecaId(request.pecaId());
        if (estoque == null) {
            throw new IllegalArgumentException("Estoque da peça não encontrado");
        }

        estoque.remover(request.quantidade());
        pecaEstoqueRepository.save(estoque);

        var pecaAlocada = new PecaAlocada( peca.getId(), request.servicoOsId(), request.quantidade());

        PecaAlocada saved = pecaAlocadaRepository.save(pecaAlocada);

        return PecaAlocadaAssembler.toResponse(saved);
    }
}