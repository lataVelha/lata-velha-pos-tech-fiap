package br.com.lata.velha.domain.exception;

public class ProprietarioNotFoundException extends DomainException {
    public ProprietarioNotFoundException(Long id) { super("Proprietário não encontrado com id: " + id); }
    public ProprietarioNotFoundException(String documento) { super("Proprietário não encontrado com documento: " + documento); }
}