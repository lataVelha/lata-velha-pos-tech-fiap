package br.com.lata.velha.domain.valueObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumeroCelularTest {

    @Test
    @DisplayName("deve criar celular com 11 dígitos")
    void deveCriarCelular11Digitos() {
        NumeroCelular cel = NumeroCelular.of("11999990001");

        assertEquals("11999990001", cel.getValor());
    }

    @Test
    @DisplayName("deve criar celular com 10 dígitos (fixo)")
    void deveCriarCelular10Digitos() {
        NumeroCelular cel = NumeroCelular.of("1133334444");

        assertEquals("1133334444", cel.getValor());
    }

    @Test
    @DisplayName("deve limpar formatação")
    void deveLimparFormatacao() {
        NumeroCelular cel = NumeroCelular.of("(11) 99999-0001");

        assertEquals("11999990001", cel.getValor());
    }

    @Test
    @DisplayName("deve formatar celular 11 dígitos")
    void deveFormatarCelular11() {
        NumeroCelular cel = NumeroCelular.of("11999990001");

        assertEquals("(11) 99999-0001", cel.getFormatado());
    }

    @Test
    @DisplayName("deve formatar celular 10 dígitos")
    void deveFormatarCelular10() {
        NumeroCelular cel = NumeroCelular.of("1133334444");

        assertEquals("(11) 3333-4444", cel.getFormatado());
    }

    @Test
    @DisplayName("deve rejeitar nulo")
    void deveRejeitarNulo() {
        assertThrows(IllegalArgumentException.class, () -> NumeroCelular.of(null));
    }

    @Test
    @DisplayName("deve rejeitar vazio")
    void deveRejeitarVazio() {
        assertThrows(IllegalArgumentException.class, () -> NumeroCelular.of(""));
    }

    @Test
    @DisplayName("deve rejeitar menos de 10 dígitos")
    void deveRejeitarCurto() {
        assertThrows(IllegalArgumentException.class, () -> NumeroCelular.of("123456789"));
    }

    @Test
    @DisplayName("deve rejeitar mais de 11 dígitos")
    void deveRejeitarLongo() {
        assertThrows(IllegalArgumentException.class, () -> NumeroCelular.of("123456789012"));
    }

    @Test
    @DisplayName("celulares iguais devem ser equals")
    void celularesIguaisDevemSerEquals() {
        NumeroCelular cel1 = NumeroCelular.of("11999990001");
        NumeroCelular cel2 = NumeroCelular.of("(11) 99999-0001");

        assertEquals(cel1, cel2);
        assertEquals(cel1.hashCode(), cel2.hashCode());
    }

    @Test
    @DisplayName("toString deve retornar formatado")
    void toStringDeveRetornarFormatado() {
        NumeroCelular cel = NumeroCelular.of("11999990001");

        assertEquals("(11) 99999-0001", cel.toString());
    }
}