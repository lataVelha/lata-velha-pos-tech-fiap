package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.ordem_servico.application.gateways.EmailProvider;
import br.com.lata.velha.ordem_servico.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.*;
import br.com.lata.velha.shared.application.logging.Logger;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:adicionar-servico-uc-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class AdicionarServicoUseCaseIT {

    @Autowired private AdicionarServicoGateway gateway;
    @Autowired private EntityManager em;
    @Autowired private Logger logger;

    @MockBean private EmailProvider emailProvider;
    @MockBean private EmailTemplateProvider emailTemplateProvider;

    private AdicionarServicoUseCase useCase;

    private Long osId;
    private Long servicoId;
    private Long pecaId;
    private Long atendenteId;

    @BeforeEach
    void setUp() {
        useCase = new AdicionarServicoUseCase(gateway, logger);

        RoleEntity role = new RoleEntity(null, "ATENDENTE");
        em.persist(role);

        CargoEntity cargo = new CargoEntity();
        cargo.setNome("ATENDENTE");
        cargo.setRoles(Set.of(role));
        em.persist(cargo);

        FuncionarioEntity atendente = new FuncionarioEntity();
        atendente.setNome("Ana Atendente");
        atendente.setCargo(cargo);
        atendente.setUserId(UUID.randomUUID());
        em.persist(atendente);
        atendenteId = atendente.getId();

        ProprietarioEntity proprietario = new ProprietarioEntity();
        proprietario.setNome("João Proprietário");
        proprietario.setEmail("joao@example.com");
        proprietario.setDocumento("35949343069");
        proprietario.setNumeroCelular("11999999999");
        proprietario.setAtivo(true);
        em.persist(proprietario);

        VeiculoEntity veiculo = new VeiculoEntity();
        veiculo.setProprietario(proprietario);
        veiculo.setPlaca("XYZ9A99");
        veiculo.setMarca("Honda");
        veiculo.setModelo("Civic");
        veiculo.setAno(2022);
        veiculo.setCor("Prata");
        veiculo.setAtivo(true);
        em.persist(veiculo);

        ServicoEntity servico = new ServicoEntity();
        servico.setNome("Troca de óleo");
        servico.setDescricao("Troca completa de óleo");
        em.persist(servico);
        servicoId = servico.getId();

        PecaEntity peca = new PecaEntity();
        peca.setNome("Filtro de óleo");
        peca.setDescricao("Filtro premium");
        peca.setValor(new BigDecimal("35.00"));
        em.persist(peca);
        pecaId = peca.getId();

        OrdemServicoEntity os = new OrdemServicoEntity();
        os.setProprietarioId(proprietario.getId());
        os.setVeiculoId(veiculo.getId());
        os.setReclamacaoProprietario("Troca de óleo periódica");
        os.setStatus(StatusOrdemServico.EM_DIAGNOSTICO);
        os.setAtendenteInicioId(atendenteId);
        os.setAtualizadoEm(LocalDateTime.now());
        em.persist(os);
        osId = os.getId();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("deve adicionar serviço sem peças e persistir ExecucaoServico")
    void deveAdicionarServicoSemPecas() {
        var input = new AdicionarServicoUseCase.Input(osId, List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(servicoId, List.of(), new BigDecimal("150.00"))));

        useCase.execute(input);

        em.flush();
        em.clear();

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        assertThat(osEntity.getServicos()).hasSize(1);
        ExecucaoServicoEntity exec = osEntity.getServicos().get(0);
        assertThat(exec.getStatusExecucaoServico()).isEqualTo(StatusExecucaoServico.PENDENTE);
        assertThat(exec.getValorMaoDeObra()).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName("deve adicionar serviço com peça e persistir PecaAlocada")
    void deveAdicionarServicoComPeca() {
        var input = new AdicionarServicoUseCase.Input(osId, List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(servicoId,
                        List.of(new AdicionarServicoUseCase.Input.PecaNecessaria(pecaId, 3)),
                        new BigDecimal("150.00"))));

        useCase.execute(input);

        em.flush();
        em.clear();

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        assertThat(osEntity.getServicos()).hasSize(1);
        ExecucaoServicoEntity exec = osEntity.getServicos().get(0);
        assertThat(exec.getPecas()).hasSize(1);

        PecaAlocadaEntity pecaAlocada = exec.getPecas().iterator().next();
        assertThat(pecaAlocada.getPecaId()).isEqualTo(pecaId);
        assertThat(pecaAlocada.getQuantidadeSolicitada()).isEqualTo(3);
        assertThat(pecaAlocada.getValorUnitario()).isEqualByComparingTo("35.00");
        assertThat(pecaAlocada.getStatus()).isEqualTo(StatusPecaAlocada.PENDENTE);
    }

    @Test
    @DisplayName("deve adicionar múltiplos serviços distintos")
    void deveAdicionarMultiplosServicos() {
        ServicoEntity servico2 = new ServicoEntity();
        servico2.setNome("Alinhamento");
        servico2.setDescricao("Alinhamento completo");
        em.persist(servico2);
        em.flush();

        var input = new AdicionarServicoUseCase.Input(osId, List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(servicoId, List.of(), new BigDecimal("150.00")),
                new AdicionarServicoUseCase.Input.ServicoAdicionar(servico2.getId(), List.of(), new BigDecimal("80.00"))));

        useCase.execute(input);

        em.flush();
        em.clear();

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        assertThat(osEntity.getServicos()).hasSize(2);
    }

    @Test
    @DisplayName("deve lançar exceção quando serviços duplicados no input e não persistir nada")
    void deveLancarExcecaoParaServicoDuplicadoENaoPersistir() {
        var input = new AdicionarServicoUseCase.Input(osId, List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(servicoId, List.of(), new BigDecimal("150.00")),
                new AdicionarServicoUseCase.Input.ServicoAdicionar(servicoId, List.of(), new BigDecimal("200.00"))));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("duplicados");

        em.flush();
        em.clear();

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        assertThat(osEntity.getServicos()).isEmpty();
    }

    @Test
    @DisplayName("deve lançar exceção quando peça informada não existe")
    void deveLancarExcecaoQuandoPecaNaoExiste() {
        Long pecaInexistente = 9999L;

        var input = new AdicionarServicoUseCase.Input(osId, List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(servicoId,
                        List.of(new AdicionarServicoUseCase.Input.PecaNecessaria(pecaInexistente, 1)),
                        new BigDecimal("150.00"))));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não existem");
    }
}
