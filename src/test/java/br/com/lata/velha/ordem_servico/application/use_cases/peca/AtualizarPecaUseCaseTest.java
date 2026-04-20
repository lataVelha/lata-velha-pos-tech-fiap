package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarPecaRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarPecaUseCaseTest {

    @Mock
    private PecaRepository repository;

    @InjectMocks
    private AtualizarPecaUseCase useCase;

    @Test
    @DisplayName("Deve atualizar peça com sucesso")
    void deveAtualizarPecaComSucesso() {
        var request = new AtualizarPecaRequest("Pastilha premium", "Pastilha cerâmica", new BigDecimal("180.00"));
        var peca = new Peca(1L, "Pastilha", "Pastilha comum", new BigDecimal("130.00"), true);

        when(repository.getActiveById(1L)).thenReturn(peca);
        when(repository.save(peca)).thenReturn(peca);

        var result = useCase.execute(1L, request);

        assertThat(peca.getNome()).isEqualTo("Pastilha premium");
        assertThat(peca.getDescricao()).isEqualTo("Pastilha cerâmica");
        assertThat(peca.getValor()).isEqualByComparingTo("180.00");
        assertThat(result.nome()).isEqualTo("Pastilha premium");
        verify(repository).save(peca);
    }

    @Test
    @DisplayName("Deve falhar quando peça não existir")
    void deveFalharQuandoPecaNaoExistir() {
        var request = new AtualizarPecaRequest("Nome", "Descrição", new BigDecimal("50.00"));
        when(repository.getActiveById(99L)).thenThrow(new IllegalArgumentException("Peça não encontrada"));

        assertThatThrownBy(() -> useCase.execute(99L, request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(repository, never()).save(any());
    }
}
