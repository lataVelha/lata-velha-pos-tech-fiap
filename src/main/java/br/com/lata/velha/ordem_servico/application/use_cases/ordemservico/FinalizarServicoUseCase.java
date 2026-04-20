package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinalizarServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoRepository veiculoRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    @Transactional
    public OrdemServicoResponse execute(Long idOs, Long idMecanico) {
        var ordemServico = ordemServicoRepository.getById(idOs);
        var mecanico = funcionarioRepository.getById(idMecanico);

        if (!StatusOrdemServico.EM_EXECUCAO.equals(ordemServico.getStatus())) {
            throw new IllegalStateException(
                    "Esta Ordem de Serviço não está em execução: " + ordemServico.getId()
            );
        }

        ordemServico.getExecucaoServicos().forEach(execucaoServico -> {
            if (!StatusExecucaoServico.EM_EXECUCAO.equals(execucaoServico.getStatus())) return;

            execucaoServico.getPecas().forEach(peca -> {
                if (peca.getId() == null) return;
                if (!peca.getStatus().equals(StatusPecaAlocada.RESERVADA)) return;
                peca.instalada(peca.getQuantidadeSolicitada());
                execucaoServico.atualizarPeca(peca);
            });

            execucaoServico.finalizar();
        });

        ordemServico.finalizar(mecanico.getId());
        var saved = ordemServicoRepository.save(ordemServico);
        notificarUseCase.execute(saved);

        var proprietario = proprietarioRepository.getActiveById(saved.getProprietarioId());
        var veiculo = veiculoRepository.getActiveById(saved.getVeiculoId());

        return OrdemServicoResponse.from(saved,
                null,
                mecanico.getNome(),
                proprietario.getNome(),
                veiculo.getMarca() + " " + veiculo.getModelo(),
                null, null, null);
    }
}