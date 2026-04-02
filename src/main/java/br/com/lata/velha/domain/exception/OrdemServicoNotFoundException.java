package br.com.lata.velha.domain.exception;

public class OrdemServicoNotFoundException extends DomainException {
    public OrdemServicoNotFoundException(Long id) { super("OS :" + id +" não encontrada!"); }
}
