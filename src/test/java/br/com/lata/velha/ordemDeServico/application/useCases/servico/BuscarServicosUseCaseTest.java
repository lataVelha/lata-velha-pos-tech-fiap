package br.com.lata.velha.ordemDeServico.application.useCases.servico;

import br.com.lata.velha.ordemDeServico.application.assemblers.PaginatedAssembler;
import br.com.lata.velha.ordemDeServico.application.assemblers.ServicoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordemDeServico.domain.entities.Servico;
import br.com.lata.velha.ordemDeServico.domain.repositories.ServicoRepository;
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
class BuscarServicosUseCaseTest {

    @Mock
    private ServicoRepository repository;

    @Mock
    private ServicoAssembler assembler;

    @Test
    @DisplayName("Deve listar serviços ativos de forma paginada")
    void deveListarServicosAtivosDeFormaPaginada() {
        var useCase = new BuscarServicosUseCase(repository, assembler, new PaginatedAssembler());

        var servico1 = new Servico(1L, "Balanceamento", "Balanceamento das rodas", true);
        var servico2 = new Servico(2L, "Troca de óleo", "Substituição do óleo", true);

        var page = new PaginatedResult<>(List.of(servico1, servico2), 0, 10, 2, 1);

        when(repository.findAllActivePaginated(0, 10)).thenReturn(page);
        when(assembler.toResponse(servico1)).thenReturn(
            new ServicoResponse(1L, "Balanceamento", "Balanceamento das rodas")
        );
        when(assembler.toResponse(servico2)).thenReturn(
            new ServicoResponse(2L, "Troca de óleo", "Substituição do óleo")
        );

        PaginatedResult<ServicoResponse> result = useCase.execute(0, 10);

        assertEquals(2, result.content().size());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(2, result.totalElements());
        verify(repository).findAllActivePaginated(0, 10);
    }
}
