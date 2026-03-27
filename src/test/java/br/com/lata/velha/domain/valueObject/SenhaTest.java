package br.com.lata.velha.domain.valueObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SenhaTest {

    @Test
    @DisplayName("deve criar senha a partir de hash")
    void shouldCreateFromHash() {
        Senha senha = Senha.fromHash("hash123", (raw, hash) -> raw.equals("123456"));

        assertNotNull(senha);
        assertEquals("hash123", senha.getHash());
    }

    @Test
    @DisplayName("deve retornar true quando senha corresponde")
    void shouldReturnTrueWhenMatches() {
        Senha senha = Senha.fromHash("hash123", (raw, hash) -> raw.equals("123456"));

        assertTrue(senha.matches("123456"));
    }

    @Test
    @DisplayName("deve retornar false quando senha não corresponde")
    void shouldReturnFalseWhenNotMatches() {
        Senha senha = Senha.fromHash("hash123", (raw, hash) -> raw.equals("123456"));

        assertFalse(senha.matches("wrongPassword"));
    }

    @Test
    @DisplayName("deve retornar false quando senha plana é nula")
    void shouldReturnFalseWhenNull() {
        Senha senha = Senha.fromHash("hash123", (raw, hash) -> true);

        assertFalse(senha.matches(null));
    }

    @Test
    @DisplayName("deve retornar false quando senha plana é vazia")
    void shouldReturnFalseWhenEmpty() {
        Senha senha = Senha.fromHash("hash123", (raw, hash) -> true);

        assertFalse(senha.matches(""));
    }

    @Test
    @DisplayName("deve rejeitar hash nulo")
    void shouldRejectNullHash() {
        assertThrows(IllegalArgumentException.class,
                () -> Senha.fromHash(null, (raw, hash) -> true));
    }

    @Test
    @DisplayName("deve rejeitar hash vazio")
    void shouldRejectEmptyHash() {
        assertThrows(IllegalArgumentException.class,
                () -> Senha.fromHash("", (raw, hash) -> true));
    }

    @Test
    @DisplayName("deve rejeitar verificador nulo")
    void shouldRejectNullVerifier() {
        assertThrows(NullPointerException.class,
                () -> Senha.fromHash("hash123", null));
    }

    @Test
    @DisplayName("senhas com mesmo hash devem ser equals")
    void shouldBeEqualWithSameHash() {
        Senha senha1 = Senha.fromHash("hash123", (r, h) -> true);
        Senha senha2 = Senha.fromHash("hash123", (r, h) -> false);

        assertEquals(senha1, senha2);
        assertEquals(senha1.hashCode(), senha2.hashCode());
    }

    @Test
    @DisplayName("toString não deve expor o hash")
    void shouldNotExposeHashOnToString() {
        Senha senha = Senha.fromHash("hash_secreto", (r, h) -> true);

        assertFalse(senha.toString().contains("hash_secreto"));
        assertTrue(senha.toString().contains("***"));
    }
}