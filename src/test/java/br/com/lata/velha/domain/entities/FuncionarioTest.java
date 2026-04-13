package br.com.lata.velha.domain.entities;

import br.com.lata.velha.shared.domain.valueObjects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioTest {

    private Funcionario funcionario;
    private Cargo cargo;
    private UserId userId;

    @BeforeEach
    void setUp() {
        cargo = new Cargo(1L, "ADMIN", null);
        userId = UserId.random();
        funcionario = new Funcionario(1L, "João", cargo, userId);
    }

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
    }

    @Nested
    @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test
        @DisplayName("deve retornar id correto")
        void shouldGetId() {
            assertEquals(1L, funcionario.getId());
        }

        @Test
        @DisplayName("deve retornar nome correto")
        void shouldGetNome() {
            assertEquals("João", funcionario.getNome());
        }

        @Test
        @DisplayName("deve setar e obter cargo")
        void shouldSetAndGetCargo() {
            Cargo newCargo = new Cargo(2L, "USER", null);
            funcionario.setCargo(newCargo);
            assertEquals(newCargo, funcionario.getCargo());
        }

        @Test
        @DisplayName("deve retornar userId correto")
        void shouldGetUserId() {
            assertEquals(userId, funcionario.getUserId());
        }
    }

    @Nested
    @DisplayName("Factory method")
    class FactoryMethod {

        @Test
        @DisplayName("create deve criar funcionário sem id")
        void shouldCreateFuncionarioWithNullId() {
            Funcionario created = Funcionario.create("Maria", cargo, userId);
            assertNull(created.getId());
            assertEquals("Maria", created.getNome());
            assertEquals(cargo, created.getCargo());
            assertEquals(userId, created.getUserId());
        }
    }

    @Nested
    @DisplayName("Equals, HashCode e ToString")
    class EqualsHashCodeToString {

        @Test
        @DisplayName("funcionários com mesmo id devem ser equals")
        void shouldBeEqualWithSameId() {
            Funcionario other = new Funcionario(1L, "Outro Nome", new Cargo(2L, "USER", null), UserId.random());
            assertEquals(funcionario, other);
            assertEquals(funcionario.hashCode(), other.hashCode());
        }

        @Test
        @DisplayName("funcionários com ids diferentes não devem ser equals")
        void shouldNotBeEqualWithDifferentId() {
            Funcionario other = new Funcionario(2L, "João", cargo, userId);
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
    }
}
