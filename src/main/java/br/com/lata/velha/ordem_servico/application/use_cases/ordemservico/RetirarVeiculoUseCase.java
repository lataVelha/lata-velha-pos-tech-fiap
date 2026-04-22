package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RetirarVeiculoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    public void execute(Long idOs, UserId userId) {
        var ordemServico = ordemServicoRepository.getById(idOs);
        var funcionario = funcionarioRepository.getByUserId(userId);
        ordemServico.entregar(funcionario.getId());
        ordemServicoRepository.save(ordemServico);
        notificarUseCase.execute(ordemServico);
    }
}