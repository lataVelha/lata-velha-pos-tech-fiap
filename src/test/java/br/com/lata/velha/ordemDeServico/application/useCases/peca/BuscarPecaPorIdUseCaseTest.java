package br.com.lata.velha.ordemDeServico.application.useCases.peca;

import br.com.lata.velha.ordemDeServico.application.assemblers.PecaAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.PecaResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Peca;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarPecaPorIdUseCaseTest {

    @Mock
    private PecaRepository repository;

    @Mock
    private PecaAssembler assembler;

    @InjectMocks
    private BuscarPecaPorIdUseCase useCase;

    @Test
    @DisplayName("Deve buscar peça ativa por ID com sucesso")
    void deveBuscarPecaAtivaPorIdComSucesso() {
        var peca = new Peca(1L, "Disco de freio", "Disco dianteiro", new BigDecimal("220.00"), true);
        var response = new PecaResponse(1L, "Disco de freio", "Disco dianteiro", new BigDecimal("220.00"), true);

        when(repository.findActiveById(1L)).thenReturn(peca);
        when(assembler.toResponse(peca)).thenReturn(response);

        var result = useCase.execute(1L);

        assertEquals(1L, result.id());
        assertEquals("Disco de freio", result.nome());
        verify(repository).findActiveById(1L);
        verify(assembler).toResponse(peca);
    }

    @Test
    @DisplayName("Deve falhar ao buscar peça inexistente")
    void deveFalharAoBuscarPecaInexistente() {
        when(repository.findActiveById(99L)).thenThrow(new IllegalArgumentException("Peça não encontrada"));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(99L));
        verify(assembler, never()).toResponse(any());
    }
}
