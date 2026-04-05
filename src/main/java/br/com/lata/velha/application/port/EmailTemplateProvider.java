package br.com.lata.velha.application.port;

import java.util.Map;

public interface EmailTemplateProvider {

    String render(String templateName, Map<String, Object> variables);
}