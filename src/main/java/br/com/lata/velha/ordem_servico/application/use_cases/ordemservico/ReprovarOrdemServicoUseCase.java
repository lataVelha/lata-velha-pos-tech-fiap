package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReprovarOrdemServicoUseCase {
    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    @Transactional
    public void execute(Input input) {
        var ordemServico = ordemServicoRepository.getByIdWithExecucoesAndPecas(input.osId());
        var funcionario = funcionarioRepository.getByUserId(input.userId());

        ordemServico.getExecucaoServicos().forEach(execucaoServico ->
            execucaoServico.recusar(funcionario.getId())
        );
        ordemServico.reprovar(funcionario.getId());
        ordemServicoRepository.save(ordemServico);
        notificarUseCase.execute(ordemServico);
    }

    public record Input(Long osId, UserId userId) {}
}
