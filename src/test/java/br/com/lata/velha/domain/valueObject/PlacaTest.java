package br.com.lata.velha.domain.valueObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlacaTest {

    @Test
    @DisplayName("deve criar placa formato antigo (ABC1234)")
    void deveCriarPlacaFormatoAntigo() {
        Placa placa = Placa.of("ABC1234");

        assertEquals("ABC1234", placa.getValor());
    }

    @Test
    @DisplayName("deve criar placa formato Mercosul (ABC1D23)")
    void deveCriarPlacaMercosul() {
        Placa placa = Placa.of("ABC1D23");

        assertEquals("ABC1D23", placa.getValor());
    }

    @Test
    @DisplayName("deve aceitar placa com hífen")
    void deveAceitarPlacaComHifen() {
        Placa placa = Placa.of("ABC-1D23");

        assertEquals("ABC1D23", placa.getValor());
    }

    @Test
    @DisplayName("deve converter para maiúsculas")
    void deveConverterParaMaiusculas() {
        Placa placa = Placa.of("abc1d23");

        assertEquals("ABC1D23", placa.getValor());
    }

    @Test
    @DisplayName("deve formatar com hífen")
    void deveFormatarComHifen() {
        Placa placa = Placa.of("ABC1D23");

        assertEquals("ABC-1D23", placa.getFormatado());
    }

    @Test
    @DisplayName("deve rejeitar placa nula")
    void deveRejeitarNula() {
        assertThrows(IllegalArgumentException.class, () -> Placa.of(null));
    }

    @Test
    @DisplayName("deve rejeitar placa vazia")
    void deveRejeitarVazia() {
        assertThrows(IllegalArgumentException.class, () -> Placa.of(""));
    }

    @Test
    @DisplayName("deve rejeitar placa com formato inválido")
    void deveRejeitarFormatoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> Placa.of("12345"));
        assertThrows(IllegalArgumentException.class, () -> Placa.of("ABCDEFG"));
        assertThrows(IllegalArgumentException.class, () -> Placa.of("1234567"));
    }

    @Test
    @DisplayName("placas iguais devem ser equals")
    void placasIguaisDevemSerEquals() {
        Placa placa1 = Placa.of("ABC1D23");
        Placa placa2 = Placa.of("abc-1d23");

        assertEquals(placa1, placa2);
        assertEquals(placa1.hashCode(), placa2.hashCode());
    }

    @Test
    @DisplayName("placas diferentes não devem ser equals")
    void placasDiferentesNaoDevemSerEquals() {
        Placa placa1 = Placa.of("ABC1234");
        Placa placa2 = Placa.of("XYZ9876");

        assertNotEquals(placa1, placa2);
    }

    @Test
    @DisplayName("toString deve retornar formatado")
    void toStringDeveRetornarFormatado() {
        Placa placa = Placa.of("ABC1D23");

        assertEquals("ABC-1D23", placa.toString());
    }
}