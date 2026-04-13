package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.exception.notFoundExceptions.CargoNotFoundException;
import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.repository.CargoRepository;
import br.com.lata.velha.infrastructure.persistence.mapper.FuncionarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CargoRepositoryImpl implements CargoRepository {
    private final CargoJpaRepository jpaRepository;
    private final FuncionarioPersistenceMapper mapper;

    @Override
    public Cargo getById(Long id) {
        return findById(id)
                .orElseThrow(() -> CargoNotFoundException.fromId(id));
    }

    @Override
    public Cargo getByIdWithRoles(Long id) {
        return jpaRepository.findByIdWithRoles(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> CargoNotFoundException.fromId(id));
    }

    @Override
    public Optional<Cargo> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}