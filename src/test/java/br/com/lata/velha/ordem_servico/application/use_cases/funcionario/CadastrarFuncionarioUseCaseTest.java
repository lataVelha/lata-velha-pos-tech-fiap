package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.application.gateways.authentication.AuthenticationService;
import br.com.lata.velha.ordem_servico.application.gateways.authentication.dtos.CreateAuthUserResponseDto;
import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.CargoNotFoundException;
import br.com.lata.velha.ordem_servico.domain.repositories.CargoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarFuncionarioUseCaseTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private AuthenticationService authService;

    @InjectMocks
    private CadastrarFuncionarioUseCase useCase;

    @Test
    @DisplayName("Deve cadastrar funcionário com sucesso")
    void deveCadastrarFuncionarioComSucesso() {
        var input = new CadastrarFuncionarioUseCase.Input("Fulano", "fulano@example.com", "Senha123!", 1L);
        var cargo = new Cargo(1L, "MECANICO", null);
        var userId = UserId.random();
        var savedDomain = new Funcionario(10L, "Fulano", cargo, userId);

        when(cargoRepository.getById(1L)).thenReturn(cargo);
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(savedDomain);
        when(authService.getRolesForCargo(1L)).thenReturn(Set.of());
        when(authService.createUser(any())).thenReturn(new CreateAuthUserResponseDto(userId.getValue()));

        var result = useCase.execute(input);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.nome()).isEqualTo("Fulano");
        assertThat(result.cargo()).isEqualTo("MECANICO");
        assertThat(result.userId()).isEqualTo(userId.getValue());
        verify(funcionarioRepository).save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceAlreadyExistsException ao cadastrar email já existente")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        var input = new CadastrarFuncionarioUseCase.Input("Fulano", "fulano@example.com", "Senha123!", 1L);

        when(authService.getRolesForCargo(1L)).thenReturn(Set.of());
        when(authService.createUser(any())).thenThrow(new ResourceAlreadyExistsException(""));

        assertThrows(ResourceAlreadyExistsException.class, () -> useCase.execute(input));
        verify(funcionarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar ao tentar cadastrar funcionário com cargo inexistente")
    void deveFalharAoCriarFuncionarioComCargoInexistente() {
        var input = new CadastrarFuncionarioUseCase.Input("Fulano", "fulano@example.com", "Senha123!", 99L);

        when(cargoRepository.getById(99L)).thenThrow(CargoNotFoundException.fromId(99L));

        assertThrows(CargoNotFoundException.class, () -> useCase.execute(input));
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }
}
