package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntradaPecaEstoqueUseCaseTest {

    @Mock
    private PecaRepository pecaRepository;

    @Mock
    private PecaEstoqueRepository pecaEstoqueRepository;

    @Mock
    private PecaAlocadaRepository pecaAlocadaRepository;

    @InjectMocks
    private EntradaPecaEstoqueUseCase useCase;

    @Test
    void deveAdicionarQuantidadeQuandoEstoqueJaExiste() {
        PecaEstoque estoque = new PecaEstoque(1L, 10, 10);
        MovimentarPecaEstoqueRequest request = new MovimentarPecaEstoqueRequest(5);

        when(pecaRepository.existsActiveById(1L)).thenReturn(true);
        when(pecaEstoqueRepository.findByPecaId(1L)).thenReturn(Optional.of(estoque));
        when(pecaEstoqueRepository.save(any(PecaEstoque.class))).thenAnswer(i -> i.getArgument(0));
        when(pecaAlocadaRepository.buscarPendentesPorPecaOrdenado(1L)).thenReturn(List.of());

        PecaEstoqueResponse response = useCase.execute(1L, request);

        assertThat(response.quantidadeArmazenada()).isEqualTo(15);
        assertThat(response.quantidadeDisponivel()).isEqualTo(15);
    }

    @Test
    void deveCriarEstoqueQuandoNaoExiste() {
        MovimentarPecaEstoqueRequest request = new MovimentarPecaEstoqueRequest(5);

        when(pecaRepository.existsActiveById(1L)).thenReturn(true);
        when(pecaEstoqueRepository.findByPecaId(1L)).thenReturn(Optional.empty());
        when(pecaEstoqueRepository.save(any(PecaEstoque.class))).thenAnswer(i -> i.getArgument(0));
        when(pecaAlocadaRepository.buscarPendentesPorPecaOrdenado(1L)).thenReturn(List.of());

        PecaEstoqueResponse response = useCase.execute(1L, request);

        assertThat(response.quantidadeArmazenada()).isEqualTo(5);
        assertThat(response.quantidadeDisponivel()).isEqualTo(5);
    }

    @Test
    void deveMovimentarReservasPendentesAoAdicionar() {
        PecaEstoque estoque = new PecaEstoque(1L, 0, 0);
        MovimentarPecaEstoqueRequest request = new MovimentarPecaEstoqueRequest(10);
        PecaAlocada pendente = new PecaAlocada(5L, 1L, 99L, 4, 0, 4, StatusPecaAlocada.PARCIAL, LocalDateTime.now());

        when(pecaRepository.existsActiveById(1L)).thenReturn(true);
        when(pecaEstoqueRepository.findByPecaId(1L)).thenReturn(Optional.of(estoque));
        when(pecaEstoqueRepository.save(any(PecaEstoque.class))).thenAnswer(i -> i.getArgument(0));
        when(pecaAlocadaRepository.buscarPendentesPorPecaOrdenado(1L)).thenReturn(List.of(pendente));
        when(pecaAlocadaRepository.save(any(PecaAlocada.class))).thenAnswer(i -> i.getArgument(0));

        PecaEstoqueResponse response = useCase.execute(1L, request);

        assertThat(response.quantidadeArmazenada()).isEqualTo(10);
        assertThat(response.quantidadeDisponivel()).isEqualTo(6);
        assertThat(pendente.getStatus()).isEqualTo(StatusPecaAlocada.RESERVADA);
    }
}
