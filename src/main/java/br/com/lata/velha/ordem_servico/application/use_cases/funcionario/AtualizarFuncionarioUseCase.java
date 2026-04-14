package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarFuncionarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.authentication.domain.exceptions.InactiveUserException;
import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.repositories.CargoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AtualizarFuncionarioUseCase {
    private final FuncionarioRepository funcionarioRepository;
    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;

    public Output execute(Input input) {
        var funcionario = funcionarioRepository.getById(input.id());
        if(!userRepository.isAtivoById(funcionario.getUserId()))
            throw InactiveUserException.fromEntityName("Funcionário");

        Cargo cargo = cargoRepository.getById(input.cargoId());
        funcionario.update(input.nome(), cargo);

        var saved = funcionarioRepository.save(funcionario);
        return Output.fromEntity(saved);
    }

    public record Input(Long id, String nome, Long cargoId) {}

    public record Output(Long id, String nome, String cargo, UserId userId) {
        public static Output fromEntity(Funcionario funcionario) {
            return new Output(
                    funcionario.getId(),
                    funcionario.getNome(),
                    funcionario.getCargo().getNome(),
                    funcionario.getUserId()
            );
        }
    }
}