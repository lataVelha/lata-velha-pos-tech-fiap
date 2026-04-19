package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.request.OrdemServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.BuscarProprietarioPorIdUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.veiculo.BuscarVeiculoPorIdUseCase;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriarOrdemServicoUseCase {

    private final OrdemServicoRepository repository;
    private final BuscarVeiculoPorIdUseCase buscarVeiculoPorIdUseCase;
    private final BuscarProprietarioPorIdUseCase buscarProprietarioPorIdUseCase;
    private final NotificarOrdemServicoUseCase notificarUseCase;
    private final FuncionarioRepository funcionarioRepository;

    public OrdemServicoResponse execute(OrdemServicoRequest request) {
        var veiculo = buscarVeiculoPorIdUseCase.execute(request.veiculoId());
        var proprietario = buscarProprietarioPorIdUseCase.execute(request.proprietarioId());
        var funcionario = funcionarioRepository.getById(request.atendenteInicioId());

        OrdemServico ordemServico = new OrdemServico(
                null,
                proprietario.id(),
                veiculo.id(),
                request.reclamacaoCliente(),
                funcionario.getId()
        );

        OrdemServico saved = repository.save(ordemServico);
        notificarUseCase.execute(saved);

        return OrdemServicoResponse.from(saved, proprietario.nome(), veiculo.modelo(), null, null, null);
    }
}
