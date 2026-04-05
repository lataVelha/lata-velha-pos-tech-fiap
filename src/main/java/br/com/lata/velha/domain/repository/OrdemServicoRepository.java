package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.model.OrdemServico;
import br.com.lata.velha.infrastructure.repository.projection.OrdemServicoProjection;
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