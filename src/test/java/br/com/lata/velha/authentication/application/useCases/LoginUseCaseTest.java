package br.com.lata.velha.authentication.application.useCases;

import br.com.lata.velha.authentication.application.security.TokenProvider;
import br.com.lata.velha.authentication.application.useCases.LoginUseCase;
import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.authentication.domain.entities.User;
import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.domain.valueObjects.Credential;
import br.com.lata.velha.domain.exception.InvalidLoginException;
import br.com.lata.velha.shared.domain.valueObjects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private LoginUseCase useCase;

    private User user;
    private UserId userId;

    @BeforeEach
    void setUp() {
        lenient().when(passwordHasher.match(any(), eq("123456"))).thenReturn(true);
        Credential credential = Credential.fromHash("hash", passwordHasher);
        userId = UserId.random();
        user = new User(userId, "admin", null, credential, Set.of(Role.create("ADMIN")), true, LocalDateTime.now(), null);
    }

    @Test
    @DisplayName("deve fazer login com sucesso")
    void shouldLoginSuccessfully() {
        LoginUseCase.Input input = new LoginUseCase.Input("admin", "123456");

        when(userRepository.getByUsername("admin")).thenReturn(user);
        when(tokenProvider.generate(userId, "ADMIN")).thenReturn("jwt-token");
        when(tokenProvider.getExpiresIn()).thenReturn(3600L);

        LoginUseCase.Output result = useCase.execute(input);

        assertNotNull(result);
        assertEquals("jwt-token", result.token());
        assertEquals(3600L, result.expiresIn());
        verify(userRepository).getByUsername("admin");
        verify(tokenProvider).generate(userId, "ADMIN");
    }

    @Test
    @DisplayName("deve falhar com senha errada")
    void shouldFailWithWrongPassword() {
        LoginUseCase.Input input = new LoginUseCase.Input("admin", "wrongPassword!1");

        when(userRepository.getByUsername("admin")).thenReturn(user);

        assertThrows(InvalidLoginException.class, () -> useCase.execute(input));
        verify(userRepository).getByUsername("admin");
        verify(tokenProvider, never()).generate(any(), any());
    }

    @Test
    @DisplayName("deve lançar InvalidLoginException para usuário inativo")
    void shouldFailForInactiveUser() {
        Credential credential = Credential.fromHash("hash", passwordHasher);
        User inactiveUser = new User(userId, "admin", null, credential,
                Set.of(Role.create("ADMIN")), false, LocalDateTime.now(), null);
        LoginUseCase.Input input = new LoginUseCase.Input("admin", "123456");

        when(userRepository.getByUsername("admin")).thenReturn(inactiveUser);

        assertThrows(InvalidLoginException.class, () -> useCase.execute(input));
        verify(tokenProvider, never()).generate(any(), any());
    }

    @Test
    @DisplayName("deve gerar scopes com múltiplas roles")
    void shouldGenerateScopesWithMultipleRoles() {
        Credential credential = Credential.fromHash("hash", passwordHasher);
        User multiRoleUser = new User(userId, "admin", null, credential, Set.of(
                Role.create("ADMIN"),
                Role.create("USER")
        ), true, LocalDateTime.now(), null);
        LoginUseCase.Input input = new LoginUseCase.Input("admin", "123456");

        when(userRepository.getByUsername("admin")).thenReturn(multiRoleUser);
        when(tokenProvider.generate(eq(userId), anyString())).thenReturn("jwt-token");
        when(tokenProvider.getExpiresIn()).thenReturn(3600L);

        LoginUseCase.Output result = useCase.execute(input);

        assertNotNull(result);
        verify(tokenProvider).generate(eq(userId), argThat(scopes ->
                scopes.contains("ADMIN") && scopes.contains("USER")));
    }
}
