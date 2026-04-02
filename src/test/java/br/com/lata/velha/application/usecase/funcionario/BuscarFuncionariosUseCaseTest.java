package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.application.assembler.FuncionarioAssembler;
import br.com.lata.velha.application.assembler.PaginatedAssembler;
import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.application.dto.response.PaginatedResponse;
import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarFuncionariosUseCaseTest {

    @Mock
    private FuncionarioRepository repository;

    @Mock
    private FuncionarioAssembler assembler;

    @Test
    @DisplayName("Deve listar funcionarios ativos de forma paginada")
    void deveListarFuncionariosAtivosDeFormaPaginada() {
        var useCase = new BuscarFuncionariosUseCase(repository, assembler, new PaginatedAssembler());

        var funcionario1 = new Funcionario(1L, "Fulano", "fulano", null, new Cargo(1L, "MECANICO", null), true);
        var funcionario2 = new Funcionario(2L, "Beltrano", "beltrano", null, new Cargo(2L, "ATENDENTE", null), true);

        var page = new PaginatedResult<>(List.of(funcionario1, funcionario2), 0, 10, 2, 1);

        when(repository.findAllActivePaginated(0, 10)).thenReturn(page);
        when(assembler.toResponse(funcionario1)).thenReturn(
            new FuncionarioResponse(1L, "Fulano", "fulano", true, "MECANICO")
        );
        when(assembler.toResponse(funcionario2)).thenReturn(
            new FuncionarioResponse(2L, "Beltrano", "beltrano", true, "ATENDENTE")
        );

        PaginatedResponse<FuncionarioResponse> result = useCase.execute(0, 10);

        assertEquals(2, result.content().size());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(2, result.totalElements());
        verify(repository).findAllActivePaginated(0, 10);
    }
}
