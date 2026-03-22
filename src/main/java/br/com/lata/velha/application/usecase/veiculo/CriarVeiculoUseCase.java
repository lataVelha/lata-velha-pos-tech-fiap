package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.application.assembler.VeiculoAssembler;
import br.com.lata.velha.application.dto.request.VeiculoRequest;
import br.com.lata.velha.application.dto.response.VeiculoResponse;
import br.com.lata.velha.domain.exception.ProprietarioNotFoundException;
import br.com.lata.velha.domain.exception.ResourceAlreadyExistsException;
import br.com.lata.velha.domain.model.Veiculo;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import br.com.lata.velha.domain.valueObject.Placa;
import org.springframework.stereotype.Service;

@Service
public class CriarVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoAssembler assembler;

    public CriarVeiculoUseCase(VeiculoRepository veiculoRepository,
                               ProprietarioRepository proprietarioRepository,
                               VeiculoAssembler assembler) {
        this.veiculoRepository = veiculoRepository;
        this.proprietarioRepository = proprietarioRepository;
        this.assembler = assembler;
    }

    public VeiculoResponse execute(VeiculoRequest request) {
        proprietarioRepository.buscarPorId(request.proprietarioId())
                .orElseThrow(() -> new ProprietarioNotFoundException(request.proprietarioId()));

        Placa placa = Placa.of(request.placa());
        if (veiculoRepository.existePorPlaca(placa.getValor())) {
            throw new ResourceAlreadyExistsException("Já existe um veículo com a placa: " + placa.getFormatado());
        }

        Veiculo salvo = veiculoRepository.salvar(assembler.toDomain(request));
        return assembler.toResponse(salvo);
    }
}