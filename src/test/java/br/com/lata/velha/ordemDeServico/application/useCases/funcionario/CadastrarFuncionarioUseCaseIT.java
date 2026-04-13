package br.com.lata.velha.ordemDeServico.application.useCases.funcionario;

import br.com.lata.velha.ordemDeServico.application.dtos.request.CadastrarFuncionarioRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.ordemDeServico.domain.exceptions.notFoundExceptions.CargoNotFoundException;
import br.com.lata.velha.ordemDeServico.infrastructure.persistence.entities.CargoEntity;
import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
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
    @DisplayName("deve cadastrar funcionário com sucesso e retornar FuncionarioResponse")
    void shouldRegisterFuncionarioSuccessfully() {
        var request = new CadastrarFuncionarioRequest("João Silva", "joao@example.com", "Senha1@!", cargoId);

        FuncionarioResponse response = useCase.execute(request);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("João Silva", response.nome());
        assertEquals("MECANICO", response.cargo());
    }

    @Test
    @DisplayName("deve lançar ResourceAlreadyExistsException ao cadastrar com email duplicado")
    void shouldThrowWhenEmailAlreadyExists() {
        var request = new CadastrarFuncionarioRequest("João Silva", "duplicado@example.com", "Senha1@!", cargoId);
        useCase.execute(request);

        var duplicate = new CadastrarFuncionarioRequest("Maria Santos", "duplicado@example.com", "OutraSenha2#", cargoId);

        assertThrows(ResourceAlreadyExistsException.class, () -> useCase.execute(duplicate));
    }

    @Test
    @DisplayName("deve lançar CargoNotFoundException quando cargoId não existe")
    void shouldThrowWhenCargoNotFound() {
        var request = new CadastrarFuncionarioRequest("Pedro", "pedro@example.com", "Senha1@!", 9999L);

        assertThrows(CargoNotFoundException.class, () -> useCase.execute(request));
    }
}
