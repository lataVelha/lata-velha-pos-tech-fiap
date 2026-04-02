package br.com.lata.velha.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CargoTest {

    private Cargo cargo;
    private Role roleAdmin;
    private Role roleUser;

    @BeforeEach
    void setUp() {
        roleAdmin = new Role(1L, "ADMIN");
        roleUser = new Role(2L, "USER");
        cargo = new Cargo(1L, "Administrador", Set.of(roleAdmin, roleUser));
    }

    @Test
    @DisplayName("deve criar cargo com roles")
    void shouldCreateCargoWithRoles() {
        assertEquals(1L, cargo.getId());
        assertEquals("Administrador", cargo.getNome());
        assertEquals(2, cargo.getRoles().size());
    }

    @Test
    @DisplayName("deve criar cargo vazio com set de roles inicializado")
    void shouldCreateEmptyCargoWithInitializedRoles() {
        Cargo empty = new Cargo();

        assertNotNull(empty.getRoles());
        assertTrue(empty.getRoles().isEmpty());
    }

    @Test
    @DisplayName("deve rejeitar nome nulo")
    void shouldRejectNullNome() {
        assertThrows(IllegalArgumentException.class, () -> cargo.setNome(null));
    }

    @Test
    @DisplayName("deve rejeitar nome vazio")
    void shouldRejectEmptyNome() {
        assertThrows(IllegalArgumentException.class, () -> cargo.setNome(""));
    }

    @Test
    @DisplayName("deve adicionar role")
    void shouldAddRole() {
        Cargo newCargo = new Cargo(2L, "Teste", null);
        Role role = new Role(3L, "MECANICO");

        newCargo.addRole(role);

        assertEquals(1, newCargo.getRoles().size());
    }

    @Test
    @DisplayName("deve remover role")
    void shouldRemoveRole() {
        cargo.removeRole(roleUser);

        assertEquals(1, cargo.getRoles().size());
    }

    @Test
    @DisplayName("deve rejeitar role nula ao adicionar")
    void shouldRejectNullRole() {
        assertThrows(NullPointerException.class, () -> cargo.addRole(null));
    }

    @Test
    @DisplayName("deve verificar se possui role")
    void shouldCheckHasRole() {
        assertTrue(cargo.hasRole("ADMIN"));
        assertTrue(cargo.hasRole("admin"));
        assertFalse(cargo.hasRole("MECANICO"));
    }

    @Test
    @DisplayName("roles devem ser imutáveis via getRoles")
    void shouldBeImmutableViaGetRoles() {
        assertThrows(UnsupportedOperationException.class,
                () -> cargo.getRoles().add(new Role(3L, "MECANICO")));
    }

    @Test
    @DisplayName("cargos com mesmo id devem ser equals")
    void shouldBeEqualWithSameId() {
        Cargo other = new Cargo();
        other.setId(1L);

        assertEquals(cargo, other);
    }
}