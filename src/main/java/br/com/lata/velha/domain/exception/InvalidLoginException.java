package br.com.lata.velha.domain.exception;


public  class InvalidLoginException extends DomainException {
    public InvalidLoginException() {
       super("Usuário ou senha inválidos");
    }
}