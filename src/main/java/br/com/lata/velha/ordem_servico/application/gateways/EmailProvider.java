package br.com.lata.velha.ordem_servico.application.gateways;

public interface EmailProvider {

    void send(String to, String subject, String htmlContent);
}
