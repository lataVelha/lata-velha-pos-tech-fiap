package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PecaAlocadaTest {

    @Test
    void deveCriarComConstrutorCompleto() {
        LocalDateTime atualizado = LocalDateTime.now();
        PecaAlocada pecaAlocada = new PecaAlocada(1L, 2L, 3L, 10, 4, 6, StatusPecaAlocada.PARCIAL, atualizado);

        assertThat(pecaAlocada.getId()).isEqualTo(1L);
        assertThat(pecaAlocada.getPecaId()).isEqualTo(2L);
        assertThat(pecaAlocada.getExecucaoServicoId()).isEqualTo(3L);
        assertThat(pecaAlocada.getQuantidadeSolicitada()).isEqualTo(10);
        assertThat(pecaAlocada.getQuantidadeReservada()).isEqualTo(4);
        assertThat(pecaAlocada.getQuantidadeEncomendada()).isEqualTo(6);
        assertThat(pecaAlocada.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
        assertThat(pecaAlocada.getAtualizado()).isEqualTo(atualizado);
    }

    @Test
    void deveCriarComConstrutorDeTresArgumentosComPadroes() {
        PecaAlocada pecaAlocada = new PecaAlocada(2L, 3L, 5);

        assertThat(pecaAlocada.getPecaId()).isEqualTo(2L);
        assertThat(pecaAlocada.getExecucaoServicoId()).isEqualTo(3L);
        assertThat(pecaAlocada.getQuantidadeSolicitada()).isEqualTo(5);
        assertThat(pecaAlocada.getQuantidadeReservada()).isZero();
        assertThat(pecaAlocada.getQuantidadeEncomendada()).isZero();
        assertThat(pecaAlocada.getStatus()).isEqualTo(StatusPecaAlocada.ORCAMENTO);
        assertThat(pecaAlocada.getAtualizado()).isNotNull();
    }

    @Test
    void deveCriarComConstrutorDeDoisArgumentosComPadroes() {
        PecaAlocada pecaAlocada = new PecaAlocada(2L, 5);

        assertThat(pecaAlocada.getPecaId()).isEqualTo(2L);
        assertThat(pecaAlocada.getExecucaoServicoId()).isNull();
        assertThat(pecaAlocada.getQuantidadeSolicitada()).isEqualTo(5);
        assertThat(pecaAlocada.getQuantidadeReservada()).isZero();
        assertThat(pecaAlocada.getQuantidadeEncomendada()).isZero();
        assertThat(pecaAlocada.getStatus()).isEqualTo(StatusPecaAlocada.ORCAMENTO);
    }

    @Test
    void deveEncomendarTotalQuandoQuantidadeDisponivelForNulaOuInvalida() {
        PecaAlocada pecaAlocada = new PecaAlocada(2L, 3L, 8);

        pecaAlocada.reservar(null);

        assertThat(pecaAlocada.getQuantidadeReservada()).isZero();
        assertThat(pecaAlocada.getQuantidadeEncomendada()).isEqualTo(8);
        assertThat(pecaAlocada.getStatus()).isEqualTo(StatusPecaAlocada.ENCOMENDA);
    }

    @Test
    void deveReservarParcialmenteQuandoNaoHouverQuantidadeSuficiente() {
        PecaAlocada pecaAlocada = new PecaAlocada(2L, 3L, 10);

        pecaAlocada.reservar(4);

        assertThat(pecaAlocada.getQuantidadeReservada()).isEqualTo(4);
        assertThat(pecaAlocada.getQuantidadeEncomendada()).isEqualTo(6);
        assertThat(pecaAlocada.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
    }

    @Test
    void deveReservarTotalmenteQuandoQuantidadeForSuficiente() {
        PecaAlocada pecaAlocada = new PecaAlocada(2L, 3L, 10);

        pecaAlocada.reservar(15);

        assertThat(pecaAlocada.getQuantidadeReservada()).isEqualTo(10);
        assertThat(pecaAlocada.getQuantidadeEncomendada()).isZero();
        assertThat(pecaAlocada.getStatus()).isEqualTo(StatusPecaAlocada.RESERVADA);
        assertThat(pecaAlocada.totalmenteReservada()).isTrue();
    }

    @Test
    void deveMovimentarParaReservadoParcial() {
        PecaAlocada pecaAlocada = new PecaAlocada(2L, 3L, 10);
        pecaAlocada.encomendarTotal();

        pecaAlocada.movimentarParaReservado(3);

        assertThat(pecaAlocada.getQuantidadeReservada()).isEqualTo(3);
        assertThat(pecaAlocada.getQuantidadeEncomendada()).isEqualTo(7);
        assertThat(pecaAlocada.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
    }

    @Test
    void deveMovimentarParaReservadoTotal() {
        PecaAlocada pecaAlocada = new PecaAlocada(2L, 3L, 10);
        pecaAlocada.encomendarTotal();

        pecaAlocada.movimentarParaReservado(10);

        assertThat(pecaAlocada.getQuantidadeReservada()).isEqualTo(10);
        assertThat(pecaAlocada.getQuantidadeEncomendada()).isZero();
        assertThat(pecaAlocada.getStatus()).isEqualTo(StatusPecaAlocada.RESERVADA);
        assertThat(pecaAlocada.isReservada()).isTrue();
    }

    @Test
    void deveFalharAoMovimentarParaReservadoComQuantidadeInvalida() {
        PecaAlocada pecaAlocada = new PecaAlocada(2L, 3L, 10);

        assertThatThrownBy(() -> pecaAlocada.movimentarParaReservado(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantidade inválida");
    }

    @Test
    void deveFalharAoInstalarComQuantidadeInvalida() {
        PecaAlocada pecaAlocada = new PecaAlocada(2L, 3L, 10);

        assertThatThrownBy(() -> pecaAlocada.instalada(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantidade inválida");
    }

    @Test
    void deveFalharAoInstalarSemReservaSuficiente() {
        PecaAlocada pecaAlocada = new PecaAlocada(2L, 3L, 10);
        pecaAlocada.setQuantidadeReservada(2);

        assertThatThrownBy(() -> pecaAlocada.instalada(3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Quantidade reservada insuficiente para instalar");
    }

    @Test
    void deveMarcarComoInstaladaQuandoReservadaENcomendadaForemZero() {
        PecaAlocada pecaAlocada = new PecaAlocada(2L, 3L, 10);
        pecaAlocada.setQuantidadeReservada(3);
        pecaAlocada.setQuantidadeEncomendada(0);
        pecaAlocada.setStatus(StatusPecaAlocada.RESERVADA);

        pecaAlocada.instalada(3);

        assertThat(pecaAlocada.getQuantidadeReservada()).isZero();
        assertThat(pecaAlocada.getStatus()).isEqualTo(StatusPecaAlocada.INSTALADA);
        assertThat(pecaAlocada.totalmenteInstalada()).isTrue();
    }

    @Test
    void deveAvaliarHelpersDeReservaEProcessamento() {
        PecaAlocada pecaAlocada = new PecaAlocada(2L, 3L, 10);

        pecaAlocada.setQuantidadeReservada(5);
        assertThat(pecaAlocada.parcialmenteReservada()).isTrue();
        assertThat(pecaAlocada.isProcessada()).isTrue();

        pecaAlocada.setQuantidadeReservada(11);
        assertThat(pecaAlocada.isProcessada()).isFalse();
    }

    @Test
    void deveValidarSettersControlados() {
        PecaAlocada pecaAlocada = new PecaAlocada();

        assertThatThrownBy(() -> pecaAlocada.setPecaId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Peça obrigatória");

        assertThatThrownBy(() -> pecaAlocada.setExecucaoServicoId(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Serviço obrigatório");

        assertThatThrownBy(() -> pecaAlocada.setQuantidadeSolicitada(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantidade inválida");
    }

    @Test
    void deveAplicarEqualsEHashCodePorId() {
        PecaAlocada a = new PecaAlocada();
        a.setId(1L);
        PecaAlocada b = new PecaAlocada();
        b.setId(1L);
        PecaAlocada c = new PecaAlocada();
        c.setId(2L);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(a).isNotEqualTo(c);
    }
}
