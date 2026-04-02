package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.model.OrdemServico;

public interface OrdemServicoRepository {

    OrdemServico save(OrdemServico ordemServico);

    OrdemServico findById(Long id);

}