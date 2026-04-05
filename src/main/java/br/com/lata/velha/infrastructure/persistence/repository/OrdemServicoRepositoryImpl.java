package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.exception.OrdemServicoNotFoundException;
import br.com.lata.velha.domain.model.OrdemServico;
import br.com.lata.velha.domain.repository.OrdemServicoRepository;
import br.com.lata.velha.infrastructure.persistence.mapper.OrdemServicoMapper;
import br.com.lata.velha.infrastructure.repository.projection.OrdemServicoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class OrdemServicoRepositoryImpl implements OrdemServicoRepository {

    private final OrdemServicoJpaRepository jpaRepository;

    @Override
    public OrdemServico save(OrdemServico ordemServico) {
        var entity = OrdemServicoMapper.toEntity(ordemServico);
        var saved = jpaRepository.save(entity);
        return OrdemServicoMapper.toDomain(saved);
    }

    @Override
    public OrdemServico findById(Long id) {
        return jpaRepository.findById(id)
                .map(OrdemServicoMapper::toDomain)
                .orElseThrow(() -> new OrdemServicoNotFoundException(id));
    }

    @Override
    public Page<OrdemServicoProjection> findByAllOrdemSevico(Long id, String status, Long proprietarioId, Long mecanicoId, Pageable pageable) {

        return jpaRepository.findByAllOrdemSevico(
                id,
                status,
                proprietarioId,
                mecanicoId,
                pageable
        );


    }

}