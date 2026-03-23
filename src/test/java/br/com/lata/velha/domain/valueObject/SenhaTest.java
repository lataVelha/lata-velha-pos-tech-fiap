package br.com.lata.velha.domain.valueObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SenhaTest {

    @Test
    @DisplayName("deve criar senha a partir de hash")
    void deveCriarSenhaDeHash() {
        Senha senha = Senha.fromHash("hash123", (plana, hash) -> plana.equals("123456"));

        assertNotNull(senha);
        assertEquals("hash123", senha.getHash());
    }

    @Test
    @DisplayName("deve retornar true quando senha corresponde")
    void deveRetornarTrueQuandoCorresponde() {
        Senha senha = Senha.fromHash("hash123", (plana, hash) -> plana.equals("123456"));

        assertTrue(senha.corresponde("123456"));
    }

    @Test
    @DisplayName("deve retornar false quando senha não corresponde")
    void deveRetornarFalseQuandoNaoCorresponde() {
        Senha senha = Senha.fromHash("hash123", (plana, hash) -> plana.equals("123456"));

        assertFalse(senha.corresponde("senhaErrada"));
    }

    @Test
    @DisplayName("deve retornar false quando senha plana é nula")
    void deveRetornarFalseQuandoNula() {
        Senha senha = Senha.fromHash("hash123", (plana, hash) -> true);

        assertFalse(senha.corresponde(null));
    }

    @Test
    @DisplayName("deve retornar false quando senha plana é vazia")
    void deveRetornarFalseQuandoVazia() {
        Senha senha = Senha.fromHash("hash123", (plana, hash) -> true);

        assertFalse(senha.corresponde(""));
    }

    @Test
    @DisplayName("deve rejeitar hash nulo")
    void deveRejeitarHashNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> Senha.fromHash(null, (plana, hash) -> true));
    }

    @Test
    @DisplayName("deve rejeitar hash vazio")
    void deveRejeitarHashVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> Senha.fromHash("", (plana, hash) -> true));
    }

    @Test
    @DisplayName("deve rejeitar verificador nulo")
    void deveRejeitarVerificadorNulo() {
        assertThrows(NullPointerException.class,
                () -> Senha.fromHash("hash123", null));
    }

    @Test
    @DisplayName("senhas com mesmo hash devem ser equals")
    void senhasIguaisDevemSerEquals() {
        Senha senha1 = Senha.fromHash("hash123", (p, h) -> true);
        Senha senha2 = Senha.fromHash("hash123", (p, h) -> false);

        assertEquals(senha1, senha2);
        assertEquals(senha1.hashCode(), senha2.hashCode());
    }

    @Test
    @DisplayName("toString não deve expor o hash")
    void toStringNaoDeveExporHash() {
        Senha senha = Senha.fromHash("hash_secreto", (p, h) -> true);

        assertFalse(senha.toString().contains("hash_secreto"));
        assertTrue(senha.toString().contains("***"));
    }
}