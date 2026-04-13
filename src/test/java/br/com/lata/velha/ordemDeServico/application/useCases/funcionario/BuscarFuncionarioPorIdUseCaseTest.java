package br.com.lata.velha.ordemDeServico.application.useCases.funcionario;

import br.com.lata.velha.ordemDeServico.domain.entities.Cargo;
import br.com.lata.velha.ordemDeServico.domain.entities.Funcionario;
import br.com.lata.velha.ordemDeServico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.shared.domain.valueObjects.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarFuncionarioPorIdUseCaseTest {

    @Mock
    private FuncionarioRepository repository;

    @InjectMocks
    private BuscarFuncionarioPorIdUseCase useCase;

    @Test
    @DisplayName("Deve buscar funcionario ativo por ID com sucesso")
    void deveBuscarFuncionarioAtivoPorIdComSucesso() {
        var funcionario = new Funcionario(1L, "Fulano", new Cargo(1L, "MECANICO", null), UserId.random());

        when(repository.getById(1L)).thenReturn(funcionario);

        var result = useCase.execute(1L);

        assertEquals(1L, result.id());
        assertEquals("Fulano", result.nome());
        assertEquals("MECANICO", result.cargo());
        verify(repository).getById(1L);
    }

    @Test
    @DisplayName("Deve falhar ao buscar funcionario inexistente")
    void deveFalharAoBuscarFuncionarioInexistente() {
        when(repository.getById(99L)).thenThrow(new IllegalArgumentException("Funcionario nao encontrado"));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(99L));
    }
}
