package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.application.assemblers.PecaAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
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
import static org.mockito.Mockito.*;

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

        when(repository.getActiveById(1L)).thenReturn(peca);
        when(assembler.toResponse(peca)).thenReturn(response);

        var result = useCase.execute(1L);

        assertEquals(1L, result.id());
        assertEquals("Disco de freio", result.nome());
        verify(repository).getActiveById(1L);
        verify(assembler).toResponse(peca);
    }

    @Test
    @DisplayName("Deve falhar ao buscar peça inexistente")
    void deveFalharAoBuscarPecaInexistente() {
        when(repository.getActiveById(99L)).thenThrow(new IllegalArgumentException("Peça não encontrada"));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(99L));
        verify(assembler, never()).toResponse(any());
    }
}
