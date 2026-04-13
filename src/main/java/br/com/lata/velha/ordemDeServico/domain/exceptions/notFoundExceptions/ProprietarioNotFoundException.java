package br.com.lata.velha.ordemDeServico.domain.exceptions.notFoundExceptions;

import br.com.lata.velha.shared.domain.exceptions.NotFoundException;

public class ProprietarioNotFoundException extends NotFoundException {
    private ProprietarioNotFoundException(String param, String value) {
        super(ProprietarioNotFoundException.class, param, value);
    }

    public static ProprietarioNotFoundException fromId(Long value) {
        return new ProprietarioNotFoundException("id", value.toString());
    }

    public static ProprietarioNotFoundException fromDocumento(String documento) {
        return new ProprietarioNotFoundException("documento", documento);
    }
}