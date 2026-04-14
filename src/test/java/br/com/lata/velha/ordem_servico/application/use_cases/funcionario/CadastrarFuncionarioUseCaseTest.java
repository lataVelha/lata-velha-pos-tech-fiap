package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarFuncionarioRequest;
import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.CargoNotFoundException;
import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.repositories.CargoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarFuncionarioUseCaseTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private CadastrarFuncionarioUseCase useCase;

    @Test
    @DisplayName("Deve cadastrar funcionário com sucesso")
    void deveCadastrarFuncionarioComSucesso() {
        var request = new CadastrarFuncionarioRequest("Fulano", "fulano@example.com", "Senha123!", 1L);
        var cargo = new Cargo(1L, "MECANICO", null);
        var savedDomain = new Funcionario(10L, "Fulano", cargo, UserId.random());

        when(cargoRepository.getByIdWithRoles(1L)).thenReturn(cargo);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordHasher.hashSenha(any())).thenReturn("hash");
        when(userRepository.save(any())).thenReturn(null);
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(savedDomain);

        var result = useCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.nome()).isEqualTo("Fulano");
        assertThat(result.cargo()).isEqualTo("MECANICO");
        verify(funcionarioRepository).save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve lançar ResourceAlreadyExistsException ao cadastrar email já existente")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        var request = new CadastrarFuncionarioRequest("Fulano", "fulano@example.com", "Senha123!", 1L);

        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> useCase.execute(request));
        verify(funcionarioRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar ao tentar cadastrar funcionário com cargo inexistente")
    void deveFalharAoCriarFuncionarioComCargoInexistente() {
        var request = new CadastrarFuncionarioRequest("Fulano", "fulano@example.com", "Senha123!", 99L);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(cargoRepository.getByIdWithRoles(99L)).thenThrow(CargoNotFoundException.fromId(99L));

        assertThrows(CargoNotFoundException.class, () -> useCase.execute(request));
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }
}
