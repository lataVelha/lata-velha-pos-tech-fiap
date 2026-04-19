package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.assemblers.PecaEstoqueAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.request.AjustarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import br.com.lata.velha.shared.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AjustarPecaEstoqueUseCaseTest {

    @Mock
    private PecaRepository pecaRepository;

    @Mock
    private PecaEstoqueRepository pecaEstoqueRepository;

    @Mock
    private PecaEstoqueAssembler assembler;

    @InjectMocks
    private AjustarPecaEstoqueUseCase useCase;

    @Test
    void deveAjustarSaldoComSucesso() {
        PecaEstoque estoque = new PecaEstoque(1L, 10, 10);
        AjustarPecaEstoqueRequest request = new AjustarPecaEstoqueRequest(4, 4);

        when(pecaRepository.existsActiveById(1L)).thenReturn(true);
        when(pecaEstoqueRepository.findByPecaId(1L)).thenReturn(Optional.of(estoque));
        when(pecaEstoqueRepository.save(any(PecaEstoque.class))).thenAnswer(i -> i.getArgument(0));
        when(assembler.toResponse(any(PecaEstoque.class))).thenAnswer(i -> {
            PecaEstoque e = i.getArgument(0);
            return new PecaEstoqueResponse(e.getPecaId(), e.getQuantidadeArmazenada(), e.getQuantidadeDisponivel());
        });

        PecaEstoqueResponse response = useCase.execute(1L, request);

        assertThat(response.quantidadeArmazenada()).isEqualTo(4);
        assertThat(response.quantidadeDisponivel()).isEqualTo(4);
    }

    @Test
    void deveCriarEstoqueQuandoNaoExiste() {
        AjustarPecaEstoqueRequest request = new AjustarPecaEstoqueRequest(10, 5);

        when(pecaRepository.existsActiveById(1L)).thenReturn(true);
        when(pecaEstoqueRepository.findByPecaId(1L)).thenReturn(Optional.empty());
        when(pecaEstoqueRepository.save(any(PecaEstoque.class))).thenAnswer(i -> i.getArgument(0));
        when(assembler.toResponse(any(PecaEstoque.class))).thenAnswer(i -> {
            PecaEstoque e = i.getArgument(0);
            return new PecaEstoqueResponse(e.getPecaId(), e.getQuantidadeArmazenada(), e.getQuantidadeDisponivel());
        });

        PecaEstoqueResponse response = useCase.execute(1L, request);

        assertThat(response.quantidadeArmazenada()).isEqualTo(10);
        assertThat(response.quantidadeDisponivel()).isEqualTo(5);
    }

    @Test
    void deveLancarExcecaoQuandoPecaNaoExiste() {
        AjustarPecaEstoqueRequest request = new AjustarPecaEstoqueRequest(10, 5);

        when(pecaRepository.existsActiveById(99L)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(99L, request))
                .isInstanceOf(NotFoundException.class);
    }
}
