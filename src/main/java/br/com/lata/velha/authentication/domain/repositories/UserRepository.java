package br.com.lata.velha.authentication.domain.repositories;

import br.com.lata.velha.authentication.domain.entities.User;
import br.com.lata.velha.shared.domain.valueObjects.Email;
import br.com.lata.velha.shared.domain.valueObjects.UserId;

public interface UserRepository {
    User getById(UserId id);
    User getByUsernameWithRoles(String username);
    boolean existsByEmail(Email email);
    boolean isAtivoById(UserId id);
    User save(User user);

}
