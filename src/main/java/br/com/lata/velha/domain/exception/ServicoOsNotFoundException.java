package br.com.lata.velha.domain.exception;

public class ServicoOsNotFoundException extends DomainException {
    public ServicoOsNotFoundException (Long id) { super("ServiçoOS :" + id +" não foi encontrado!"); }
}
