package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.domain.exception.VeiculoNotFoundException;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeletarVeiculoUseCase {

    private final VeiculoRepository repository;

    public void execute(Long id) {
        repository.buscarPorId(id)
                .orElseThrow(() -> new VeiculoNotFoundException(id));
        repository.deletar(id);
    }
}