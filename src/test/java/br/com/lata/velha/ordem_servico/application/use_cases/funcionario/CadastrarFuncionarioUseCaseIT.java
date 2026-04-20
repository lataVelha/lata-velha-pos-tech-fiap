package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.authentication.infrastructure.persistence.jpa.UserJpaRepository;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.CargoNotFoundException;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.CargoEntity;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
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
        "spring.datasource.url=jdbc:h2:mem:cadastrar-funcionario-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class CadastrarFuncionarioUseCaseIT {

    @Autowired
    private CadastrarFuncionarioUseCase useCase;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private EntityManager em;

    private Long cargoId;

    @BeforeEach
    void setUp() {
        RoleEntity role = new RoleEntity(null, "MECANICO");
        em.persist(role);

        CargoEntity cargo = new CargoEntity();
        cargo.setNome("MECANICO");
        cargo.setRoles(Set.of(role));
        em.persist(cargo);

        em.flush();
        cargoId = cargo.getId();
    }

    @Test
    @DisplayName("deve cadastrar funcionário com sucesso e retornar output com dados corretos")
    void shouldRegisterFuncionarioSuccessfully() {
        var input = new CadastrarFuncionarioUseCase.Input("João Silva", "joao@example.com", "Senha1@!", cargoId);
        var output = useCase.execute(input);

        assertNotNull(output);
        assertNotNull(output.id());
        assertEquals("João Silva", output.nome());
        assertEquals("MECANICO", output.cargo());
        assertNotNull(output.userId());
    }

    @Test
    @DisplayName("deve persistir usuário no banco com o email correto ao cadastrar funcionário")
    void shouldPersistUserWithCorrectEmail() {
        var input = new CadastrarFuncionarioUseCase.Input("João Silva", "joao@example.com", "Senha1@!", cargoId);
        var output = useCase.execute(input);
        em.flush();

        var savedUser = userJpaRepository.findById(output.userId());

        assertTrue(savedUser.isPresent());
        assertEquals("joao@example.com", savedUser.get().getEmail());
    }

    @Test
    @DisplayName("deve associar o role do cargo ao usuário criado")
    void shouldAssignCargoRolesToUser() {
        var input = new CadastrarFuncionarioUseCase.Input("João Silva", "joao@example.com", "Senha1@!", cargoId);
        useCase.execute(input);
        em.flush();
        em.clear();

        var userWithRoles = userJpaRepository.findByUsernameWithRoles("joao@example.com");

        assertTrue(userWithRoles.isPresent());
        var roles = userWithRoles.get().getRoles();
        assertFalse(roles.isEmpty(), "Usuário deve ter pelo menos uma role");
        assertTrue(
                roles.stream().anyMatch(r -> "MECANICO".equals(r.getNome())),
                "Usuário deve ter a role MECANICO"
        );
    }

    @Test
    @DisplayName("o userId retornado no output deve corresponder ao usuário salvo no banco")
    void shouldReturnUserIdMatchingPersistedUser() {
        var input = new CadastrarFuncionarioUseCase.Input("João Silva", "joao@example.com", "Senha1@!", cargoId);
        var output = useCase.execute(input);
        em.flush();

        assertTrue(userJpaRepository.existsById(output.userId()));
    }

    @Test
    @DisplayName("deve lançar ResourceAlreadyExistsException ao cadastrar com email duplicado")
    void shouldThrowWhenEmailAlreadyExists() {
        var input = new CadastrarFuncionarioUseCase.Input("João Silva", "duplicado@example.com", "Senha1@!", cargoId);
        useCase.execute(input);

        var duplicate = new CadastrarFuncionarioUseCase.Input("Maria Santos", "duplicado@example.com", "OutraSenha2#", cargoId);

        assertThrows(ResourceAlreadyExistsException.class, () -> useCase.execute(duplicate));
    }

    @Test
    @DisplayName("deve lançar CargoNotFoundException quando cargoId não existe")
    void shouldThrowWhenCargoNotFound() {
        var input = new CadastrarFuncionarioUseCase.Input("Pedro", "pedro@example.com", "Senha1@!", 9999L);

        assertThrows(CargoNotFoundException.class, () -> useCase.execute(input));
    }
}
