package br.com.lata.velha.ordem_servico.domain.entities;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PecaEstoqueTest {

    @Test
    void deveCriarEstoqueQuandoDadosValidos() {
        PecaEstoque estoque = new PecaEstoque(1L, 5);

        assertThat(estoque.getPecaId()).isEqualTo(1L);
        assertThat(estoque.getQuantidadeArmazenada()).isEqualTo(5);
    }

    @Test
    void deveFalharQuandoPecaIdForNulo() {
        assertThatThrownBy(() -> new PecaEstoque(null, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID da peça inválido");
    }

    @Test
    void deveFalharQuandoPecaIdForMenorOuIgualAZero() {
        assertThatThrownBy(() -> new PecaEstoque(0L, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID da peça inválido");
    }

    @Test
    void deveAdicionarQuantidadeQuandoEntradaForValida() {
        PecaEstoque estoque = new PecaEstoque(1L, 5);

        estoque.adicionar(3);

        assertThat(estoque.getQuantidadeArmazenada()).isEqualTo(8);
    }

    @Test
    void deveFalharAoAdicionarQuantidadeInvalida() {
        PecaEstoque estoque = new PecaEstoque(1L, 5);

        assertThatThrownBy(() -> estoque.adicionar(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantidade de entrada inválida");
    }

    @Test
    void deveRemoverQuantidadeQuandoSaidaForValida() {
        PecaEstoque estoque = new PecaEstoque(1L, 10);

        estoque.remover(4);

        assertThat(estoque.getQuantidadeArmazenada()).isEqualTo(6);
    }

    @Test
    void deveFalharAoRemoverQuantidadeInvalida() {
        PecaEstoque estoque = new PecaEstoque(1L, 10);

        assertThatThrownBy(() -> estoque.remover(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantidade de saída inválida");
    }

    @Test
    void deveFalharAoRemoverMaisDoQueDisponivel() {
        PecaEstoque estoque = new PecaEstoque(1L, 3);

        assertThatThrownBy(() -> estoque.remover(4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estoque insuficiente para a peça informada");
    }

    @Test
    void deveAjustarQuantidadeQuandoValorForValido() {
        PecaEstoque estoque = new PecaEstoque(1L, 5);

        estoque.ajustar(12);

        assertThat(estoque.getQuantidadeArmazenada()).isEqualTo(12);
    }

    @Test
    void deveFalharAoDefinirQuantidadeArmazenadaInvalida() {
        PecaEstoque estoque = new PecaEstoque(1L, 5);

        assertThatThrownBy(() -> estoque.setQuantidadeArmazenada(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Quantidade armazenada inválida");
    }

    @Test
    void deveRetornarTextoNoFormatoEsperado() {
        PecaEstoque estoque = new PecaEstoque(1L, 7);

        assertThat(estoque.toString()).isEqualTo("PecaEstoque{pecaId=1, quantidade=7}");
    }
}
