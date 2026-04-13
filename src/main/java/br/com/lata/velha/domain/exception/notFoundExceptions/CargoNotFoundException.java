package br.com.lata.velha.domain.exception.notFoundExceptions;

import br.com.lata.velha.domain.entities.Cargo;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;

public class CargoNotFoundException extends NotFoundException {
    private CargoNotFoundException(String paramName, String paramValue) {
        super(Cargo.class, paramName, paramValue);
    }

    public static CargoNotFoundException fromId(Long id) {
        return new CargoNotFoundException("id", id.toString());
    }
}
