package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.exception.InvalidLoginException;
import br.com.lata.velha.domain.exception.notFoundExceptions.FuncionarioNotFoundException;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import br.com.lata.velha.infrastructure.persistence.mapper.FuncionarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FuncionarioRepositoryImpl implements FuncionarioRepository {
    private final FuncionarioJpaRepository jpaRepository;
    private final FuncionarioPersistenceMapper mapper;

    @Override
    public Funcionario save(Funcionario funcionario) {
        var entity = mapper.toEntity(funcionario);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Funcionario> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Funcionario getById(Long id) {
        return findById(id)
                .orElseThrow(() -> FuncionarioNotFoundException.fromId(id));
    }
}