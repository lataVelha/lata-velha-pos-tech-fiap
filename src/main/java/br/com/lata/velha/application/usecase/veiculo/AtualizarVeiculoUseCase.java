package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.application.assembler.VeiculoAssembler;
import br.com.lata.velha.application.dto.request.VeiculoRequest;
import br.com.lata.velha.application.dto.response.VeiculoResponse;
import br.com.lata.velha.domain.exception.ProprietarioNotFoundException;
import br.com.lata.velha.domain.exception.VeiculoNotFoundException;
import br.com.lata.velha.domain.model.Veiculo;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AtualizarVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoAssembler assembler;

    public VeiculoResponse execute(Long id, VeiculoRequest request) {
        Veiculo existente = veiculoRepository.buscarPorId(id)
                .orElseThrow(() -> new VeiculoNotFoundException(id));
        proprietarioRepository.buscarPorId(request.proprietarioId())
                .orElseThrow(() -> new ProprietarioNotFoundException(request.proprietarioId()));

        assembler.updateDomain(existente, request);
        Veiculo salvo = veiculoRepository.salvar(existente);
        return assembler.toResponse(salvo);
    }
}