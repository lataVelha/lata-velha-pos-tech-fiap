package br.com.lata.velha.ordemDeServico.application.useCases.funcionario;

import br.com.lata.velha.ordemDeServico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.ordemDeServico.domain.repositories.FuncionarioRepository;
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