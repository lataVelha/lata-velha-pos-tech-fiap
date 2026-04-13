package br.com.lata.velha.ordemDeServico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordemDeServico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordemDeServico.infrastructure.persistence.mappers.PecaEstoquePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PecaEstoqueRepositoryImpl implements PecaEstoqueRepository {

    private final PecaEstoqueJpaRepository jpaRepository;
    private final PecaEstoquePersistenceMapper mapper;

    @Override
    public PecaEstoque save(PecaEstoque pecaEstoque) {
        var entity = mapper.toEntity(pecaEstoque);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public PecaEstoque findByPecaId(Long pecaId) {
        return jpaRepository.findById(pecaId)
                .map(mapper::toDomain)
                .orElse(null);
    }
}
