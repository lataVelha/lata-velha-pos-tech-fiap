package br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions;

import br.com.lata.velha.ordem_servico.domain.entities.ServicoOS;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;

public class ServicoOsNotFoundException extends NotFoundException {
    private ServicoOsNotFoundException(String param, String value) {
        super(ServicoOS.class, param, value);
    }

    public static ServicoOsNotFoundException fromId(Long value) {
        return new ServicoOsNotFoundException("id", value.toString());
    }
}
