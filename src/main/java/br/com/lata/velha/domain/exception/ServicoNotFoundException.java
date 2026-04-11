package br.com.lata.velha.domain.exception;

public class ServicoNotFoundException extends DomainException {

    public ServicoNotFoundException(Long id) {
        super("Serviço não encontrado com id: " + id);
    }
}