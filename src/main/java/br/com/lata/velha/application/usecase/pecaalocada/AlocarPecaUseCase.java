package br.com.lata.velha.application.usecase.pecaalocada;

import br.com.lata.velha.application.assembler.PecaAlocadaAssembler;
import br.com.lata.velha.application.dto.request.AlocarPecaRequest;
import br.com.lata.velha.application.dto.response.PecaAlocadaResponse;
import br.com.lata.velha.domain.model.Peca;
import br.com.lata.velha.domain.model.PecaAlocada;
import br.com.lata.velha.domain.repository.PecaAlocadaRepository;
import br.com.lata.velha.domain.repository.PecaEstoqueRepository;
import br.com.lata.velha.domain.repository.PecaRepository;
import br.com.lata.velha.domain.repository.ServicoOSRepository;
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