package br.com.lata.velha.ordemDeServico.domain.exceptions.notFoundExceptions;

import br.com.lata.velha.ordemDeServico.domain.entities.Funcionario;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;

public class FuncionarioNotFoundException extends NotFoundException {
    private FuncionarioNotFoundException(String paramName, String paramValue) {
        super(Funcionario.class, paramName, paramValue);
    }

    public static FuncionarioNotFoundException fromId(Long id) {
        return new FuncionarioNotFoundException("id", id.toString());
    }
}
