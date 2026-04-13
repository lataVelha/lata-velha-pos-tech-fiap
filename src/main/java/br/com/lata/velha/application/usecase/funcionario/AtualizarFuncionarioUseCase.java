package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.application.dto.request.AtualizarFuncionarioRequest;
import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.authentication.domain.exceptions.InactiveUserException;
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
    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;

    public FuncionarioResponse execute(Long id, AtualizarFuncionarioRequest request) {
        var funcionario = funcionarioRepository.getById(id);
        if(!userRepository.isAtivoById(funcionario.getUserId()))
            throw InactiveUserException.fromFuncionario(funcionario);

        Cargo cargo = cargoRepository.getById(request.cargoId());

        funcionario.setNome(request.nome());
        funcionario.setCargo(cargo);

        Funcionario saved = funcionarioRepository.save(funcionario);
        return FuncionarioResponse.fromEntity(saved);
    }
}