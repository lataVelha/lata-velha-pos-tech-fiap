package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.authentication.domain.entities.User;
import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.domain.exception.notFoundExceptions.FuncionarioNotFoundException;
import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import br.com.lata.velha.shared.domain.valueObjects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesativarFuncionarioUseCaseTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DesativarFuncionarioUseCase useCase;

    @Test
    @DisplayName("Deve desativar funcionario ativo com sucesso")
    void deveDesativarFuncionarioAtivoComSucesso() {
        UserId userId = UserId.random();
        var funcionario = new Funcionario(1L, "Fulano", new Cargo(1L, "MECANICO", null), userId);
        var user = new User(userId, "fulano", null, null, Set.of(), true, LocalDateTime.now(), null);

        when(funcionarioRepository.getById(1L)).thenReturn(funcionario);
        when(userRepository.getById(userId)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        useCase.execute(1L);

        assertFalse(user.isAtivo());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deve lançar FuncionarioNotFoundException para id inexistente")
    void deveLancarExcecaoParaFuncionarioInexistente() {
        when(funcionarioRepository.getById(99L))
                .thenThrow(FuncionarioNotFoundException.fromId(99L));

        assertThrows(FuncionarioNotFoundException.class, () -> useCase.execute(99L));
        verify(userRepository, never()).getById(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve desativar usuário vinculado ao funcionario")
    void deveDesativarUsuarioVinculadoAoFuncionario() {
        UserId userId = UserId.random();
        var funcionario = new Funcionario(2L, "Ciclano", new Cargo(1L, "ATENDENTE", null), userId);
        var user = new User(userId, "ciclano", null, null, Set.of(), true, LocalDateTime.now(), null);

        when(funcionarioRepository.getById(2L)).thenReturn(funcionario);
        when(userRepository.getById(userId)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        useCase.execute(2L);

        verify(funcionarioRepository).getById(2L);
        verify(userRepository).getById(userId);
        verify(userRepository).save(user);
        assertFalse(user.isAtivo());
    }
}
