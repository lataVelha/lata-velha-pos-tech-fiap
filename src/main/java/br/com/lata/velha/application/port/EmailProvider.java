package br.com.lata.velha.application.port;

public interface EmailProvider {

    void send(String to, String subject, String htmlContent);
}