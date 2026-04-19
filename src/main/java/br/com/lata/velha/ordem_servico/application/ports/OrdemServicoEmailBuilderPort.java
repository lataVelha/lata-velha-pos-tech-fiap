package br.com.lata.velha.ordem_servico.application.ports;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;

import java.util.Map;

public interface OrdemServicoEmailBuilderPort {

    Map<String, Object> buildVariables(OrdemServico os, Proprietario proprietario, Veiculo veiculo);

    String getAssunto(StatusOrdemServico status);
}