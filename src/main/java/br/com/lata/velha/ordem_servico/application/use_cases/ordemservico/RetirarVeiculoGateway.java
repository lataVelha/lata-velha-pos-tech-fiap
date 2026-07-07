package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface RetirarVeiculoGateway {
    OrdemServico getOrdemServicoComServicosEPecas(Long id);
    Funcionario getFuncionarioPorUserId(UserId userId);
    List<PecaEstoque> getEstoquePorPecaIds(Set<Long> pecaIds);
    OrdemServico salvarOrdemServico(OrdemServico os);
    void salvarEstoques(Collection<PecaEstoque> estoques);
}
