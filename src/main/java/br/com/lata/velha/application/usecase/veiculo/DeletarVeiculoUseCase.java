package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.domain.exception.VeiculoNotFoundException;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class DeletarVeiculoUseCase {

    private final VeiculoRepository repository;

    public DeletarVeiculoUseCase(VeiculoRepository repository) {
        this.repository = repository;
    }

    public void execute(Long id) {
        repository.buscarPorId(id)
                .orElseThrow(() -> new VeiculoNotFoundException(id));
        repository.deletar(id);
    }
}