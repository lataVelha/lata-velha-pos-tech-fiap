package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarFuncionarioPorIdUseCase {

    private final FuncionarioRepository repository;

    public FuncionarioResponse execute(Long id) {
        var funcionario = repository.getById(id);
        return FuncionarioResponse.from(funcionario);
    }
}