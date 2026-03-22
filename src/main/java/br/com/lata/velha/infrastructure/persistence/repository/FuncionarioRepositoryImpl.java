package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import br.com.lata.velha.infrastructure.persistence.mapper.FuncionarioPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class FuncionarioRepositoryImpl implements FuncionarioRepository {

    private final FuncionarioJpaRepository jpaRepository;
    private final FuncionarioPersistenceMapper mapper;

    public FuncionarioRepositoryImpl(FuncionarioJpaRepository jpaRepository,
                                     FuncionarioPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Funcionario> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(mapper::toDomain);
    }
}