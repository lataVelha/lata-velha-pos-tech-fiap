package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.OrdemServicoNotFoundException;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.OrdemServicoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.repositories.projection.OrdemServicoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


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