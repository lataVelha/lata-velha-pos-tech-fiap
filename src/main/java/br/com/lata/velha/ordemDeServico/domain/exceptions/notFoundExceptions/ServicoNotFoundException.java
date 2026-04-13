package br.com.lata.velha.ordemDeServico.domain.exceptions.notFoundExceptions;

import br.com.lata.velha.ordemDeServico.domain.entities.Servico;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;

public class ServicoNotFoundException extends NotFoundException {
    private ServicoNotFoundException(String param, String value) {
        super(Servico.class, param, value);
    }

    public static ServicoNotFoundException fromId(Long value) {
        return new ServicoNotFoundException("id", value.toString());
    }
}