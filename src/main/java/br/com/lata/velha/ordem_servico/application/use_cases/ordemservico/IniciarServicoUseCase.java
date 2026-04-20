package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IniciarServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PecaAlocadaRepository pecaAlocadaRepository;
    private final PecaRepository pecaRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoRepository veiculoRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    public OrdemServicoResponse execute(Long idOs, Long servicoId, UUID userId) {
        var ordemServico = ordemServicoRepository.getById(idOs);
        var mecanico = funcionarioRepository.getByUserId(userId);

        var statusOs = ordemServico.getStatus();
        if (!StatusOrdemServico.APROVADA.equals(statusOs) && !StatusOrdemServico.EM_EXECUCAO.equals(statusOs)) {
            throw new IllegalStateException(
                    "Esta Ordem de Serviço não pode ter serviços iniciados: " + ordemServico.getId()
            );
        }

        var execucao = ordemServico.getExecucaoServicos().stream()
                .filter(e -> e.getId().equals(servicoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Serviço não encontrado na OS: " + servicoId));

        boolean primeiroServico = StatusOrdemServico.APROVADA.equals(statusOs);

        if (primeiroServico) {
            ordemServico.iniciarExecucao();
        }

        if (execucao.isAprovado()) {
            execucao.iniciar(mecanico.getId());
        }

        processarPecas(execucao, mecanico.getId());

        var saved = ordemServicoRepository.save(ordemServico);

        if (primeiroServico) {
            notificarUseCase.execute(saved);
        }
        var atendente = funcionarioRepository.getById(saved.getAtendenteInicioId());
        var proprietario = proprietarioRepository.getActiveById(saved.getProprietarioId());
        var veiculo = veiculoRepository.getActiveById(saved.getVeiculoId());

        Map<Long, String> mecanicoNomes = saved.getExecucaoServicos().stream()
                .filter(e -> e.getMecanicoResponsavelId() != null)
                .collect(Collectors.toMap(
                        e -> e.getMecanicoResponsavelId(),
                        e -> funcionarioRepository.getById(e.getMecanicoResponsavelId()).getNome(),
                        (a, b) -> a
                ));

        var pecaIds = saved.getExecucaoServicos().stream()
                .flatMap(e -> e.getPecas().stream())
                .map(p -> p.getPecaId())
                .collect(Collectors.toSet());

        Map<Long, Peca> pecaMap = pecaIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        pecaRepository::getActiveById
                ));

        return OrdemServicoResponse.from(saved,
                atendente.getNome(),
                mecanico.getNome(),
                proprietario.getNome(),
                veiculo.getMarca() + " " + veiculo.getModelo(),
                null, null, null,
                mecanicoNomes, pecaMap);
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
