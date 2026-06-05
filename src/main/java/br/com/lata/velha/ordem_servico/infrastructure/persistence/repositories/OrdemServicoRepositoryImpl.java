package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.OrdemServicoNotFoundException;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.OrdemServicoEntity;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrdemServicoRepositoryImpl implements OrdemServicoRepository {

    private final OrdemServicoJpaRepository jpaRepository;
    private final ExecucaoServicoJpaRepository execucaoServicoJpaRepository;

    @Override
    public OrdemServico save(OrdemServico ordemServico) {
        var entity = OrdemServicoEntity.fromDomain(ordemServico);
        var saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public OrdemServico getById(Long id) {
        return jpaRepository.findById(id)
                .map(OrdemServicoEntity::toDomain)
                .orElseThrow(() -> OrdemServicoNotFoundException.fromId(id));
    }

    @Override
    public OrdemServico getByIdWithExecucoesAndPecas(Long id) {
        var entity = jpaRepository.findById(id)
                .orElseThrow(() -> OrdemServicoNotFoundException.fromId(id));
        var execucoes = execucaoServicoJpaRepository.findWithPecasByOsId(id);
        return entity.toDomain(execucoes);
    }

    @Override
    public PaginatedResult<OrdemServicoProjection> findByAllOrdemSevico(Long id, String status, Long proprietarioId, Long mecanicoId, int page, int size) {
        Page<OrdemServicoProjection> result = jpaRepository.findByAllOrdemSevico(
                id,
                status,
                proprietarioId,
                mecanicoId,
                PageRequest.of(page, size)
        );

        List<OrdemServicoProjection> content = result.getContent();
        return new PaginatedResult<>(content, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }
}
