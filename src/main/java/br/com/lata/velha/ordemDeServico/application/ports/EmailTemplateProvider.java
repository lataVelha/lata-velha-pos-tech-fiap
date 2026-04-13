package br.com.lata.velha.ordemDeServico.application.ports;

import java.util.Map;

public interface EmailTemplateProvider {

    String render(String templateName, Map<String, Object> variables);
}