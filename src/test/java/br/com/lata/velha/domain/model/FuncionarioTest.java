package br.com.lata.velha.domain.model;

import br.com.lata.velha.domain.valueObject.Senha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioTest {

    private Funcionario funcionario;
    private Senha senha;
    private Cargo cargo;

    @BeforeEach
    void setUp() {
        senha = Senha.fromHash("hash123", (plana, hash) -> plana.equals("123456"));
        cargo = new Cargo(1L, "ADMIN", Set.of(new Role(1L, "ADMIN"), new Role(2L, "USER")));
        funcionario = new Funcionario(1L, "João", "admin", senha, cargo);
    }

    // ==================== AUTENTICAÇÃO ====================

    @Nested
    @DisplayName("Autenticação")
    class Autenticacao {

        @Test
        @DisplayName("deve autenticar com senha correta")
        void deveAutenticarComSenhaCorreta() {
            assertTrue(funcionario.autenticar("123456"));
        }

        @Test
        @DisplayName("não deve autenticar com senha errada")
        void naoDeveAutenticarComSenhaErrada() {
            assertFalse(funcionario.autenticar("senhaErrada"));
        }

        @Test
        @DisplayName("não deve autenticar com senha nula")
        void naoDeveAutenticarComSenhaNula() {
            assertFalse(funcionario.autenticar(null));
        }

        @Test
        @DisplayName("não deve autenticar quando senha do funcionário é nula")
        void naoDeveAutenticarSemSenha() {
            funcionario.setSenha(null);

            assertFalse(funcionario.autenticar("123456"));
        }
    }

    // ==================== ROLES ====================

    @Nested
    @DisplayName("Verificação de roles")
    class Roles {

        @Test
        @DisplayName("deve verificar role existente")
        void deveVerificarRoleExistente() {
            assertTrue(funcionario.possuiRole("ADMIN"));
            assertTrue(funcionario.possuiRole("USER"));
        }

        @Test
        @DisplayName("deve retornar false para role inexistente")
        void deveRetornarFalseParaRoleInexistente() {
            assertFalse(funcionario.possuiRole("MECANICO"));
        }

        @Test
        @DisplayName("deve retornar false quando cargo é nulo")
        void deveRetornarFalseQuandoCargoNulo() {
            funcionario.setCargo(null);

            assertFalse(funcionario.possuiRole("ADMIN"));
        }
    }

    // ==================== VALIDAÇÕES ====================

    @Nested
    @DisplayName("Validações")
    class Validacoes {

        @Test
        @DisplayName("deve rejeitar nome nulo")
        void deveRejeitarNomeNulo() {
            assertThrows(IllegalArgumentException.class, () -> funcionario.setNome(null));
        }

        @Test
        @DisplayName("deve rejeitar nome vazio")
        void deveRejeitarNomeVazio() {
            assertThrows(IllegalArgumentException.class, () -> funcionario.setNome(""));
        }

        @Test
        @DisplayName("deve rejeitar username nulo")
        void deveRejeitarUsernameNulo() {
            assertThrows(IllegalArgumentException.class, () -> funcionario.setUsername(null));
        }

        @Test
        @DisplayName("deve rejeitar username vazio")
        void deveRejeitarUsernameVazio() {
            assertThrows(IllegalArgumentException.class, () -> funcionario.setUsername(""));
        }
    }

    // ==================== EQUALS / HASHCODE ====================

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("funcionários com mesmo id devem ser equals")
        void mesmoIdDevemSerEquals() {
            Funcionario outro = new Funcionario();
            outro.setId(1L);

            assertEquals(funcionario, outro);
            assertEquals(funcionario.hashCode(), outro.hashCode());
        }

        @Test
        @DisplayName("funcionários com ids diferentes não devem ser equals")
        void idsDiferentesNaoDevemSerEquals() {
            Funcionario outro = new Funcionario();
            outro.setId(2L);

            assertNotEquals(funcionario, outro);
        }
    }
}