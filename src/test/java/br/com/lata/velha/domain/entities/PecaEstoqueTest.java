package br.com.lata.velha.domain.entities;

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
}
