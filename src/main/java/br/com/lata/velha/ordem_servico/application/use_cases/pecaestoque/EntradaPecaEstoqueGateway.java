package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;

import java.util.List;

public interface EntradaPecaEstoqueGateway {
    Peca getPecaAtivaPorId(Long pecaId);
    PecaEstoque getEstoquePorPecaId(Long pecaId);
    List<PecaAlocada> getPecasAlocadasPendentes(Long pecaId);
    PecaEstoque salvarEstoque(PecaEstoque estoque);
    PecaAlocada salvarPecaAlocada(PecaAlocada alocada);
    ExecucaoServico getExecucaoServicoPorId(Long id);
    ExecucaoServico salvarExecucaoServico(ExecucaoServico execucao);
}
