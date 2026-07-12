package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoSemProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.gateways.EmailProvider;
import br.com.lata.velha.ordem_servico.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoGateway;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.CriarProprietarioGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.NotificarCadastroProprietarioUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.veiculo.CriarVeiculoGateway;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.CargoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.FuncionarioEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.OrdemServicoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.PecaEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ProprietarioEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ServicoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.gateways.OrdemServicoGatewayImpl;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:criar-os-completa-uc-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class CriarOrdemServicoCompletaUseCaseIT {

    @SpyBean
    private OrdemServicoGatewayImpl gatewayImpl;

    @Autowired private CriarOrdemServicoGateway criarOrdemServicoGateway;
    @Autowired private CriarProprietarioGateway criarProprietarioGateway;
    @Autowired private CriarVeiculoGateway criarVeiculoGateway;
    @Autowired private AdicionarServicoGateway adicionarServicoGateway;
    @Autowired private NotificarOrdemServicoGateway notificarGateway;
    @Autowired private EntityManager em;

    @MockBean private EmailProvider emailProvider;
    @MockBean private EmailTemplateProvider emailTemplateProvider;

    private CriarOrdemServicoCompletaUseCase useCase;

    private Long funcionarioId;
    private FuncionarioEntity funcionario;

    @BeforeEach
    void setUp() {
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            OrdemServicoProjection proj = mock(OrdemServicoProjection.class);
            when(proj.getId()).thenReturn(id);
            return proj;
        }).when(gatewayImpl).getOrdemServicoProjectionById(anyLong());

        var notificarService = new NotificarOrdemServicoService(notificarGateway, emailProvider, emailTemplateProvider);
        var notificarCadastroProprietario = new NotificarCadastroProprietarioUseCase(emailProvider, emailTemplateProvider);
        useCase = new CriarOrdemServicoCompletaUseCase(
                criarOrdemServicoGateway, criarProprietarioGateway, criarVeiculoGateway,
                adicionarServicoGateway, notificarService, notificarCadastroProprietario);

        RoleEntity role = new RoleEntity(null, "ATENDENTE");
        em.persist(role);

        CargoEntity cargo = new CargoEntity();
        cargo.setNome("ATENDENTE");
        cargo.setRoles(Set.of(role));
        em.persist(cargo);

        funcionario = new FuncionarioEntity();
        funcionario.setNome("Maria Atendente");
        funcionario.setCargo(cargo);
        funcionario.setUserId(UUID.randomUUID());
        em.persist(funcionario);
        funcionarioId = funcionario.getId();

        em.flush();
    }

    private CriarOrdemServicoCompletaUseCase.Input buildInput(List<AdicionarServicoUseCase.Input.ServicoAdicionar> servicos) {
        var funcionarioUserId = UserId.create(funcionario.getUserId());
        var proprietarioRequest = new ProprietarioRequest(
                "João da Silva", "joao@example.com", "359.493.430-69", "(11) 99999-9999", null);
        var veiculoRequest = new VeiculoSemProprietarioRequest("ABC1D23", "Fiat", "Uno", 2020, "Prata");

        return new CriarOrdemServicoCompletaUseCase.Input(
                proprietarioRequest, veiculoRequest, funcionarioUserId, "Barulho ao frear", servicos);
    }

    @Test
    @DisplayName("deve cadastrar proprietário, veículo e criar OS RECEBIDA em uma única chamada")
    void deveCriarOrdemServicoCompletaComSucesso() {
        var input = buildInput(List.of());

        OrdemServicoProjection output = useCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.getId()).isNotNull();

        em.flush();
        em.clear();

        OrdemServicoEntity entity = em.find(OrdemServicoEntity.class, output.getId());
        assertThat(entity).isNotNull();
        assertThat(entity.getStatus()).isEqualTo(StatusOrdemServico.RECEBIDA);
        assertThat(entity.getAtendenteInicioId()).isEqualTo(funcionarioId);
        assertThat(entity.getReclamacaoProprietario()).isEqualTo("Barulho ao frear");

        ProprietarioEntity proprietario = em.find(ProprietarioEntity.class, entity.getProprietarioId());
        assertThat(proprietario).isNotNull();
        assertThat(proprietario.getNome()).isEqualTo("João da Silva");
        assertThat(proprietario.getDocumento()).isEqualTo("35949343069");

        var veiculo = em.find(br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.VeiculoEntity.class, entity.getVeiculoId());
        assertThat(veiculo).isNotNull();
        assertThat(veiculo.getProprietario().getId()).isEqualTo(proprietario.getId());
        assertThat(veiculo.getPlaca()).isEqualTo("ABC1D23");
    }

    @Test
    @DisplayName("deve criar a OS já com múltiplos serviços, cada um com múltiplas peças, permanecendo RECEBIDA")
    void deveCriarOrdemServicoCompletaComServicosEPecas() {
        ServicoEntity trocaOleo = new ServicoEntity();
        trocaOleo.setNome("Troca de óleo");
        trocaOleo.setDescricao("Troca completa de óleo");
        em.persist(trocaOleo);

        PecaEntity filtroOleo = new PecaEntity();
        filtroOleo.setNome("Filtro de óleo");
        filtroOleo.setDescricao("Filtro premium");
        filtroOleo.setValor(new BigDecimal("35.00"));
        em.persist(filtroOleo);

        PecaEntity oleoMotor = new PecaEntity();
        oleoMotor.setNome("Óleo de motor");
        oleoMotor.setDescricao("5W30 sintético");
        oleoMotor.setValor(new BigDecimal("60.00"));
        em.persist(oleoMotor);

        em.flush();

        var servicos = List.of(new AdicionarServicoUseCase.Input.ServicoAdicionar(
                trocaOleo.getId(),
                List.of(
                        new AdicionarServicoUseCase.Input.PecaNecessaria(filtroOleo.getId(), 1),
                        new AdicionarServicoUseCase.Input.PecaNecessaria(oleoMotor.getId(), 4)
                ),
                new BigDecimal("150.00")
        ));

        var input = buildInput(servicos);

        OrdemServicoProjection output = useCase.execute(input);

        em.flush();
        em.clear();

        OrdemServicoEntity entity = em.find(OrdemServicoEntity.class, output.getId());
        assertThat(entity.getStatus()).isEqualTo(StatusOrdemServico.RECEBIDA);
        assertThat(entity.getServicos()).hasSize(1);
        assertThat(entity.getServicos().get(0).getPecas()).hasSize(2);
    }
}
