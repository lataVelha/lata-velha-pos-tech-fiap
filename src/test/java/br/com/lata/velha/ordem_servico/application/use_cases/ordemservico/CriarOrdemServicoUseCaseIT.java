package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.ordem_servico.application.ports.EmailProvider;
import br.com.lata.velha.ordem_servico.application.ports.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.*;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:criar-os-uc-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class CriarOrdemServicoUseCaseIT {

    @Autowired
    private CriarOrdemServicoUseCase useCase;

    @Autowired
    private EntityManager em;

    @MockBean
    private EmailProvider emailProvider;

    @MockBean
    private EmailTemplateProvider emailTemplateProvider;

    private Long veiculoId;
    private Long proprietarioId;
    private Long funcionarioId;
    private FuncionarioEntity funcionario;

    @BeforeEach
    void setUp() {
        RoleEntity role = new RoleEntity(null, "ATENDENTE");
        em.persist(role);

        CargoEntity cargo = new CargoEntity();
        cargo.setNome("ATENDENTE");
        cargo.setRoles(Set.of(role));
        em.persist(cargo);

        ProprietarioEntity proprietario = new ProprietarioEntity();
        proprietario.setNome("João Proprietário");
        proprietario.setEmail("joao@example.com");
        proprietario.setDocumento("35949343069");
        proprietario.setNumeroCelular("11999999999");
        proprietario.setAtivo(true);
        em.persist(proprietario);
        proprietarioId = proprietario.getId();

        VeiculoEntity veiculo = new VeiculoEntity();
        veiculo.setProprietario(proprietario);
        veiculo.setPlaca("ABC1D23");
        veiculo.setMarca("Fiat");
        veiculo.setModelo("Uno");
        veiculo.setAno(2020);
        veiculo.setCor("Branco");
        veiculo.setAtivo(true);
        em.persist(veiculo);
        veiculoId = veiculo.getId();

        this.funcionario = new FuncionarioEntity();
        funcionario.setNome("Maria Atendente");
        funcionario.setCargo(cargo);
        funcionario.setUserId(UUID.randomUUID());
        em.persist(funcionario);
        funcionarioId = funcionario.getId();

        em.flush();
    }

    @Test
    @DisplayName("deve criar OrdemServico com sucesso e persistir no banco")
    void deveCriarOrdemServicoComSucesso() {
        var funcionarioUserId = UserId.create(funcionario.getUserId());
        var input = new CriarOrdemServicoUseCase.Input(veiculoId, proprietarioId, funcionarioUserId, "Barulho ao frear");

        var output = useCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.id()).isNotNull();

        em.flush();
        em.clear();

        OrdemServicoEntity entity = em.find(OrdemServicoEntity.class, output.id());
        assertThat(entity).isNotNull();
        assertThat(entity.getStatus()).isEqualTo(StatusOrdemServico.RECEBIDA);
        assertThat(entity.getVeiculoId()).isEqualTo(veiculoId);
        assertThat(entity.getProprietarioId()).isEqualTo(proprietarioId);
        assertThat(entity.getAtendenteInicioId()).isEqualTo(funcionarioId);
        assertThat(entity.getReclamacaoCliente()).isEqualTo("Barulho ao frear");
    }

    @Test
    @DisplayName("deve persistir reclamação do cliente corretamente")
    void devePersistirReclamacaoCliente() {
        var funcionarioUserId = UserId.create(funcionario.getUserId());
        var input = new CriarOrdemServicoUseCase.Input(veiculoId, proprietarioId, funcionarioUserId, "Motor superaquecendo");

        var output = useCase.execute(input);

        em.flush();
        em.clear();

        OrdemServicoEntity entity = em.find(OrdemServicoEntity.class, output.id());
        assertThat(entity.getReclamacaoCliente()).isEqualTo("Motor superaquecendo");
    }

    @Test
    @DisplayName("deve persistir atendente e status RECEBIDA corretamente")
    void devePersistirAtendenteEStatus() {
        var funcionarioUserId = UserId.create(funcionario.getUserId());
        var input = new CriarOrdemServicoUseCase.Input(veiculoId, proprietarioId, funcionarioUserId, "Freio falhando");

        var output = useCase.execute(input);

        em.flush();
        em.clear();

        OrdemServicoEntity entity = em.find(OrdemServicoEntity.class, output.id());
        assertThat(entity.getAtendenteInicioId()).isEqualTo(funcionarioId);
        assertThat(entity.getStatus()).isEqualTo(StatusOrdemServico.RECEBIDA);
    }
}
