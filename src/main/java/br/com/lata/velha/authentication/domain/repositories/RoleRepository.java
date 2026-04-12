package br.com.lata.velha.authentication.domain.repositories;

import br.com.lata.velha.authentication.domain.entities.Role;

public interface RoleRepository {
    Role getByNome(String nome);
}
