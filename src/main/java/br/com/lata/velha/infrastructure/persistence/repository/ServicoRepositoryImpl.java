package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.exception.notFoundExceptions.ServicoNotFoundException;
import br.com.lata.velha.domain.model.Servico;
import br.com.lata.velha.domain.repository.ServicoRepository;
import br.com.lata.velha.infrastructure.persistence.mapper.ServicoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ServicoRepositoryImpl implements ServicoRepository {

    private final ServicoJpaRepository jpaRepository;
    private final ServicoPersistenceMapper mapper;

    @Override
    public Servico save(Servico servico) {
        var entity = mapper.toEntity(servico);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Servico findActiveById(Long id) {
        return jpaRepository.findByIdAndAtivoTrue(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> ServicoNotFoundException.fromId(id));
    }

    @Override
    public PaginatedResult<Servico> findAllActivePaginated(int page, int size) {
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
