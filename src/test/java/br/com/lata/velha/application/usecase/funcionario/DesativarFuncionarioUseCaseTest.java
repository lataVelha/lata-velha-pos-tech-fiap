package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DesativarFuncionarioUseCaseTest {

    @Mock
    private FuncionarioRepository repository;

    @InjectMocks
    private DesativarFuncionarioUseCase useCase;

    @Test
    @DisplayName("Deve desativar funcionario ativo com sucesso")
    void deveDesativarFuncionarioAtivoComSucesso() {
        var funcionario = new Funcionario(1L, "Fulano", "fulano", null, new Cargo(1L, "MECANICO", null), true);
        when(repository.findActiveById(1L)).thenReturn(funcionario);

        useCase.execute(1L);

        assertFalse(funcionario.isAtivo());
        verify(repository).save(funcionario);
    }

    @Test
    @DisplayName("Deve falhar quando funcionario ja estiver desativado")
    void deveFalharQuandoFuncionarioJaEstiverDesativado() {
        var funcionario = new Funcionario(1L, "Fulano", "fulano", null, new Cargo(1L, "MECANICO", null), false);
        when(repository.findActiveById(1L)).thenReturn(funcionario);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(1L));
        verify(repository, never()).save(funcionario);
    }
}
