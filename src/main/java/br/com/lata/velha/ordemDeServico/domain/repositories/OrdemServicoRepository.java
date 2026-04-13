package br.com.lata.velha.ordemDeServico.domain.repositories;

import br.com.lata.velha.ordemDeServico.domain.entities.OrdemServico;
import br.com.lata.velha.ordemDeServico.infrastructure.repositories.projection.OrdemServicoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdemServicoRepository {

    OrdemServico save(OrdemServico ordemServico);

    OrdemServico findById(Long id);

    Page<OrdemServicoProjection> findByAllOrdemSevico(Long id,
                                                      String status,
                                                      Long proprietarioId,
                                                      Long mecanicoId,
                                                      Pageable pageable);
}