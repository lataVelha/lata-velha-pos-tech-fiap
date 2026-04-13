package br.com.lata.velha.ordemDeServico.domain.exceptions.notFoundExceptions;

import br.com.lata.velha.ordemDeServico.domain.entities.OrdemServico;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;

public class OrdemServicoNotFoundException extends NotFoundException {
    private OrdemServicoNotFoundException(String param, String value) {
        super(OrdemServico.class, param, value);
    }

    public static OrdemServicoNotFoundException fromId(Long value) {
        return new OrdemServicoNotFoundException("id", value.toString());
    }
}
