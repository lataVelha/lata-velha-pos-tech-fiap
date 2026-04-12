package br.com.lata.velha.authentication.domain.entities;

import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.domain.valueObjects.Credential;
import br.com.lata.velha.authentication.domain.valueObjects.Senha;
import br.com.lata.velha.shared.domain.valueObjects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static final PasswordHasher PLAIN_HASHER = new PasswordHasher() {
        @Override public String hashSenha(Senha s) { return s.getValor(); }
        @Override public boolean match(Credential cred, String raw) { return raw.equals(cred.getHash()); }
    };

    private static final String VALID_SENHA_VALUE = "Senha123!";

    private UserId userId;
    private Credential credential;
    private LocalDateTime criacaoDate;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UserId.random();
        credential = Credential.fromHash(VALID_SENHA_VALUE, PLAIN_HASHER);
        criacaoDate = LocalDateTime.now().minusDays(1);

        user = new User(
                userId,
                "joao",
                "joao@email.com",
                credential,
                new HashSet<>(),
                true,
                criacaoDate,
                null
        );
    }

    // ==================== LOGIN ====================

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("deve retornar true e atualizar ultimoLoginDate com senha correta")
        void shouldReturnTrueAndUpdateLoginDateOnCorrectPassword() {
            Senha senha = Senha.fromString(VALID_SENHA_VALUE);

            boolean result = user.login(senha);

            assertTrue(result);
            assertNotNull(user.getUltimoLoginDate());
        }

        @Test
        @DisplayName("deve retornar false e não atualizar ultimoLoginDate com senha errada")
        void shouldReturnFalseAndNotUpdateLoginDateOnWrongPassword() {
            Senha senhaErrada = Senha.fromString("Errada123@");

            boolean result = user.login(senhaErrada);

            assertFalse(result);
            assertNull(user.getUltimoLoginDate());
        }
    }

    // ==================== ADD ROLE ====================

    @Nested
    @DisplayName("addRole")
    class AddRole {

        @Test
        @DisplayName("deve adicionar role ao usuário")
        void shouldAddRole() {
            Role role = Role.create("ADMIN");

            user.addRole(role);

            assertTrue(user.getRoles().contains(role));
        }

        @Test
        @DisplayName("deve lançar exceção ao adicionar role nula")
        void shouldThrowWhenRoleIsNull() {
            var ex = assertThrows(IllegalArgumentException.class, () -> user.addRole(null));
            assertTrue(ex.getMessage().contains(userId.toString()));
        }
    }

    // ==================== ATIVAR / DESATIVAR ====================

    @Nested
    @DisplayName("ativar e desativar")
    class AtivarDesativar {

        @Test
        @DisplayName("ativar deve setar ativo para true")
        void ativarShouldSetAtivoTrue() {
            user.desativar();
            assertFalse(user.isAtivo());

            user.ativar();
            assertTrue(user.isAtivo());
        }

        @Test
        @DisplayName("desativar deve setar ativo para false")
        void desativarShouldSetAtivoFalse() {
            user.desativar();
            assertFalse(user.isAtivo());
        }
    }

    // ==================== GETTERS ====================

    @Nested
    @DisplayName("Getters")
    class Getters {

        @Test
        @DisplayName("deve retornar os campos corretamente")
        void shouldReturnFieldsCorrectly() {
            assertEquals(userId, user.getUserId());
            assertEquals("joao", user.getUsername());
            assertEquals("joao@email.com", user.getEmail());
            assertEquals(credential, user.getCredential());
            assertEquals(criacaoDate, user.getCriacaoDate());
            assertTrue(user.isAtivo());
            assertNotNull(user.getRoles());
        }
    }
}
