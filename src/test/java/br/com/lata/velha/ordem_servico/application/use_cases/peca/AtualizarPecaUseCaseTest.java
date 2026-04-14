package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.application.assemblers.PecaAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarPecaRequest;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarPecaUseCaseTest {

    @Mock
    private PecaRepository repository;

    @Mock
    private PecaAssembler assembler;

    @InjectMocks
    private AtualizarPecaUseCase useCase;

    @Test
    @DisplayName("Deve atualizar peça com sucesso")
    void deveAtualizarPecaComSucesso() {
        var request = new AtualizarPecaRequest("Pastilha premium", "Pastilha cerâmica", new BigDecimal("180.00"));
        var peca = new Peca(1L, "Pastilha", "Pastilha comum", new BigDecimal("130.00"), true);
        var response = new PecaResponse(1L, "Pastilha premium", "Pastilha cerâmica", new BigDecimal("180.00"), true);

        when(repository.findActiveById(1L)).thenReturn(peca);
        when(repository.save(peca)).thenReturn(peca);
        when(assembler.toResponse(peca)).thenReturn(response);

        var result = useCase.execute(1L, request);

        assertEquals("Pastilha premium", peca.getNome());
        assertEquals("Pastilha cerâmica", peca.getDescricao());
        assertEquals(new BigDecimal("180.00"), peca.getValor());
        assertEquals("Pastilha premium", result.nome());
        verify(repository).save(peca);
    }

    @Test
    @DisplayName("Deve falhar quando peça não existir")
    void deveFalharQuandoPecaNaoExistir() {
        var request = new AtualizarPecaRequest("Nome", "Descrição", new BigDecimal("50.00"));
        when(repository.findActiveById(99L)).thenThrow(new IllegalArgumentException("Peça não encontrada"));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(99L, request));
        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
