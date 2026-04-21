package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IniciarDiagnosticoUseCase {
    private final OrdemServicoRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    public void execute(Input input) {
        var ordemServico = repository.getById(input.idOs());
        var mecanico = funcionarioRepository.getByUserId(input.userId());
        ordemServico.iniciarDiagnostico(mecanico.getId());
        var saved = repository.save(ordemServico);
        notificarUseCase.execute(saved);
    }

    public record Input(Long idOs, UserId userId) {}
}