package br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada;

import br.com.lata.velha.ordem_servico.application.assemblers.PecaAlocadaAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.request.AlocarPecaRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ExecucaoServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AlocarPecaUseCase {

    private final PecaAlocadaRepository pecaAlocadaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaRepository pecaRepository;
    private final ExecucaoServicoRepository execucaoServicoRepository;

    @Transactional
    public PecaAlocadaResponse execute(AlocarPecaRequest request) {
        
        var servicoOS = execucaoServicoRepository.findById(request.servicoOsId());
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