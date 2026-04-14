package br.com.lata.velha.authentication.infrastructure.persistence.repositories;

import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.authentication.domain.exceptions.not_found_exceptions.RoleNotFoundException;
import br.com.lata.velha.authentication.domain.repositories.RoleRepository;
import br.com.lata.velha.authentication.infrastructure.persistence.jpa.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {
    private final RoleJpaRepository jpaRepository;

    @Override
    public Role getByNome(String nome) {
        return jpaRepository.findByNome(nome)
                .orElseThrow(() -> RoleNotFoundException.fromNome(nome))
                .toDomain();
    }
}
