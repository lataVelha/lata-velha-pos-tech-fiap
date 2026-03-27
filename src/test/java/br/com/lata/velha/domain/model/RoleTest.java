package br.com.lata.velha.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    @DisplayName("deve criar role com id e nome")
    void shouldCreateRoleWithIdAndNome() {
        Role role = new Role(1L, "ADMIN");

        assertEquals(1L, role.getId());
        assertEquals("ADMIN", role.getNome());
    }

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
}