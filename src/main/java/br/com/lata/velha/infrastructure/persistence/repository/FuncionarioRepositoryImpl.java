package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.exception.InvalidLoginException;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import br.com.lata.velha.infrastructure.persistence.mapper.FuncionarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FuncionarioRepositoryImpl implements FuncionarioRepository {

    private final FuncionarioJpaRepository jpaRepository;
    private final FuncionarioPersistenceMapper mapper;

    @Override
    public Funcionario findByUsername(String username) {
        return jpaRepository.findByUsername(username)
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
    public Optional<Funcionario> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Funcionario> findAll() {
        return jpaRepository.findByAtivoTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}