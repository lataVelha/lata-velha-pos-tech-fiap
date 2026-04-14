package br.com.lata.velha.shared.domain.services;

import br.com.lata.velha.shared.domain.value_objects.Email;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public interface UserService {
    UserId createUser(Email email);
}
