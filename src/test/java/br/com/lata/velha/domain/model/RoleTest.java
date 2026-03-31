package br.com.lata.velha.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    // ==================== CRIAÇÃO ====================

    @Nested
    @DisplayName("Criação")
    class Creation {

        @Test
        @DisplayName("deve criar role com id e nome")
        void shouldCreateRoleWithIdAndNome() {
            Role role = new Role(1L, "ADMIN");

            assertEquals(1L, role.getId());
            assertEquals("ADMIN", role.getNome());
        }

        @Test
        @DisplayName("deve criar role vazia")
        void shouldCreateEmptyRole() {
            Role role = new Role();

            assertNull(role.getId());
            assertNull(role.getNome());
        }
    }

    // ==================== VALIDAÇÕES ====================

    @Nested
    @DisplayName("Validações")
    class Validations {

        @Test
        @DisplayName("deve rejeitar nome nulo")
        void shouldRejectNullNome() {
            assertThrows(IllegalArgumentException.class, () -> new Role(1L, null));
        }

        @Test
        @DisplayName("deve rejeitar nome vazio")
        void shouldRejectEmptyNome() {
            assertThrows(IllegalArgumentException.class, () -> new Role(1L, ""));
        }

        @Test
        @DisplayName("deve rejeitar nome em branco")
        void shouldRejectBlankNome() {
            assertThrows(IllegalArgumentException.class, () -> new Role(1L, "   "));
        }

        @Test
        @DisplayName("deve aceitar nome válido via setter")
        void shouldAcceptValidNomeViaSetter() {
            Role role = new Role(1L, "ADMIN");
            role.setNome("USER");
            assertEquals("USER", role.getNome());
        }
    }

    // ==================== GETTERS / SETTERS ====================

    @Nested
    @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test
        @DisplayName("deve setar e obter id")
        void shouldSetAndGetId() {
            Role role = new Role(1L, "ADMIN");
            role.setId(99L);
            assertEquals(99L, role.getId());
        }
    }

    // ==================== EQUALS / HASHCODE / TOSTRING ====================

    @Nested
    @DisplayName("Equals, HashCode e ToString")
    class EqualsHashCodeToString {

        @Test
        @DisplayName("roles com mesmo id devem ser equals")
        void shouldBeEqualWithSameId() {
            Role role1 = new Role(1L, "ADMIN");
            Role role2 = new Role(1L, "USER");

            assertEquals(role1, role2);
            assertEquals(role1.hashCode(), role2.hashCode());
        }

        @Test
        @DisplayName("roles com ids diferentes não devem ser equals")
        void shouldNotBeEqualWithDifferentId() {
            Role role1 = new Role(1L, "ADMIN");
            Role role2 = new Role(2L, "ADMIN");

            assertNotEquals(role1, role2);
        }

        @Test
        @DisplayName("deve ser igual a si mesmo")
        void shouldBeEqualToSelf() {
            Role role = new Role(1L, "ADMIN");
            assertEquals(role, role);
        }

        @Test
        @DisplayName("não deve ser igual a null")
        void shouldNotBeEqualToNull() {
            Role role = new Role(1L, "ADMIN");
            assertNotEquals(null, role);
        }

        @Test
        @DisplayName("não deve ser igual a tipo diferente")
        void shouldNotBeEqualToDifferentType() {
            Role role = new Role(1L, "ADMIN");
            assertNotEquals("string", role);
        }

        @Test
        @DisplayName("toString deve conter id e nome")
        void shouldContainIdAndNomeInToString() {
            Role role = new Role(1L, "ADMIN");
            String result = role.toString();

            assertTrue(result.contains("1"));
            assertTrue(result.contains("ADMIN"));
        }
    }
}