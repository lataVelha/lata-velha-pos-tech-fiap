package br.com.lata.velha.ordemDeServico.domain.exceptions.notFoundExceptions;

import br.com.lata.velha.ordemDeServico.domain.entities.Veiculo;
import br.com.lata.velha.ordemDeServico.domain.valueObjects.Placa;
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