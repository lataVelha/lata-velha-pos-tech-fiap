package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.application.assembler.FuncionarioAssembler;
import br.com.lata.velha.application.dto.request.CadastrarFuncionarioRequest;
import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.domain.valueObjects.Credential;
import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.CargoRepository;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarFuncionarioUseCaseTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private FuncionarioAssembler assembler;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private CadastrarFuncionarioUseCase useCase;

    @Test
    @DisplayName("Deve cadastrar funcionário com sucesso")
    void deveCadastrarFuncionarioComSucesso() {
        // Arrange
        var request = new CadastrarFuncionarioRequest("Fulano", "fulano", "Senha123!", 1L);
        var cargo = new Cargo(1L, "MECANICO", null);
        var domain = new Funcionario(null, "Fulano", "fulano", null, cargo, true);
        var savedDomain = new Funcionario(10L, "Fulano", "fulano", null, cargo, true);
        var response = new FuncionarioResponse(10L, "Fulano", "fulano", true, "MECANICO");

        when(cargoRepository.findById(1L)).thenReturn(Optional.of(cargo));
        when(passwordHasher.hashSenha(any())).thenReturn("hash");
        when(assembler.toDomain(eq(request), eq(cargo), any(Credential.class))).thenReturn(domain);
        when(funcionarioRepository.save(domain)).thenReturn(savedDomain);
        when(assembler.toResponse(savedDomain)).thenReturn(response);

        // Act
        var result = useCase.execute(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.nome()).isEqualTo("Fulano");
        assertThat(result.cargoNome()).isEqualTo("MECANICO");
        verify(funcionarioRepository).save(any(Funcionario.class));
    }

    @Test
    @DisplayName("Deve falhar ao tentar cadastrar funcionário com cargo inexistente")
    void deveFalharAoCriarFuncionarioComCargoInexistente() {
        // Arrange
        var request = new CadastrarFuncionarioRequest("Fulano", "fulano", "Senha123!", 99L);
        when(cargoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(request), "Cargo não encontrado");
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }
}
