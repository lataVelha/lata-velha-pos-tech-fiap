package br.com.lata.velha.shared.domain.services;

import br.com.lata.velha.shared.domain.valueObjects.Email;
import br.com.lata.velha.shared.domain.valueObjects.UserId;

public interface UserService {
    UserId createUser(Email email);
}
