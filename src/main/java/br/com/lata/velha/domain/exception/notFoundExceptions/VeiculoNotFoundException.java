package br.com.lata.velha.domain.exception.notFoundExceptions;

import br.com.lata.velha.domain.entities.Veiculo;
import br.com.lata.velha.domain.valueObject.Placa;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;

public class VeiculoNotFoundException extends NotFoundException {
    private VeiculoNotFoundException(String param, String value) {
        super(Veiculo.class, param, value);
    }

    public static VeiculoNotFoundException fromId(Long value) {
        return new VeiculoNotFoundException("id", value.toString());
    }

    public static VeiculoNotFoundException fromPlaca(Placa placa) {
        return new VeiculoNotFoundException("placa", placa.toString());
    }
}