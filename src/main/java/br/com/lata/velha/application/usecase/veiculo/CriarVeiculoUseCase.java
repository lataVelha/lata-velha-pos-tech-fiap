package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.application.assembler.VeiculoAssembler;
import br.com.lata.velha.application.dto.request.VeiculoRequest;
import br.com.lata.velha.application.dto.response.VeiculoResponse;
import br.com.lata.velha.domain.entities.Veiculo;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriarVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoAssembler assembler;

    public VeiculoResponse execute(VeiculoRequest request) {
        proprietarioRepository.findActiveById(request.proprietarioId());

        Veiculo saved = veiculoRepository.save(assembler.toDomain(request));
        return assembler.toResponse(saved);
    }
}