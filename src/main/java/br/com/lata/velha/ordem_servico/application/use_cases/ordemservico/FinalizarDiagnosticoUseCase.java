package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class FinalizarDiagnosticoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    public void execute(Input input) {
        var ordemServico = ordemServicoRepository.getByIdWithExecucoesAndPecas(input.idOs());
        if(!funcionarioRepository.existsByUserId(input.userId()))
            throw new IllegalArgumentException("Usuário não é um funcionário");
        ordemServico.finalizarDiagnostico();
        ordemServicoRepository.save(ordemServico);
        notificarUseCase.execute(ordemServico);
    }

    public record Input (Long idOs, UserId userId) {}
}
