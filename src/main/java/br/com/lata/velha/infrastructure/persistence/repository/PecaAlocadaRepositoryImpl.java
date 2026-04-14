package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.model.PecaAlocada;
import br.com.lata.velha.domain.repository.PecaAlocadaRepository;
import br.com.lata.velha.infrastructure.persistence.mapper.PecaAlocadaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PecaAlocadaRepositoryImpl implements PecaAlocadaRepository {

    private final PecaAlocadaJpaRepository jpaRepository;
    private final PecaAlocadaPersistenceMapper mapper;

    @Override
    public PecaAlocada save(PecaAlocada pecaAlocada) {
        var entity = mapper.toEntity(pecaAlocada);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public PecaAlocada findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Peça alocada não encontrada"));
    }

    @Override
    public PaginatedResult<PecaAlocada> findByServicoOsId(Long servicoOsId, int page, int size) {
        var result = jpaRepository.findByServicoOSId(servicoOsId, PageRequest.of(page, size));
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
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Integer somarQuantidadeReservadaPorPeca(Long pecaId) {
        return jpaRepository.somarQuantidadeReservadaPorPeca(pecaId);
    }

    @Override
    public List<PecaAlocada> buscarPendentesPorPecaOrdenado(Long pecaId) {
        return jpaRepository.buscarPendentesPorPecaOrdenado(pecaId)
                .stream().map(mapper::toDomain).toList();
    }
}