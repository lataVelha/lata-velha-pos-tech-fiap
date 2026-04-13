package br.com.lata.velha.authentication.application.useCases;

import br.com.lata.velha.application.dto.request.CadastrarFuncionarioRequest;
import br.com.lata.velha.application.usecase.funcionario.CadastrarFuncionarioUseCase;
import br.com.lata.velha.authentication.domain.exceptions.notFoundExceptions.UserNotFoundException;
import br.com.lata.velha.authentication.domain.exceptions.InvalidLoginException;
import br.com.lata.velha.infrastructure.persistence.entity.CargoEntity;
import br.com.lata.velha.infrastructure.persistence.entity.RoleEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:login-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class LoginUseCaseIT {

    @Autowired
    private LoginUseCase loginUseCase;

    @Autowired
    private CadastrarFuncionarioUseCase cadastrarUseCase;

    @Autowired
    private EntityManager em;

    private static final String USERNAME = "tecnico@example.com";
    private static final String SENHA = "Senha1@!";

    @BeforeEach
    void setUp() {
        RoleEntity role = new RoleEntity(null, "MECANICO");
        em.persist(role);

        CargoEntity cargo = new CargoEntity();
        cargo.setNome("MECANICO");
        cargo.setRoles(Set.of(role));
        em.persist(cargo);

        em.flush();

        cadastrarUseCase.execute(
                new CadastrarFuncionarioRequest("Técnico", USERNAME, SENHA, cargo.getId())
        );

        em.flush();
    }

    @Test
    @DisplayName("deve retornar token JWT ao fazer login com credenciais válidas")
    void shouldReturnTokenOnSuccessfulLogin() {
        LoginUseCase.Input input = new LoginUseCase.Input(USERNAME, SENHA);

        LoginUseCase.Output output = loginUseCase.execute(input);

        assertNotNull(output);
        assertNotNull(output.token());
        assertFalse(output.token().isBlank());
        assertNotNull(output.expiresIn());
        assertTrue(output.expiresIn() > 0);
    }

    @Test
    @DisplayName("deve lançar InvalidLoginException com senha incorreta")
    void shouldThrowOnWrongPassword() {
        LoginUseCase.Input input = new LoginUseCase.Input(USERNAME, "senhaErrada");

        assertThrows(InvalidLoginException.class, () -> loginUseCase.execute(input));
    }

    @Test
    @DisplayName("deve lançar UserNotFoundException com username inexistente")
    void shouldThrowWhenUsernameNotFound() {
        LoginUseCase.Input input = new LoginUseCase.Input("inexistente@example.com", SENHA);

        assertThrows(UserNotFoundException.class, () -> loginUseCase.execute(input));
    }
}
