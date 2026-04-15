package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.application.ports.authentication.AuthenticationService;
import br.com.lata.velha.ordem_servico.application.ports.authentication.dtos.CreateAuthUserDto;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.repositories.CargoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CadastrarFuncionarioUseCase {
    private final FuncionarioRepository funcionarioRepository;
    private final CargoRepository cargoRepository;
    private final AuthenticationService authService;

    public Output execute(Input input) {
        var cargo = cargoRepository.getById(input.cargoId());
        var userId = createUser(input);
        var funcionario = Funcionario.create(input.nome(), cargo, userId);
        var saved = funcionarioRepository.save(funcionario);
        return Output.fromDomain(saved);
    }

    private UserId createUser(Input input) {
        var roles = authService.getRolesForCargo(input.cargoId());
        var createUserDto = new CreateAuthUserDto(input.username, input.senha, roles);
        var userResponse = authService.createUser(createUserDto);
        return UserId.create(userResponse.userId());
    }

    public record Input(String nome, String username, String senha, Long cargoId) {};

    public record Output(Long id, String nome, String cargo, UUID userId) {
        public static Output fromDomain(Funcionario entity) {
            return new Output(
                    entity.getId(),
                    entity.getNome(),
                    entity.getCargo().getNome(),
                    entity.getUserId().getValue()
            );
        }
    }
}
