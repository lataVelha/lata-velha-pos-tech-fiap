package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.application.assembler.FuncionarioAssembler;
import br.com.lata.velha.application.dto.request.AtualizarFuncionarioRequest;
import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.CargoRepository;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AtualizarFuncionarioUseCase {

    private final FuncionarioRepository funcionarioRepository;
    private final CargoRepository cargoRepository;
    private final FuncionarioAssembler assembler;

    public FuncionarioResponse execute(Long id, AtualizarFuncionarioRequest request) {
        Funcionario funcionario = funcionarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));

        Cargo cargo = cargoRepository.findById(request.cargoId())
                .orElseThrow(() -> new IllegalArgumentException("Cargo não encontrado"));

        funcionario.setNome(request.nome());
        funcionario.setUsername(request.username());
        funcionario.setCargo(cargo);

        Funcionario saved = funcionarioRepository.save(funcionario);
        return assembler.toResponse(saved);
    }
}