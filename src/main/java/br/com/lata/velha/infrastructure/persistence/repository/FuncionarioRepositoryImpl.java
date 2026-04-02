package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.exception.InvalidLoginException;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import br.com.lata.velha.infrastructure.persistence.mapper.FuncionarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public Funcionario findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(InvalidLoginException::new);
    }
}