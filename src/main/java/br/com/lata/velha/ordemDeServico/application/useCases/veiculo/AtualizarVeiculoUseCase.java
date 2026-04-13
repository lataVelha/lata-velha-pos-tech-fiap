package br.com.lata.velha.ordemDeServico.application.useCases.veiculo;

import br.com.lata.velha.ordemDeServico.application.assemblers.VeiculoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Veiculo;
import br.com.lata.velha.ordemDeServico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordemDeServico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AtualizarVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoAssembler assembler;

    public VeiculoResponse execute(Long id, VeiculoRequest request) {
        Veiculo existing = veiculoRepository.findActiveById(id);
        proprietarioRepository.findActiveById(request.proprietarioId());

        assembler.updateDomain(existing, request);
        Veiculo saved = veiculoRepository.save(existing);
        return assembler.toResponse(saved);
    }
}