package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.application.dto.request.AtualizarFuncionarioRequest;
import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.domain.entities.Cargo;
import br.com.lata.velha.domain.entities.Funcionario;
import br.com.lata.velha.domain.repository.CargoRepository;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import br.com.lata.velha.shared.domain.valueObjects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarFuncionarioUseCaseTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CargoRepository cargoRepository;

    @InjectMocks
    private AtualizarFuncionarioUseCase useCase;

    @Test
    @DisplayName("Deve atualizar funcionario com sucesso")
    void deveAtualizarFuncionarioComSucesso() {
        var request = new AtualizarFuncionarioRequest("Novo Nome", 2L);
        var cargoNovo = new Cargo(2L, "ATENDENTE", null);
        UserId userId = UserId.random();
        var funcionario = new Funcionario(1L, "Nome Antigo", new Cargo(1L, "MECANICO", null), userId);

        when(funcionarioRepository.getById(1L)).thenReturn(funcionario);
        when(userRepository.isAtivoById(userId)).thenReturn(true);
        when(cargoRepository.getById(2L)).thenReturn(cargoNovo);
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);

        var result = useCase.execute(1L, request);

        assertEquals("Novo Nome", funcionario.getNome());
        assertEquals("ATENDENTE", funcionario.getCargo().getNome());
        assertEquals("Novo Nome", result.nome());
        verify(funcionarioRepository).save(funcionario);
    }

    @Test
    @DisplayName("Deve falhar quando cargo nao existir")
    void deveFalharQuandoCargoNaoExistir() {
        var request = new AtualizarFuncionarioRequest("Nome", 99L);
        UserId userId = UserId.random();
        var funcionario = new Funcionario(1L, "Nome", new Cargo(1L, "MECANICO", null), userId);

        when(funcionarioRepository.getById(1L)).thenReturn(funcionario);
        when(userRepository.isAtivoById(userId)).thenReturn(true);
        when(cargoRepository.getById(99L)).thenThrow(new IllegalArgumentException("Cargo não encontrado"));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L, request));
        verify(funcionarioRepository, never()).save(funcionario);
    }
}
