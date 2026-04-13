package br.com.lata.velha.authentication.domain.exceptions;

import br.com.lata.velha.shared.domain.exceptions.DomainException;
import br.com.lata.velha.domain.entities.Funcionario;

public class InactiveUserException extends DomainException {
    private InactiveUserException(String message) {
        super(message);
    }

    public static InactiveUserException fromFuncionario(Funcionario funcionario) {
        var message = composeMessage(funcionario.getClass().getName());
        return new InactiveUserException(message);
    }

    private static String composeMessage(String className) {
        return className + "está inativo!";
    }
}
