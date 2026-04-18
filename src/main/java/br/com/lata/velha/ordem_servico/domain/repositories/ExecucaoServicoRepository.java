package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;

public interface ExecucaoServicoRepository {

    ExecucaoServico save(ExecucaoServico execucaoServico);

    ExecucaoServico findById(Long id);

}