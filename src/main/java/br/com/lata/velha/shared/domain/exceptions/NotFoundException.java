package br.com.lata.velha.shared.domain.exceptions;

import br.com.lata.velha.domain.exception.DomainException;

import java.lang.reflect.Type;

public class NotFoundException extends DomainException {
    private NotFoundException(String message) {
        super(message);
    }

    protected NotFoundException(Type type, String paramName, String paramValue) {
        this(composeMessage(type, paramName, paramValue));
    }

    private static String composeMessage(Type type, String paramName, String paramValue) {
        var message = new StringBuilder()
                .append("Entidade");
        if(type != null) {
            message.append(type.getClass().getName());
        }
        message.append(" não encontrada.");

        var isValidParamName = paramName != null && !paramName.isBlank();
        if(isValidParamName) {
            message.append("\nBuscando por ")
                    .append(paramName);

            var isValidParamValue = paramValue != null && !paramValue.isBlank();
            if(isValidParamValue) {
                message.append(" com valor")
                        .append(paramValue);
            }
        }
        return message.toString();
    }
}
