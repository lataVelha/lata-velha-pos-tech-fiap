package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.infrastructure.repositories.projection.OrdemServicoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdemServicoRepository {

    OrdemServico save(OrdemServico ordemServico);

    OrdemServico getById(Long id);

    Page<OrdemServicoProjection> findByAllOrdemSevico(Long id,
                                                      String status,
                                                      Long proprietarioId,
                                                      Long mecanicoId,
                                                      Pageable pageable);
}