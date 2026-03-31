package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.application.assembler.FuncionarioAssembler;
import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarFuncionarioPorIdUseCase {

    private final FuncionarioRepository repository;
    private final FuncionarioAssembler assembler;

    public FuncionarioResponse execute(Long id) {
        return repository.findById(id)
                .map(assembler::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));
    }
}