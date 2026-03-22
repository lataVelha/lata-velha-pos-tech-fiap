package br.com.lata.velha.domain.exception;


public  class InvalidLoginException extends DomainException {
    public InvalidLoginException() {
        super("User or password is invalid");
    }
}