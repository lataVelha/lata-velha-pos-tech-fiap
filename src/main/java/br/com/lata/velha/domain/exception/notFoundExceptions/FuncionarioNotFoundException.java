package br.com.lata.velha.domain.exception.notFoundExceptions;

import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;

public class FuncionarioNotFoundException extends NotFoundException {
    private FuncionarioNotFoundException(String paramName, String paramValue) {
        super(Funcionario.class, paramName, paramValue);
    }

    public static FuncionarioNotFoundException fromId(Long id) {
        return new FuncionarioNotFoundException("id", id.toString());
    }
}
