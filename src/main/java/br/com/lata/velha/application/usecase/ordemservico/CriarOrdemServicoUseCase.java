package br.com.lata.velha.application.usecase.ordemservico;

import br.com.lata.velha.application.assembler.OrdemServicoAssembler;
import br.com.lata.velha.application.dto.request.OrdemServicoRequest;
import br.com.lata.velha.application.dto.response.OrdemServicoResponse;
import br.com.lata.velha.application.usecase.proprietario.BuscarProprietarioPorIdUseCase;
import br.com.lata.velha.application.usecase.veiculo.BuscarVeiculoPorIdUseCase;
import br.com.lata.velha.domain.model.OrdemServico;
import br.com.lata.velha.domain.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriarOrdemServicoUseCase {

    private final OrdemServicoRepository repository;

    private final OrdemServicoAssembler ordemServicoAssembler;

    private final BuscarVeiculoPorIdUseCase buscarVeiculoPorIdUseCase;

    private final BuscarProprietarioPorIdUseCase buscarProprietarioPorIdUseCase;

    private final NotificarOSCriadaUseCase notificarUseCase;


    public OrdemServicoResponse execute(OrdemServicoRequest request) {

        var veiculo = buscarVeiculoPorIdUseCase.execute(request.veiculoId());

        var proprietario = buscarProprietarioPorIdUseCase.execute(request.proprietarioId());

        OrdemServico os = new OrdemServico(
                null,
                request.proprietarioId(),
                request.veiculoId(),
                request.reclamacaoCliente(),
                request.atendenteInicioId()
        );

        OrdemServico saved = repository.save(os);

        notificarUseCase.execute(saved, proprietario.nome(), proprietario.email(),
                        veiculo.marca() + " - " + veiculo.modelo());

        return ordemServicoAssembler.toResponse(os, proprietario.nome(), veiculo.modelo());
    }
}