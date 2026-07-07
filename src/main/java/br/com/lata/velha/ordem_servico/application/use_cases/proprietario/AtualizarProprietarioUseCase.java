package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;

public class AtualizarProprietarioUseCase {

    private final AtualizarProprietarioGateway gateway;

    public AtualizarProprietarioUseCase(AtualizarProprietarioGateway gateway) {
        this.gateway = gateway;
    }

    public Proprietario execute(Long id, ProprietarioRequest request) {
        Proprietario existing = gateway.getProprietarioPorId(id);
        request.updateDomain(existing);
        return gateway.salvarProprietario(existing);
    }
}
