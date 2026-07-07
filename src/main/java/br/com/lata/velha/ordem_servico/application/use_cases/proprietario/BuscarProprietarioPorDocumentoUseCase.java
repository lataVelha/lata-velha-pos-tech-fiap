package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;

public class BuscarProprietarioPorDocumentoUseCase {

    private final BuscarProprietarioPorDocumentoGateway gateway;

    public BuscarProprietarioPorDocumentoUseCase(BuscarProprietarioPorDocumentoGateway gateway) {
        this.gateway = gateway;
    }

    public Proprietario execute(String documento) {
        String cleaned = documento.replaceAll("[^\\dA-Za-z]", "").toUpperCase();
        return gateway.getProprietarioPorDocumento(cleaned);
    }
}
