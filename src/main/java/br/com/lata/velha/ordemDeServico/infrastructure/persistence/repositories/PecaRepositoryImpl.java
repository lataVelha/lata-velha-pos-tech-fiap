package br.com.lata.velha.ordemDeServico.infrastructure.persistence.repositories;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordemDeServico.domain.entities.Peca;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaRepository;
import br.com.lata.velha.ordemDeServico.infrastructure.persistence.mappers.PecaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public Peca findActiveById(Long id) {
        return jpaRepository.findByIdAndAtivoTrue(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada"));
    }

    @Override
    public List<Peca> findAllActive() {
        return jpaRepository.findByAtivoTrue().stream()
                .map(mapper::toDomain)
                .toList();
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
}
