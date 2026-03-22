package br.com.lata.velha.domain.exception;

public class ResourceAlreadyExistsException extends DomainException {
    public ResourceAlreadyExistsException(String message) { super(message); }
}