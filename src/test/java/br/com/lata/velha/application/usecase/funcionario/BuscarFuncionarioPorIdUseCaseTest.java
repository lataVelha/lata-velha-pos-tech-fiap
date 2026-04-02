package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.application.assembler.FuncionarioAssembler;
import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarFuncionarioPorIdUseCaseTest {

    @Mock
    private FuncionarioRepository repository;

    @Mock
    private FuncionarioAssembler assembler;

    @InjectMocks
    private BuscarFuncionarioPorIdUseCase useCase;

    @Test
    @DisplayName("Deve buscar funcionario ativo por ID com sucesso")
    void deveBuscarFuncionarioAtivoPorIdComSucesso() {
        var funcionario = new Funcionario(1L, "Fulano", "fulano", null, new Cargo(1L, "MECANICO", null), true);
        var response = new FuncionarioResponse(1L, "Fulano", "fulano", true, "MECANICO");

        when(repository.findActiveById(1L)).thenReturn(funcionario);
        when(assembler.toResponse(funcionario)).thenReturn(response);

        var result = useCase.execute(1L);

        assertEquals(1L, result.id());
        assertEquals("Fulano", result.nome());
        verify(repository).findActiveById(1L);
        verify(assembler).toResponse(funcionario);
    }

    @Test
    @DisplayName("Deve falhar ao buscar funcionario inexistente")
    void deveFalharAoBuscarFuncionarioInexistente() {
        when(repository.findActiveById(99L)).thenThrow(new IllegalArgumentException("Funcionario nao encontrado"));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(99L));
        verify(assembler, never()).toResponse(any());
    }
}
