package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.PecaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class PecaRepositoryImpl implements PecaRepository {

    private final PecaJpaRepository jpaRepository;
    private final PecaPersistenceMapper mapper;

    @Override
    public Peca save(Peca peca) {
        var entity = mapper.toEntity(peca);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Peca getActiveById(Long id) {
        return jpaRepository.findByIdAndAtivoTrue(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada"));
    }

    @Override
    public PaginatedResult<Peca> findAllActivePaginated(int page, int size) {
        var result = jpaRepository.findByAtivoTrue(PageRequest.of(page, size));
        var content = result.getContent().stream().map(mapper::toDomain).toList();

        return new PaginatedResult<>(
                content,
                page,
                size,
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Override
    public boolean existsActiveById(Long pecaId) {
        return jpaRepository.existsByIdAndAtivoTrue(pecaId);
    }

    @Override
    public List<Peca> findAllByIds(Set<Long> ids) {
        return jpaRepository.findAllByIdIn(ids).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
