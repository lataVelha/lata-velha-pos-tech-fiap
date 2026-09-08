package br.com.lata.velha.ordem_servico.application.use_cases.historicoestadoos;

import br.com.lata.velha.ordem_servico.domain.entities.HistoricoEstadoOs;


public interface CadastrarHistoricoEstadoOsGateway {

    void salvar(HistoricoEstadoOs historico);

}
