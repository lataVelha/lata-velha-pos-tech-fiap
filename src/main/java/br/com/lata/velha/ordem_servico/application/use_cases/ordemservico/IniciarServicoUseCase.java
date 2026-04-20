package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IniciarServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PecaAlocadaRepository pecaAlocadaRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoRepository veiculoRepository;

    public OrdemServicoResponse execute(Long idOs, Long idMecanico) {
        var ordemServico = ordemServicoRepository.getById(idOs);
        var mecanico = funcionarioRepository.getById(idMecanico);

        if (!StatusOrdemServico.EM_EXECUCAO.equals(ordemServico.getStatus())) {
            throw new IllegalStateException(
                    "Esta Ordem de Serviço não pode ser iniciada: " + ordemServico.getId()
            );
        }

        ordemServico.getExecucaoServicos()
                .forEach(execucao -> processarPecas(execucao, mecanico.getId()));

        var saved = ordemServicoRepository.save(ordemServico);
        var atendente = funcionarioRepository.getById(saved.getAtendenteInicioId());
        var proprietario = proprietarioRepository.getActiveById(saved.getProprietarioId());
        var veiculo = veiculoRepository.getActiveById(saved.getVeiculoId());

        return OrdemServicoResponse.from(saved,
                atendente.getNome(),
                mecanico.getNome(),
                proprietario.getNome(),
                veiculo.getMarca() + " " + veiculo.getModelo(),
                null, null, null);
    }

    private void processarPecas(ExecucaoServico execucaoServico, Long mecanicoId) {
        boolean temReservada = false;
        boolean temNaoReservada = false;

        for (var peca : execucaoServico.getPecas()) {
            var alocada = pecaAlocadaRepository
                    .findByPecaIdAndServicoOsId(peca.getPecaId(), execucaoServico.getId());

            if (alocada == null) continue;

            switch (alocada.getStatus()) {
                case RESERVADA -> {
                    temReservada = true;
                    execucaoServico.processarPeca(peca, peca.getQuantidadeReservada(), mecanicoId);
                }
                case ENCOMENDA, PARCIAL -> {
                    temNaoReservada = true;
                    execucaoServico.processarPeca(peca, peca.getQuantidadeReservada(), mecanicoId);
                }
            }
        }

        if (temNaoReservada) {
            execucaoServico.setStatus(StatusExecucaoServico.AGUARDANDO_PECA);
        } else if (temReservada) {
            execucaoServico.setStatus(StatusExecucaoServico.EM_EXECUCAO);
        }
    }
}
