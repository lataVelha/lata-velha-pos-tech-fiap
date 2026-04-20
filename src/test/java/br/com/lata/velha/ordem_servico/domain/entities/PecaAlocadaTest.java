package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PecaAlocadaTest {

    private PecaAlocada buildPeca(int solicitada, int reservada, int encomendada, StatusPecaAlocada status) {
        return new PecaAlocada(1L, 10L, 99L, solicitada, reservada, encomendada, status, LocalDateTime.now());
    }

    @Test
    void deveCriarPecaAlocadaComDadosValidos() {
        PecaAlocada peca = buildPeca(5, 0, 0, StatusPecaAlocada.ORCAMENTO);

        assertThat(peca.getId()).isEqualTo(1L);
        assertThat(peca.getPecaId()).isEqualTo(10L);
        assertThat(peca.getExecucaoServicoId()).isEqualTo(99L);
        assertThat(peca.getQuantidadeSolicitada()).isEqualTo(5);
        assertThat(peca.getStatus()).isEqualTo(StatusPecaAlocada.ORCAMENTO);
    }

    @Test
    void deveCriarPecaAlocadaViaFactoryMethod() {
        PecaAlocada peca = PecaAlocada.create(10L, 99L, 3);

        assertThat(peca.getId()).isNull();
        assertThat(peca.getPecaId()).isEqualTo(10L);
        assertThat(peca.getExecucaoServicoId()).isEqualTo(99L);
        assertThat(peca.getQuantidadeSolicitada()).isEqualTo(3);
        assertThat(peca.getQuantidadeReservada()).isZero();
        assertThat(peca.getQuantidadeEncomendada()).isZero();
        assertThat(peca.getStatus()).isEqualTo(StatusPecaAlocada.ORCAMENTO);
    }

    @Test
    void deveFalharQuandoPecaIdForNulo() {
        var now = LocalDateTime.now();
        assertThatThrownBy(() ->
                new PecaAlocada(1L, null, 99L, 5, 0, 0, StatusPecaAlocada.ORCAMENTO, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Peça obrigatória");
    }

    @Test
    void deveFalharQuandoExecucaoServicoIdForNulo() {
        var now = LocalDateTime.now();
        assertThatThrownBy(() ->
                new PecaAlocada(1L, 10L, null, 5, 0, 0, StatusPecaAlocada.ORCAMENTO, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Serviço obrigatório");
    }

    @Test
    void deveFalharQuandoQuantidadeSolicitadaForInvalida() {
        var now = LocalDateTime.now();
        assertThatThrownBy(() ->
                new PecaAlocada(1L, 10L, 99L, 0, 0, 0, StatusPecaAlocada.ORCAMENTO, now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantidade inválida");
    }

    @Test
    void deveReservarTotalmenteQuandoEstoqueEhSuficiente() {
        PecaAlocada peca = buildPeca(3, 0, 0, StatusPecaAlocada.ORCAMENTO);
        PecaEstoque estoque = new PecaEstoque(10L, 10, 10);

        peca.reservar(estoque);

        assertThat(peca.getQuantidadeReservada()).isEqualTo(3);
        assertThat(peca.getQuantidadeEncomendada()).isZero();
        assertThat(peca.getStatus()).isEqualTo(StatusPecaAlocada.RESERVADA);
        assertThat(estoque.getQuantidadeDisponivel()).isEqualTo(7);
    }

    @Test
    void deveReservarParcialmenteQuandoEstoqueEhInsuficiente() {
        PecaAlocada peca = buildPeca(5, 0, 0, StatusPecaAlocada.ORCAMENTO);
        PecaEstoque estoque = new PecaEstoque(10L, 3, 3);

        peca.reservar(estoque);

        assertThat(peca.getQuantidadeReservada()).isEqualTo(3);
        assertThat(peca.getQuantidadeEncomendada()).isEqualTo(2);
        assertThat(peca.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
        assertThat(estoque.getQuantidadeDisponivel()).isZero();
    }

    @Test
    void deveEncomendarTudoQuandoEstoqueEhNulo() {
        PecaAlocada peca = buildPeca(4, 0, 0, StatusPecaAlocada.ORCAMENTO);

        peca.reservar(null);

        assertThat(peca.getQuantidadeReservada()).isZero();
        assertThat(peca.getQuantidadeEncomendada()).isEqualTo(4);
        assertThat(peca.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
    }

    @Test
    void deveEncomendarTudoQuandoEstoqueZero() {
        PecaAlocada peca = buildPeca(4, 0, 0, StatusPecaAlocada.ORCAMENTO);
        PecaEstoque estoque = new PecaEstoque(10L, 0, 0);

        peca.reservar(estoque);

        assertThat(peca.getQuantidadeReservada()).isZero();
        assertThat(peca.getQuantidadeEncomendada()).isEqualTo(4);
        assertThat(peca.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
    }

    @Test
    void deveRegistrarInstalacao() {
        PecaAlocada peca = buildPeca(3, 3, 0, StatusPecaAlocada.RESERVADA);

        peca.instalada(3);

        assertThat(peca.getQuantidadeReservada()).isZero();
        assertThat(peca.getStatus()).isEqualTo(StatusPecaAlocada.INSTALADA);
    }

    @Test
    void deveRegistrarInstalacaoParcial() {
        PecaAlocada peca = buildPeca(5, 5, 0, StatusPecaAlocada.RESERVADA);

        peca.instalada(2);

        assertThat(peca.getQuantidadeReservada()).isEqualTo(3);
        assertThat(peca.getStatus()).isEqualTo(StatusPecaAlocada.RESERVADA);
    }

    @Test
    void deveFalharInstalacaoQuandoQuantidadeInvalida() {
        PecaAlocada peca = buildPeca(3, 3, 0, StatusPecaAlocada.RESERVADA);

        assertThatThrownBy(() -> peca.instalada(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantidade inválida");

        assertThatThrownBy(() -> peca.instalada(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantidade inválida");
    }

    @Test
    void deveFalharInstalacaoQuandoReservadaInsuficiente() {
        PecaAlocada peca = buildPeca(5, 2, 0, StatusPecaAlocada.RESERVADA);

        assertThatThrownBy(() -> peca.instalada(3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Quantidade reservada insuficiente para instalar");
    }

    @Test
    void deveRetornarTotalmenteReservadaQuandoCompleto() {
        PecaAlocada peca = buildPeca(3, 3, 0, StatusPecaAlocada.RESERVADA);

        assertThat(peca.totalmenteReservada()).isTrue();
    }

    @Test
    void deveRetornarFalsoParaTotalmenteReservadaQuandoParcial() {
        PecaAlocada peca = buildPeca(5, 3, 2, StatusPecaAlocada.PARCIAL);

        assertThat(peca.totalmenteReservada()).isFalse();
    }

    @Test
    void deveRetornarIsProcessadaQuandoTotalmenteReservada() {
        PecaAlocada peca = buildPeca(3, 3, 0, StatusPecaAlocada.RESERVADA);

        assertThat(peca.isProcessada()).isTrue();
    }

    @Test
    void deveRetornarProcessadaQuandoParcialmenteReservada() {
        PecaAlocada peca = buildPeca(5, 3, 0, StatusPecaAlocada.PARCIAL);

        assertThat(peca.isProcessada()).isTrue();
    }

    @Test
    void deveImplementarEqualsEHashCodePeloId() {
        PecaAlocada a = buildPeca(3, 0, 0, StatusPecaAlocada.ORCAMENTO);
        PecaAlocada b = new PecaAlocada(1L, 20L, 88L, 2, 0, 0, StatusPecaAlocada.RESERVADA, LocalDateTime.now());
        PecaAlocada c = new PecaAlocada(2L, 10L, 99L, 3, 0, 0, StatusPecaAlocada.ORCAMENTO, LocalDateTime.now());

        assertThat(a)
                .isEqualTo(b)
                .isNotEqualTo(c);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void naodeveEqualQuandoComparadoComOutroTipo() {
        PecaAlocada peca = buildPeca(3, 0, 0, StatusPecaAlocada.ORCAMENTO);

        assertThat(peca).isNotEqualTo("string");
    }

    @Test
    void deveRetornarAtualizado() {
        PecaAlocada peca = buildPeca(3, 0, 0, StatusPecaAlocada.ORCAMENTO);

        assertThat(peca.getAtualizado()).isNotNull();
    }
}
