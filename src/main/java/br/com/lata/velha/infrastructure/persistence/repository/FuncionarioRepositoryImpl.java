package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.exception.InvalidLoginException;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import br.com.lata.velha.infrastructure.persistence.mapper.FuncionarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FuncionarioRepositoryImpl implements FuncionarioRepository {

    private final FuncionarioJpaRepository jpaRepository;
    private final FuncionarioPersistenceMapper mapper;

    @Override
    public Funcionario findByUsername(String username) {
        return jpaRepository.findByUsernameAndAtivoTrue(username)
                .map(mapper::toDomain)
                .orElseThrow(InvalidLoginException::new);
    }

    @Override
    public Funcionario save(Funcionario funcionario) {
        var entity = mapper.toEntity(funcionario);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Funcionario findActiveById(Long id) {
        return jpaRepository.findByIdAndAtivoTrue(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));
    }

    @Override
    public List<Funcionario> findAllActive() {
        return jpaRepository.findByAtivoTrue().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PaginatedResult<Funcionario> findAllActivePaginated(int page, int size) {
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
    public Funcionario findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(InvalidLoginException::new);
    }
}