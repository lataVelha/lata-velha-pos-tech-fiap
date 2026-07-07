package br.com.lata.velha.ordem_servico.application.use_cases.peca;

public class DesativarPecaUseCase {

    private final DesativarPecaGateway gateway;

    public DesativarPecaUseCase(DesativarPecaGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(Long id) {
        var peca = gateway.getPecaAtivaPorId(id);
        peca.desativar();
        gateway.salvarPeca(peca);
    }
}
