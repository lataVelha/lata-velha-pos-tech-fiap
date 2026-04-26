package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IniciarServicoUseCase {
    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    @Transactional
    public void execute(Input input) {
        var ordemServico = ordemServicoRepository.getByIdWithExecucoesAndPecas(input.idOs());
        var mecanico = funcionarioRepository.getByUserId(input.userId());
        var primeiroServico = !ordemServico.isEmExecucao();
        ordemServico.iniciarExecucaoServico(input.servicoId(), mecanico.getId());
        var saved = ordemServicoRepository.save(ordemServico);
        if (primeiroServico)
            notificarUseCase.execute(saved);
    }

    public record Input(Long idOs, Long servicoId, UserId userId) {}
}
