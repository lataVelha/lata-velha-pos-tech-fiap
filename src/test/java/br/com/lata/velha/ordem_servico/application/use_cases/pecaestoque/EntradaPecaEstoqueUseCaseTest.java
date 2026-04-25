package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.ExecucaoServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntradaPecaEstoqueUseCaseTest {

    @Mock private PecaRepository pecaRepository;
    @Mock private PecaEstoqueRepository pecaEstoqueRepository;
    @Mock private PecaAlocadaRepository pecaAlocadaRepository;
    @Mock private ExecucaoServicoRepository execucaoServicoRepository;

    @InjectMocks
    private EntradaPecaEstoqueUseCase useCase;

    private static final Long PECA_ID = 1L;
    private static final Long EXECUCAO_ID = 99L;

    private PecaAlocada pendente(StatusPecaAlocada status, int solicitada, int reservada, int encomendada) {
        return new PecaAlocada(5L, PECA_ID, EXECUCAO_ID, new BigDecimal("10.00"),
                solicitada, reservada, encomendada, 0, status, LocalDateTime.now());
    }

    private ExecucaoServico execucaoComPeca(StatusExecucaoServico execStatus, PecaAlocada peca) {
        return new ExecucaoServico(EXECUCAO_ID, 10L, 1L, execStatus, new BigDecimal("100.00"),
                new HashSet<>(Set.of(peca)), null, null, null, null, LocalDateTime.now());
    }

    @Test
    @DisplayName("deve adicionar quantidade quando estoque já existe")
    void deveAdicionarQuantidadeQuandoEstoqueJaExiste() {
        PecaEstoque estoque = new PecaEstoque(PECA_ID, 10, 10);

        when(pecaRepository.existsActiveById(PECA_ID)).thenReturn(true);
        when(pecaEstoqueRepository.findByPecaId(PECA_ID)).thenReturn(Optional.of(estoque));
        when(pecaEstoqueRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(pecaAlocadaRepository.buscarPendentesPorPecaOrdenado(PECA_ID)).thenReturn(List.of());

        var response = useCase.execute(PECA_ID, new MovimentarPecaEstoqueRequest(5));

        assertThat(response.quantidadeArmazenada()).isEqualTo(15);
        assertThat(response.quantidadeDisponivel()).isEqualTo(15);
        verifyNoInteractions(execucaoServicoRepository);
    }

    @Test
    @DisplayName("deve criar estoque quando não existe")
    void deveCriarEstoqueQuandoNaoExiste() {
        when(pecaRepository.existsActiveById(PECA_ID)).thenReturn(true);
        when(pecaEstoqueRepository.findByPecaId(PECA_ID)).thenReturn(Optional.empty());
        when(pecaEstoqueRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(pecaAlocadaRepository.buscarPendentesPorPecaOrdenado(PECA_ID)).thenReturn(List.of());

        var response = useCase.execute(PECA_ID, new MovimentarPecaEstoqueRequest(5));

        assertThat(response.quantidadeArmazenada()).isEqualTo(5);
        assertThat(response.quantidadeDisponivel()).isEqualTo(5);
        verifyNoInteractions(execucaoServicoRepository);
    }

    @Test
    @DisplayName("deve reservar peça pendente através da execução e reduzir estoque disponível")
    void deveReservarPecaPendentePelaExecucao() {
        PecaEstoque estoque = new PecaEstoque(PECA_ID, 0, 0);
        var pecaNaExecucao = pendente(StatusPecaAlocada.ENCOMENDA, 4, 0, 4);
        var execucao = execucaoComPeca(StatusExecucaoServico.AGUARDANDO_PECA, pecaNaExecucao);

        when(pecaRepository.existsActiveById(PECA_ID)).thenReturn(true);
        when(pecaEstoqueRepository.findByPecaId(PECA_ID)).thenReturn(Optional.of(estoque));
        when(pecaEstoqueRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(pecaAlocadaRepository.buscarPendentesPorPecaOrdenado(PECA_ID))
                .thenReturn(List.of(pendente(StatusPecaAlocada.ENCOMENDA, 4, 0, 4)));
        when(execucaoServicoRepository.getAllByIdWithPeca(Set.of(EXECUCAO_ID)))
                .thenReturn(Set.of(execucao));
        when(execucaoServicoRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        var response = useCase.execute(PECA_ID, new MovimentarPecaEstoqueRequest(10));

        assertThat(response.quantidadeDisponivel()).isEqualTo(6);
        assertThat(pecaNaExecucao.getStatus()).isEqualTo(StatusPecaAlocada.RESERVADA);
        assertThat(pecaNaExecucao.getQuantidadeReservada()).isEqualTo(4);
        verify(execucaoServicoRepository).saveAll(any());
    }

    @Test
    @DisplayName("deve mudar status da execução para APROVADO quando todas as peças ficam reservadas")
    void deveAtualizarStatusExecucaoParaAprovadoQuandoTodasPecasReservadas() {
        PecaEstoque estoque = new PecaEstoque(PECA_ID, 0, 0);
        var pecaNaExecucao = pendente(StatusPecaAlocada.ENCOMENDA, 3, 0, 3);
        var execucao = execucaoComPeca(StatusExecucaoServico.AGUARDANDO_PECA, pecaNaExecucao);

        when(pecaRepository.existsActiveById(PECA_ID)).thenReturn(true);
        when(pecaEstoqueRepository.findByPecaId(PECA_ID)).thenReturn(Optional.of(estoque));
        when(pecaEstoqueRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(pecaAlocadaRepository.buscarPendentesPorPecaOrdenado(PECA_ID))
                .thenReturn(List.of(pendente(StatusPecaAlocada.ENCOMENDA, 3, 0, 3)));
        when(execucaoServicoRepository.getAllByIdWithPeca(Set.of(EXECUCAO_ID)))
                .thenReturn(Set.of(execucao));
        when(execucaoServicoRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        useCase.execute(PECA_ID, new MovimentarPecaEstoqueRequest(5));

        assertThat(execucao.getStatus()).isEqualTo(StatusExecucaoServico.APROVADO);
    }

    @Test
    @DisplayName("deve manter status AGUARDANDO_PECA quando peça fica apenas parcialmente reservada")
    void deveMantereStatusQuandoPecaFicaParcial() {
        PecaEstoque estoque = new PecaEstoque(PECA_ID, 0, 0);
        var pecaNaExecucao = pendente(StatusPecaAlocada.ENCOMENDA, 5, 0, 5);
        var execucao = execucaoComPeca(StatusExecucaoServico.AGUARDANDO_PECA, pecaNaExecucao);

        when(pecaRepository.existsActiveById(PECA_ID)).thenReturn(true);
        when(pecaEstoqueRepository.findByPecaId(PECA_ID)).thenReturn(Optional.of(estoque));
        when(pecaEstoqueRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(pecaAlocadaRepository.buscarPendentesPorPecaOrdenado(PECA_ID))
                .thenReturn(List.of(pendente(StatusPecaAlocada.ENCOMENDA, 5, 0, 5)));
        when(execucaoServicoRepository.getAllByIdWithPeca(Set.of(EXECUCAO_ID)))
                .thenReturn(Set.of(execucao));
        when(execucaoServicoRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        useCase.execute(PECA_ID, new MovimentarPecaEstoqueRequest(2));

        assertThat(pecaNaExecucao.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
        assertThat(execucao.getStatus()).isEqualTo(StatusExecucaoServico.AGUARDANDO_PECA);
    }

    @Test
    @DisplayName("deve parar de processar pendentes quando estoque esgota")
    void devePararDeProcessarQuandoEstoqueAcaba() {
        PecaEstoque estoque = new PecaEstoque(PECA_ID, 0, 0);

        var peca1NaExecucao = pendente(StatusPecaAlocada.ENCOMENDA, 3, 0, 3);
        var execucao1 = execucaoComPeca(StatusExecucaoServico.AGUARDANDO_PECA, peca1NaExecucao);
        var execucao1Id = EXECUCAO_ID;

        var peca2NaExecucao = new PecaAlocada(6L, PECA_ID, 100L, new BigDecimal("10.00"),
                2, 0, 2, 0, StatusPecaAlocada.ENCOMENDA, LocalDateTime.now());
        var execucao2 = new ExecucaoServico(100L, 10L, 1L, StatusExecucaoServico.AGUARDANDO_PECA,
                new BigDecimal("100.00"), new HashSet<>(Set.of(peca2NaExecucao)), null, null, null, null, LocalDateTime.now());

        var pendente1 = pendente(StatusPecaAlocada.ENCOMENDA, 3, 0, 3);
        var pendente2 = new PecaAlocada(6L, PECA_ID, 100L, new BigDecimal("10.00"),
                2, 0, 2, 0, StatusPecaAlocada.ENCOMENDA, LocalDateTime.now());

        when(pecaRepository.existsActiveById(PECA_ID)).thenReturn(true);
        when(pecaEstoqueRepository.findByPecaId(PECA_ID)).thenReturn(Optional.of(estoque));
        when(pecaEstoqueRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(pecaAlocadaRepository.buscarPendentesPorPecaOrdenado(PECA_ID))
                .thenReturn(List.of(pendente1, pendente2));
        when(execucaoServicoRepository.getAllByIdWithPeca(Set.of(execucao1Id, 100L)))
                .thenReturn(Set.of(execucao1, execucao2));
        when(execucaoServicoRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

        useCase.execute(PECA_ID, new MovimentarPecaEstoqueRequest(3));

        assertThat(peca1NaExecucao.getStatus()).isEqualTo(StatusPecaAlocada.RESERVADA);
        assertThat(peca2NaExecucao.getStatus()).isEqualTo(StatusPecaAlocada.ENCOMENDA);

        verify(execucaoServicoRepository).saveAll(argThat(list -> list.size() == 1));
    }

    @Test
    @DisplayName("não deve consultar execuções quando não há pendentes")
    void naoDeveConsultarExecucoesQuandoNaoHaPendentes() {
        when(pecaRepository.existsActiveById(PECA_ID)).thenReturn(true);
        when(pecaEstoqueRepository.findByPecaId(PECA_ID)).thenReturn(Optional.empty());
        when(pecaEstoqueRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(pecaAlocadaRepository.buscarPendentesPorPecaOrdenado(PECA_ID)).thenReturn(List.of());

        useCase.execute(PECA_ID, new MovimentarPecaEstoqueRequest(10));

        verifyNoInteractions(execucaoServicoRepository);
    }
}
