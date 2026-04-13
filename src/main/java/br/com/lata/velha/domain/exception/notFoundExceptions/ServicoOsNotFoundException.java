package br.com.lata.velha.domain.exception.notFoundExceptions;

import br.com.lata.velha.domain.model.ServicoOS;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;

public class ServicoOsNotFoundException extends NotFoundException {
    private ServicoOsNotFoundException(String param, String value) {
        super(ServicoOS.class, param, value);
    }

    public static ServicoOsNotFoundException fromId(Long value) {
        return new ServicoOsNotFoundException("id", value.toString());
    }
}
