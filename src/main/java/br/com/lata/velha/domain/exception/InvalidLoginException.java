package br.com.lata.velha.domain.exception;


public  class InvalidLoginException extends DomainException {

    public InvalidLoginException() {
        this("Usuário ou senha inválidos");
    }

    public InvalidLoginException(String message) {
       super(message);
    }
}