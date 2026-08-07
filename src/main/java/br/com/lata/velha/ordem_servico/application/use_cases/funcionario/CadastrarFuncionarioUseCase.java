package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.application.gateways.authentication.AuthenticationService;
import br.com.lata.velha.ordem_servico.application.gateways.authentication.dtos.CreateAuthUserDto;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class CadastrarFuncionarioUseCase {

    private final CadastrarFuncionarioGateway gateway;
    private final AuthenticationService authService;

    public CadastrarFuncionarioUseCase(CadastrarFuncionarioGateway gateway, AuthenticationService authService) {
        this.gateway = gateway;
        this.authService = authService;
    }

    public Funcionario execute(Input input) {
        var cargo = gateway.getCargoPorId(input.cargoId());
        var userId = createUser(input);
        var funcionario = Funcionario.create(input.nome(), cargo, userId);
        return gateway.salvarFuncionario(funcionario);
    }

    private UserId createUser(Input input) {
        var roles = authService.getRolesForCargo(input.cargoId());
        var createUserDto = new CreateAuthUserDto(input.username(), input.senha(), roles, input.cpf());
        var userResponse = authService.createUser(createUserDto);
        return UserId.create(userResponse.userId());
    }

    public record Input(String nome, String username, String senha, Long cargoId, String cpf) {}
}
