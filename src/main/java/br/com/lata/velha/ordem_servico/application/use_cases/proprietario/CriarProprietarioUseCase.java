package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;

public class CriarProprietarioUseCase {

    private final CriarProprietarioGateway gateway;
    private final NotificarCadastroProprietarioUseCase notificarUseCase;

    public CriarProprietarioUseCase(CriarProprietarioGateway gateway, NotificarCadastroProprietarioUseCase notificarUseCase) {
        this.gateway = gateway;
        this.notificarUseCase = notificarUseCase;
    }

    public Proprietario execute(ProprietarioRequest request) {
        Proprietario saved = gateway.salvarProprietario(request.toDomain());
        notificarUseCase.execute(saved);
        return saved;
    }
}
