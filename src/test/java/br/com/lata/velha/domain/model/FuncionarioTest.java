package br.com.lata.velha.domain.model;

import br.com.lata.velha.domain.exception.InvalidLoginException;
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
    class Authentication {

        @Test
        @DisplayName("deve autenticar com senha correta")
        void shouldAuthenticateWithCorrectPassword() {
            assertDoesNotThrow(() -> funcionario.authenticateOrFail("123456"));
        }

        @Test
        @DisplayName("não deve autenticar com senha errada")
        void shouldFailWithWrongPassword() {
            assertThrows(InvalidLoginException.class,
                    () -> funcionario.authenticateOrFail("senhaErrada"));
        }

        @Test
        @DisplayName("não deve autenticar com senha nula")
        void shouldFailWithNullPassword() {
            assertThrows(InvalidLoginException.class,
                    () -> funcionario.authenticateOrFail(null));
        }

        @Test
        @DisplayName("não deve autenticar quando senha do funcionário é nula")
        void shouldFailWhenSenhaIsNull() {
            funcionario.setSenha(null);

            assertThrows(InvalidLoginException.class,
                    () -> funcionario.authenticateOrFail("123456"));
        }
    }

    // ==================== ROLES ====================

    @Nested
    @DisplayName("Verificação de roles")
    class RolesCheck {

        @Test
        @DisplayName("deve verificar role existente")
        void shouldReturnTrueForExistingRole() {
            assertTrue(funcionario.hasRole("ADMIN"));
            assertTrue(funcionario.hasRole("USER"));
        }

        @Test
        @DisplayName("deve retornar false para role inexistente")
        void shouldReturnFalseForNonExistingRole() {
            assertFalse(funcionario.hasRole("MECANICO"));
        }

        @Test
        @DisplayName("deve retornar false quando cargo é nulo")
        void shouldReturnFalseWhenCargoIsNull() {
            funcionario.setCargo(null);

            assertFalse(funcionario.hasRole("ADMIN"));
        }
    }

    // ==================== VALIDAÇÕES ====================

    @Nested
    @DisplayName("Validações")
    class Validations {

        @Test
        @DisplayName("deve rejeitar nome nulo")
        void shouldRejectNullNome() {
            assertThrows(IllegalArgumentException.class, () -> funcionario.setNome(null));
        }

        @Test
        @DisplayName("deve rejeitar nome vazio")
        void shouldRejectEmptyNome() {
            assertThrows(IllegalArgumentException.class, () -> funcionario.setNome(""));
        }

        @Test
        @DisplayName("deve rejeitar username nulo")
        void shouldRejectNullUsername() {
            assertThrows(IllegalArgumentException.class, () -> funcionario.setUsername(null));
        }

        @Test
        @DisplayName("deve rejeitar username vazio")
        void shouldRejectEmptyUsername() {
            assertThrows(IllegalArgumentException.class, () -> funcionario.setUsername(""));
        }
    }

    // ==================== EQUALS / HASHCODE ====================

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("funcionários com mesmo id devem ser equals")
        void shouldBeEqualWithSameId() {
            Funcionario outro = new Funcionario();
            outro.setId(1L);

            assertEquals(funcionario, outro);
            assertEquals(funcionario.hashCode(), outro.hashCode());
        }

        @Test
        @DisplayName("funcionários com ids diferentes não devem ser equals")
        void shouldNotBeEqualWithDifferentId() {
            Funcionario outro = new Funcionario();
            outro.setId(2L);

            assertNotEquals(funcionario, outro);
        }
    }
}