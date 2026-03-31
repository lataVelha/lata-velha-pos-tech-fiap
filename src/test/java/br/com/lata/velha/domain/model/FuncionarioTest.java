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
        senha = Senha.fromHash("hash123", (raw, hash) -> raw.equals("123456"));
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
                    () -> funcionario.authenticateOrFail("wrongPassword"));
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

        @Test
        @DisplayName("não deve autenticar com senha vazia")
        void shouldFailWithEmptyPassword() {
            assertThrows(InvalidLoginException.class,
                    () -> funcionario.authenticateOrFail(""));
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
        @DisplayName("deve rejeitar nome em branco")
        void shouldRejectBlankNome() {
            assertThrows(IllegalArgumentException.class, () -> funcionario.setNome("   "));
        }

        @Test
        @DisplayName("deve aceitar nome válido")
        void shouldAcceptValidNome() {
            funcionario.setNome("Maria");
            assertEquals("Maria", funcionario.getNome());
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

        @Test
        @DisplayName("deve rejeitar username em branco")
        void shouldRejectBlankUsername() {
            assertThrows(IllegalArgumentException.class, () -> funcionario.setUsername("   "));
        }

        @Test
        @DisplayName("deve aceitar username válido")
        void shouldAcceptValidUsername() {
            funcionario.setUsername("newuser");
            assertEquals("newuser", funcionario.getUsername());
        }
    }

    // ==================== GETTERS / SETTERS ====================

    @Nested
    @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test
        @DisplayName("deve setar e obter id")
        void shouldSetAndGetId() {
            funcionario.setId(99L);
            assertEquals(99L, funcionario.getId());
        }

        @Test
        @DisplayName("deve setar e obter senha")
        void shouldSetAndGetSenha() {
            Senha newSenha = Senha.fromHash("newHash", (r, h) -> true);
            funcionario.setSenha(newSenha);
            assertEquals(newSenha, funcionario.getSenha());
        }

        @Test
        @DisplayName("deve setar e obter cargo")
        void shouldSetAndGetCargo() {
            Cargo newCargo = new Cargo(2L, "USER", null);
            funcionario.setCargo(newCargo);
            assertEquals(newCargo, funcionario.getCargo());
        }
    }

    // ==================== EQUALS / HASHCODE / TOSTRING ====================

    @Nested
    @DisplayName("Equals, HashCode e ToString")
    class EqualsHashCodeToString {

        @Test
        @DisplayName("funcionários com mesmo id devem ser equals")
        void shouldBeEqualWithSameId() {
            Funcionario other = new Funcionario();
            other.setId(1L);

            assertEquals(funcionario, other);
            assertEquals(funcionario.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("funcionários com ids diferentes não devem ser equals")
        void shouldNotBeEqualWithDifferentId() {
            Funcionario other = new Funcionario();
            other.setId(2L);

            assertNotEquals(funcionario, other);
        }

        @Test
        @DisplayName("deve ser igual a si mesmo")
        void shouldBeEqualToSelf() {
            assertEquals(funcionario, funcionario);
        }

        @Test
        @DisplayName("não deve ser igual a null")
        void shouldNotBeEqualToNull() {
            assertNotEquals(null, funcionario);
        }

        @Test
        @DisplayName("não deve ser igual a tipo diferente")
        void shouldNotBeEqualToDifferentType() {
            assertNotEquals("string", funcionario);
        }

        @Test
        @DisplayName("toString deve conter id, nome e username")
        void shouldContainFieldsInToString() {
            String result = funcionario.toString();

            assertTrue(result.contains("1"));
            assertTrue(result.contains("João"));
            assertTrue(result.contains("admin"));
        }
    }
}