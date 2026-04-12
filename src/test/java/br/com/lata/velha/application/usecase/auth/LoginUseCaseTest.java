package br.com.lata.velha.application.usecase.auth;

import br.com.lata.velha.application.dto.request.LoginRequest;
import br.com.lata.velha.application.dto.response.LoginResponse;
import br.com.lata.velha.application.port.TokenProvider;
import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.domain.valueObjects.Credential;
import br.com.lata.velha.domain.exception.InvalidLoginException;
import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private LoginUseCase useCase;

    private Funcionario funcionario;

    @BeforeEach
    void setUp() {
        lenient().when(passwordHasher.match(any(), eq("123456"))).thenReturn(true);
        Credential credential = Credential.fromHash("hash", passwordHasher);
        Cargo cargo = new Cargo(1L, "ADMIN", Set.of(Role.create("ADMIN")));
        funcionario = new Funcionario(1L, "Admin", "admin", credential, cargo);
    }

    @Test
    @DisplayName("deve fazer login com sucesso")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("admin", "123456");

        when(funcionarioRepository.findByUsername("admin")).thenReturn(funcionario);
        when(tokenProvider.generate(1L, "ADMIN")).thenReturn("jwt-token");
        when(tokenProvider.getExpiresIn()).thenReturn(3600L);

        LoginResponse result = useCase.execute(request);

        assertNotNull(result);
        assertEquals("jwt-token", result.token());
        assertEquals(3600L, result.expiresIn());
        verify(funcionarioRepository).findByUsername("admin");
        verify(tokenProvider).generate(1L, "ADMIN");
    }

    @Test
    @DisplayName("deve falhar com senha errada")
    void shouldFailWithWrongPassword() {
        LoginRequest request = new LoginRequest("admin", "wrongPassword!1");

        when(funcionarioRepository.findByUsername("admin")).thenReturn(funcionario);

        assertThrows(InvalidLoginException.class, () -> useCase.execute(request));
        verify(funcionarioRepository).findByUsername("admin");
        verify(tokenProvider, never()).generate(any(), any());
    }

    @Test
    @DisplayName("deve gerar scopes com múltiplas roles")
    void shouldGenerateScopesWithMultipleRoles() {
        Credential credential = Credential.fromHash("hash", passwordHasher);
        Cargo cargo = new Cargo(1L, "ADMIN", Set.of(
                Role.create("ADMIN"),
                Role.create("USER")
        ));
        Funcionario multiRole = new Funcionario(1L, "Admin", "admin", credential, cargo);
        LoginRequest request = new LoginRequest("admin", "123456");

        when(funcionarioRepository.findByUsername("admin")).thenReturn(multiRole);
        when(tokenProvider.generate(eq(1L), anyString())).thenReturn("jwt-token");
        when(tokenProvider.getExpiresIn()).thenReturn(3600L);

        LoginResponse result = useCase.execute(request);

        assertNotNull(result);
        verify(tokenProvider).generate(eq(1L), argThat(scopes ->
                scopes.contains("ADMIN") && scopes.contains("USER")));
    }
}
