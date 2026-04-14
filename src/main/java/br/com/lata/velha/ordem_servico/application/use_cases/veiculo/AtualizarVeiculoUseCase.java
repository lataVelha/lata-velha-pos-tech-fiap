package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.assemblers.VeiculoAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
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