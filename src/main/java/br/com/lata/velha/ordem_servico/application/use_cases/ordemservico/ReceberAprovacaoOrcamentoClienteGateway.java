package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ReceberAprovacaoOrcamentoClienteGateway {
    OrdemServico getOrdemServicoComServicosEPecas(Long id);
    List<PecaEstoque> getEstoquePorPecaIds(Set<Long> pecaIds);
    List<Servico> getServicosAtivosPorIds(Set<Long> ids);
    OrdemServico salvarOrdemServico(OrdemServico os);
    void salvarEstoques(Collection<PecaEstoque> estoques);
}
