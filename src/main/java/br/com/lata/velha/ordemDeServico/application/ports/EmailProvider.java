package br.com.lata.velha.ordemDeServico.application.ports;

public interface EmailProvider {

    void send(String to, String subject, String htmlContent);
}