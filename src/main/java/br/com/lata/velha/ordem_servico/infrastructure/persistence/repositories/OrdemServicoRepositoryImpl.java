package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.OrdemServicoNotFoundException;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.OrdemServicoMapper;
import br.com.lata.velha.ordem_servico.infrastructure.repositories.projection.OrdemServicoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class OrdemServicoRepositoryImpl implements OrdemServicoRepository {

    private final OrdemServicoJpaRepository jpaRepository;
    private final OrdemServicoMapper ordemServicoMapper;

    @Override
    public OrdemServico save(OrdemServico ordemServico) {
        var entity = ordemServicoMapper.toEntity(ordemServico);
        var saved = jpaRepository.save(entity);
        return ordemServicoMapper.toDomain(saved);
    }

    @Override
    public OrdemServico getById(Long id) {
        return jpaRepository.findById(id)
                .map(ordemServicoMapper::toDomain)
                .orElseThrow(() -> OrdemServicoNotFoundException.fromId(id));
    }

    @Override
    public OrdemServico getByIdWithExecucoes(Long id) {
        return jpaRepository.findByIdWithExecucoes(id)
                .map(ordemServicoMapper::toDomain)
                .orElseThrow(() -> OrdemServicoNotFoundException.fromId(id));
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