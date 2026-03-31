package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.application.assembler.FuncionarioAssembler;
import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BuscarFuncionariosUseCase {

    private final FuncionarioRepository repository;
    private final FuncionarioAssembler assembler;

    public List<FuncionarioResponse> execute() {
        return repository.findAll().stream()
                .map(assembler::toResponse)
                .collect(Collectors.toList());
    }
}