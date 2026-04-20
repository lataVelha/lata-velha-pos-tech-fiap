package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriarOrdemServicoUseCase {
    private final OrdemServicoRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoRepository veiculoRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    public OrdemServicoResponse execute(Input input) {
        var veiculo = veiculoRepository.getActiveById(input.veiculoId());
        var proprietario = proprietarioRepository.getActiveById(input.proprietarioId());
        var funcionario = funcionarioRepository.getById(input.atendenteInicioId());

        var ordemServico = OrdemServico.create(
                proprietario.getId(),
                veiculo.getId(),
                input.reclamacaoCliente(),
                funcionario.getId()
        );

        var saved = repository.save(ordemServico);
        notificarUseCase.execute(saved);

        return OrdemServicoResponse.from(saved,
                funcionario.getNome(),
                null,
                proprietario.getNome(),
                veiculo.getMarca() + " " + veiculo.getModelo(),
                null, null, null);
    }

    public record Input(Long veiculoId, Long proprietarioId, Long atendenteInicioId, String reclamacaoCliente) {}
}
