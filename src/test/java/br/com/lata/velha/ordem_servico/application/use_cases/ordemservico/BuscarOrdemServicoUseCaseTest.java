package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
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
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarOrdemServicoUseCaseTest {

    @Mock
    private OrdemServicoRepository ordemServicoRepository;

    @InjectMocks
    private BuscarOrdemServicoUseCase useCase;

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
        var projection = buildProjection(1L, "RECEBIDA");
        when(ordemServicoRepository.findByAllOrdemSevico(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of(projection)));

        var result = useCase.execute(null, null, null, null, 0, 10);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).id()).isEqualTo(1L);
        assertThat(result.content().get(0).status()).isEqualTo("RECEBIDA");
    }

    @Test
    @DisplayName("deve retornar lista vazia quando não há OS")
    void deveRetornarListaVaziaQuandoNaoHaOs() {
        when(ordemServicoRepository.findByAllOrdemSevico(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        var result = useCase.execute(null, null, null, null, 0, 10);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }

    @Test
    @DisplayName("deve converter status enum para string ao chamar o repositório")
    void deveConverterStatusEnumParaStringNoRepositorio() {
        when(ordemServicoRepository.findByAllOrdemSevico(any(), eq("APROVADA"), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        useCase.execute(null, StatusOrdemServico.APROVADA, null, null, 0, 10);

        verify(ordemServicoRepository)
                .findByAllOrdemSevico(isNull(), eq("APROVADA"), isNull(), isNull(), eq(0), eq(10));
    }

    @Test
    @DisplayName("deve passar null ao repositório quando status é null")
    void devePassarNullAoRepositorioQuandoStatusEhNull() {
        when(ordemServicoRepository.findByAllOrdemSevico(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        useCase.execute(null, null, null, null, 0, 10);

        verify(ordemServicoRepository)
                .findByAllOrdemSevico(isNull(), isNull(), isNull(), isNull(), eq(0), eq(10));
    }

    @Test
    @DisplayName("deve filtrar por id quando informado")
    void deveFiltrarPorId() {
        when(ordemServicoRepository.findByAllOrdemSevico(eq(5L), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        useCase.execute(5L, null, null, null, 0, 10);

        verify(ordemServicoRepository)
                .findByAllOrdemSevico(eq(5L), isNull(), isNull(), isNull(), eq(0), eq(10));
    }

    @Test
    @DisplayName("deve filtrar por proprietarioId quando informado")
    void deveFiltrarPorProprietarioId() {
        when(ordemServicoRepository.findByAllOrdemSevico(any(), any(), eq(30L), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        useCase.execute(null, null, 30L, null, 0, 10);

        verify(ordemServicoRepository)
                .findByAllOrdemSevico(isNull(), isNull(), eq(30L), isNull(), eq(0), eq(10));
    }

    @Test
    @DisplayName("deve filtrar por mecanicoId quando informado")
    void deveFiltrarPorMecanicoId() {
        when(ordemServicoRepository.findByAllOrdemSevico(any(), any(), any(), eq(20L), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        useCase.execute(null, null, null, 20L, 0, 10);

        verify(ordemServicoRepository)
                .findByAllOrdemSevico(isNull(), isNull(), isNull(), eq(20L), eq(0), eq(10));
    }

    @Test
    @DisplayName("deve retornar metadados de paginação corretamente")
    void deveRetornarMetadadosDePaginacao() {
        var projections = List.of(
                buildProjection(1L, "RECEBIDA"),
                buildProjection(2L, "APROVADA")
        );
        when(ordemServicoRepository.findByAllOrdemSevico(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(projections, 0, 10, 25L));

        var result = useCase.execute(null, null, null, null, 0, 10);

        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(25L);
        assertThat(result.totalPages()).isEqualTo(3);
        assertThat(result.content()).hasSize(2);
    }

    @Test
    @DisplayName("deve passar page e size corretos para o repositório")
    void devePassarPageSizeCorreto() {
        when(ordemServicoRepository.findByAllOrdemSevico(any(), any(), any(), any(), eq(2), eq(5)))
                .thenReturn(paginatedResultOf(List.of(), 2, 5, 0L));

        useCase.execute(null, null, null, null, 2, 5);

        verify(ordemServicoRepository)
                .findByAllOrdemSevico(isNull(), isNull(), isNull(), isNull(), eq(2), eq(5));
    }

    @Test
    @DisplayName("deve mapear campos do projection no response")
    void deveMapearCamposDoProjectionNoResponse() {
        var projection = buildProjection(99L, "EM_DIAGNOSTICO");
        when(ordemServicoRepository.findByAllOrdemSevico(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of(projection)));

        var result = useCase.execute(null, null, null, null, 0, 10);

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
                buildProjection(1L, "RECEBIDA"),
                buildProjection(2L, "APROVADA"),
                buildProjection(3L, "FINALIZADA")
        );
        when(ordemServicoRepository.findByAllOrdemSevico(any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(projections));

        var result = useCase.execute(null, null, null, null, 0, 10);

        assertThat(result.content()).hasSize(3);
        assertThat(result.content()).extracting("status")
                .containsExactly("RECEBIDA", "APROVADA", "FINALIZADA");
    }

    @Test
    @DisplayName("deve filtrar combinando id, status, proprietarioId e mecanicoId")
    void deveFiltrarComTodosOsParametros() {
        when(ordemServicoRepository.findByAllOrdemSevico(eq(1L), eq("EM_EXECUCAO"), eq(30L), eq(20L), anyInt(), anyInt()))
                .thenReturn(paginatedResultOf(List.of()));

        useCase.execute(1L, StatusOrdemServico.EM_EXECUCAO, 30L, 20L, 0, 10);

        verify(ordemServicoRepository)
                .findByAllOrdemSevico(eq(1L), eq("EM_EXECUCAO"), eq(30L), eq(20L), eq(0), eq(10));
    }
}
