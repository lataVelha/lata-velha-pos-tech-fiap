package br.com.lata.velha.ordem_servico.infrastructure.persistence.gateways;

import br.com.lata.velha.ordem_servico.application.use_cases.peca.*;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada.*;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque.*;
import br.com.lata.velha.ordem_servico.domain.entities.*;
import br.com.lata.velha.ordem_servico.domain.repositories.*;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PecaGatewayImpl implements
        CadastrarPecaGateway,
        AtualizarPecaGateway,
        BuscarPecaPorIdGateway,
        BuscarPecasGateway,
        DesativarPecaGateway,
        BuscarPecaAlocadaPorIdGateway,
        BuscarPecasAlocadasGateway,
        AjustarPecaEstoqueGateway,
        BuscarPecaEstoqueGateway,
        EntradaPecaEstoqueGateway,
        SaidaPecaEstoqueGateway {

    private final PecaRepository pecaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaAlocadaRepository pecaAlocadaRepository;
    private final ExecucaoServicoRepository execucaoServicoRepository;

    @Override
    public Peca salvarPeca(Peca peca) {
        return pecaRepository.save(peca);
    }

    @Override
    public PecaEstoque salvarEstoque(PecaEstoque estoque) {
        return pecaEstoqueRepository.save(estoque);
    }

    @Override
    public Peca getPecaAtivaPorId(Long id) {
        return pecaRepository.getActiveById(id);
    }

    @Override
    public PaginatedResult<Peca> findAll(int page, int size) {
        return pecaRepository.findAllActivePaginated(page, size);
    }

    @Override
    public PecaAlocada getPecaAlocadaPorId(Long id) {
        return pecaAlocadaRepository.findById(id);
    }

    @Override
    public PaginatedResult<PecaAlocada> findByExecucaoServicoId(Long execucaoServicoId, int page, int size) {
        return pecaAlocadaRepository.findByServicoOsId(execucaoServicoId, page, size);
    }

    @Override
    public PecaEstoque getEstoquePorPecaId(Long pecaId) {
        return pecaEstoqueRepository.findByPecaId(pecaId)
                .orElse(new PecaEstoque(pecaId, 0, 0));
    }

    @Override
    public List<PecaAlocada> getPecasAlocadasPendentes(Long pecaId) {
        return pecaAlocadaRepository.buscarPendentesPorPecaOrdenado(pecaId);
    }

    @Override
    public PecaAlocada salvarPecaAlocada(PecaAlocada alocada) {
        return pecaAlocadaRepository.save(alocada);
    }

    @Override
    public ExecucaoServico getExecucaoServicoPorId(Long id) {
        return execucaoServicoRepository.findById(id);
    }

    @Override
    public ExecucaoServico salvarExecucaoServico(ExecucaoServico execucao) {
        return execucaoServicoRepository.save(execucao);
    }
}
