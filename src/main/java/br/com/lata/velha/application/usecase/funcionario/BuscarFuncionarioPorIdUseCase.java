package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarFuncionarioPorIdUseCase {

    private final FuncionarioRepository repository;

    public FuncionarioResponse execute(Long id) {
        var funcionario = repository.getById(id);
        return FuncionarioResponse.fromEntity(funcionario);
    }
}