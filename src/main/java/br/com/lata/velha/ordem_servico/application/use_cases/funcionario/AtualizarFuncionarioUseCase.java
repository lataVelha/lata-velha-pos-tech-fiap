package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarFuncionarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.authentication.domain.exceptions.InactiveUserException;
import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.repositories.CargoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AtualizarFuncionarioUseCase {
    private final FuncionarioRepository funcionarioRepository;
    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;

    public FuncionarioResponse execute(Long id, AtualizarFuncionarioRequest request) {
        var funcionario = funcionarioRepository.getById(id);
        if(!userRepository.isAtivoById(funcionario.getUserId()))
            throw InactiveUserException.fromEntityName("Funcionário");

        Cargo cargo = cargoRepository.getById(request.cargoId());

        funcionario.setNome(request.nome());
        funcionario.setCargo(cargo);

        Funcionario saved = funcionarioRepository.save(funcionario);
        return FuncionarioResponse.fromEntity(saved);
    }
}