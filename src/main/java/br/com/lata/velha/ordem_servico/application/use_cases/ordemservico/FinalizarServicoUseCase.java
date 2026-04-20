package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FinalizarServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoRepository veiculoRepository;
    private final PecaRepository pecaRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    @Transactional
    public OrdemServicoResponse execute(Long idOs, Long servicoId, UUID userId) {
        var ordemServico = ordemServicoRepository.getById(idOs);
        var mecanico = funcionarioRepository.getByUserId(userId);

        if (!StatusOrdemServico.EM_EXECUCAO.equals(ordemServico.getStatus())) {
            throw new IllegalStateException(
                    "Esta Ordem de Serviço não está em execução: " + ordemServico.getId()
            );
        }

        var execucao = ordemServico.getExecucaoServicos().stream()
                .filter(e -> e.getId().equals(servicoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Serviço não encontrado na OS: " + servicoId));

        if (!StatusExecucaoServico.EM_EXECUCAO.equals(execucao.getStatus())) {
            throw new IllegalStateException(
                    "Este serviço não está em execução: " + servicoId);
        }

        execucao.getPecas().forEach(peca -> {
            if (peca.getId() == null) return;
            if (!peca.getStatus().equals(StatusPecaAlocada.RESERVADA)) return;
            peca.instalada(peca.getQuantidadeSolicitada());
            execucao.atualizarPeca(peca);
        });

        execucao.finalizar();

        boolean todosConcluidosOuRecusados = ordemServico.getExecucaoServicos().stream()
                .allMatch(e -> e.isFinalizado() || e.isRecusado());

        if (todosConcluidosOuRecusados) {
            ordemServico.finalizar(mecanico.getId());
        }

        var saved = ordemServicoRepository.save(ordemServico);

        if (todosConcluidosOuRecusados) {
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
                .collect(Collectors.toMap(id -> id, pecaRepository::getActiveById));

        return OrdemServicoResponse.from(saved,
                atendente.getNome(),
                mecanico.getNome(),
                proprietario.getNome(),
                veiculo.getMarca() + " " + veiculo.getModelo(),
                null, null, null,
                mecanicoNomes, pecaMap);
    }
}
