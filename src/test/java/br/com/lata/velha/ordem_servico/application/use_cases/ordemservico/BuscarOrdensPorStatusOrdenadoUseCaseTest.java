package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarOrdensPorStatusOrdenadoUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @InjectMocks
    private BuscarOrdensPorStatusOrdenadoUseCase useCase;

    private OrdemServicoProjection buildProjection(Long id, String status) {
        return new OrdemServicoProjection() {
            public Long getId() { return id; }
            public Long getAtendenteInicioId() { return 10L; }
            public String getAtendenteNome() { return "Ana"; }
            public Long getVeiculoId() { return 40L; }
            public String getVeiculoDescricao() { return "Honda Civic"; }
            public Long getProprietarioId() { return 30L; }
            public String getProprietarioNome() { return "João"; }
            public Long getMecanicoFinalId() { return 20L; }
            public String getMecanicoNome() { return "Carlos"; }
            public String getStatus() { return status; }
            public String getReclamacaoProprietario() { return "Barulho ao frear"; }
            public LocalDateTime getIniciadoEm() { return null; }
            public LocalDateTime getFinalizadoEm() { return null; }
            public LocalDateTime getEntregueEm() { return null; }
            public LocalDateTime getAtualizadoEm() { return null; }
            public String getServicos() { return "[]"; }
        };
    }

    private PaginatedResult<OrdemServicoProjection> paginatedResultOf(List<OrdemServicoProjection> projections) {
        return new PaginatedResult<>(projections, 0, 10, projections.size(), 1);
    }

    private PaginatedResult<OrdemServicoProjection> paginatedResultOf(List<OrdemServicoProjection> projections, int page, int size, long total) {
        int totalPages = (int) Math.ceil((double) total / size);
        return new PaginatedResult<>(projections, page, size, total, totalPages);
    }

    // ------------------------------------------------------------------

    @Test
    @DisplayName("deve retornar lista paginada quando há OS")
    void deveRetornarListaPaginadaComOs() {
        var projection = buildProjection(1L, "EM_EXECUCAO");
        when(ordemServicoRepository.findOrderedByStatusPriority(anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of(projection)));

        var result = useCase.execute(0, 10);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("deve retornar lista vazia quando não há OS")
    void deveRetornarListaVaziaQuandoNaoHaOs() {
        when(ordemServicoRepository.findOrderedByStatusPriority(anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        var result = useCase.execute(0, 10);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }

    @Test
    @DisplayName("deve passar page e size corretos para o repositório")
    void devePassarPageSizeCorreto() {
        when(ordemServicoRepository.findOrderedByStatusPriority(eq(2), eq(5)))
                .thenReturn(paginatedResultOf(List.of(), 2, 5, 0L));

        useCase.execute(2, 5);

        verify(ordemServicoRepository)
                .findOrderedByStatusPriority(eq(2), eq(5));
    }

    @Test
    @DisplayName("deve retornar metadados de paginação corretamente")
    void deveRetornarMetadadosDePaginacao() {
        var projections = List.of(
                buildProjection(1L, "EM_EXECUCAO"),
                buildProjection(2L, "AGUARDANDO_APROVACAO")
        );
        when(ordemServicoRepository.findOrderedByStatusPriority(anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(projections, 0, 10, 25L));

        var result = useCase.execute(0, 10);

        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(25L);
        assertThat(result.totalPages()).isEqualTo(3);
        assertThat(result.content()).hasSize(2);
    }

    @Test
    @DisplayName("deve mapear campos do projection no response")
    void deveMapearCamposDoProjectionNoResponse() {
        var projection = buildProjection(99L, "EM_DIAGNOSTICO");
        when(ordemServicoRepository.findOrderedByStatusPriority(anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of(projection)));

        var result = useCase.execute(0, 10);

        var response = result.content().get(0);
        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.status()).isEqualTo("EM_DIAGNOSTICO");
        assertThat(response.reclamacaoProprietario()).isEqualTo("Barulho ao frear");
        assertThat(response.atendente().nome()).isEqualTo("Ana");
        assertThat(response.mecanico().nome()).isEqualTo("Carlos");
        assertThat(response.proprietario().nome()).isEqualTo("João");
        assertThat(response.veiculo().descricao()).isEqualTo("Honda Civic");
    }

    @Test
    @DisplayName("deve retornar múltiplas OS na lista")
    void deveRetornarMultiplasOs() {
        var projections = List.of(
                buildProjection(1L, "EM_EXECUCAO"),
                buildProjection(2L, "AGUARDANDO_APROVACAO"),
                buildProjection(3L, "EM_DIAGNOSTICO")
        );
        when(ordemServicoRepository.findOrderedByStatusPriority(anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(projections));

        var result = useCase.execute(0, 10);

        assertThat(result.content()).hasSize(3);
        assertThat(result.content()).extracting("status")
                .containsExactly("EM_EXECUCAO", "AGUARDANDO_APROVACAO", "EM_DIAGNOSTICO");
    }

    @Test
    @DisplayName("deve chamar repositório com parâmetros corretos na página 1")
    void devePassarParametrosCorretosPagina1() {
        when(ordemServicoRepository.findOrderedByStatusPriority(eq(1), eq(20)))
                .thenReturn(paginatedResultOf(List.of(), 1, 20, 50L));

        useCase.execute(1, 20);

        verify(ordemServicoRepository)
                .findOrderedByStatusPriority(eq(1), eq(20));
    }

    @Test
    @DisplayName("deve retornar resultado com página 0 e tamanho 10 por padrão")
    void deveRetornarComParametrosPadrao() {
        when(ordemServicoRepository.findOrderedByStatusPriority(eq(0), eq(10)))
                .thenReturn(paginatedResultOf(List.of()));

        var result = useCase.execute(0, 10);

        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        verify(ordemServicoRepository)
                .findOrderedByStatusPriority(eq(0), eq(10));
    }
}

