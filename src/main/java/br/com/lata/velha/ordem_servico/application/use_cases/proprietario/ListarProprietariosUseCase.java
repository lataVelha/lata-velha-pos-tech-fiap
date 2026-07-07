package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class ListarProprietariosUseCase {

    private final ListarProprietariosGateway gateway;

    public ListarProprietariosUseCase(ListarProprietariosGateway gateway) {
        this.gateway = gateway;
    }

    public PaginatedResult<Proprietario> execute(int page, int size) {
        return gateway.findAll(page, size);
    }
}
