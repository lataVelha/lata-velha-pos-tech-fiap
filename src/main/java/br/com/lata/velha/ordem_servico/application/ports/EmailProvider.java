package br.com.lata.velha.ordem_servico.application.ports;

public interface EmailProvider {

    void send(String to, String subject, String htmlContent);
}