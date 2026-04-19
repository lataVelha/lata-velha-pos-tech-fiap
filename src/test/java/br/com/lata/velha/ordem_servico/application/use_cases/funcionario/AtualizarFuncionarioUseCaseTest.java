package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.authentication.domain.exceptions.InactiveUserException;
import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarFuncionarioRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Cargo;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.FuncionarioNotFoundException;
import br.com.lata.velha.ordem_servico.domain.repositories.CargoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
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
    @DisplayName("Deve atualizar funcionário com sucesso")
    void shouldUpdateFuncionarioSuccessfully() {
        var request = new AtualizarFuncionarioRequest("Novo Nome", 2L);
        var cargoNovo = new Cargo(2L, "ATENDENTE", null);
        UserId userId = UserId.random();
        var funcionario = new Funcionario(1L, "Nome Antigo", new Cargo(1L, "MECANICO", null), userId);

        when(funcionarioRepository.getById(1L)).thenReturn(funcionario);
        when(userRepository.isAtivoById(userId)).thenReturn(true);
        when(cargoRepository.getById(2L)).thenReturn(cargoNovo);
        when(funcionarioRepository.save(funcionario)).thenReturn(funcionario);

        var result = useCase.execute(request.toUpdateUseCaseInput(1L));

        assertEquals("Novo Nome", funcionario.getNome());
        assertEquals("ATENDENTE", funcionario.getCargo().getNome());
        assertEquals("Novo Nome", result.nome());
        assertEquals(userId, result.userId());
        verify(funcionarioRepository).save(funcionario);
    }

    @Test
    @DisplayName("Deve falhar quando cargo nao existir")
    void shouldFailWhenCargoNotFound() {
        var request = new AtualizarFuncionarioRequest("Nome", 99L);
        var input = request.toUpdateUseCaseInput(1L);
        UserId userId = UserId.random();
        var funcionario = new Funcionario(1L, "Nome", new Cargo(1L, "MECANICO", null), userId);

        when(funcionarioRepository.getById(1L)).thenReturn(funcionario);
        when(userRepository.isAtivoById(userId)).thenReturn(true);
        when(cargoRepository.getById(99L)).thenThrow(new IllegalArgumentException("Cargo não encontrado"));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(input));
        verify(funcionarioRepository, never()).save(funcionario);
    }

    @Test
    @DisplayName("Deve lançar InactiveUserException quando usuário estiver inativo")
    void shouldThrowInactiveUserExceptionWhenUserIsInactive() {
        var request = new AtualizarFuncionarioRequest("Novo Nome", 2L);
        var input = request.toUpdateUseCaseInput(1L);
        UserId userId = UserId.random();
        var funcionario = new Funcionario(1L, "Nome Antigo", new Cargo(1L, "MECANICO", null), userId);

        when(funcionarioRepository.getById(1L)).thenReturn(funcionario);
        when(userRepository.isAtivoById(userId)).thenReturn(false);

        assertThrows(InactiveUserException.class, () -> useCase.execute(input));
        verify(cargoRepository, never()).getById(any());
        verify(funcionarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar exceção quando funcionário não for encontrado")
    void shouldPropagateExceptionWhenFuncionarioNotFound() {
        var request = new AtualizarFuncionarioRequest("Novo Nome", 2L);
        var input = request.toUpdateUseCaseInput(99L);

        when(funcionarioRepository.getById(99L)).thenThrow(FuncionarioNotFoundException.fromId(99L));

        assertThrows(FuncionarioNotFoundException.class, () -> useCase.execute(input));
        verify(userRepository, never()).isAtivoById(any());
        verify(funcionarioRepository, never()).save(any());
    }
}
