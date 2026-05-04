package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.ordem_servico.application.ports.EmailProvider;
import br.com.lata.velha.ordem_servico.application.ports.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:finalizar-servico-uc-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class FinalizarServicoUseCaseIT {

    @Autowired private FinalizarServicoUseCase useCase;
    @Autowired private EntityManager em;

    @MockBean private EmailProvider emailProvider;
    @MockBean private EmailTemplateProvider emailTemplateProvider;

    private Long osId;
    private Long execucaoId;
    private Long mecanicoId;
    private UUID mecanicoUserId;

    @BeforeEach
    void setUp() {
        RoleEntity role = new RoleEntity(null, "MECANICO");
        em.persist(role);

        CargoEntity cargo = new CargoEntity();
        cargo.setNome("MECANICO");
        cargo.setRoles(Set.of(role));
        em.persist(cargo);

        FuncionarioEntity mecanico = new FuncionarioEntity();
        mecanico.setNome("Carlos Mecânico");
        mecanico.setCargo(cargo);
        mecanicoUserId = UUID.randomUUID();
        mecanico.setUserId(mecanicoUserId);
        em.persist(mecanico);
        mecanicoId = mecanico.getId();

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

        OrdemServicoEntity os = new OrdemServicoEntity();
        os.setProprietarioId(proprietario.getId());
        os.setVeiculoId(veiculo.getId());
        os.setReclamacaoProprietario("Troca de óleo periódica");
        os.setStatus(StatusOrdemServico.EM_EXECUCAO);
        os.setAtendenteInicioId(1L);
        os.setIniciadoEm(LocalDateTime.now());
        os.setAtualizadoEm(LocalDateTime.now());
        em.persist(os);
        osId = os.getId();

        ExecucaoServicoEntity execucao = new ExecucaoServicoEntity();
        execucao.setOrdemServico(os);
        execucao.setServico(servico);
        execucao.setStatusExecucaoServico(StatusExecucaoServico.EM_EXECUCAO);
        execucao.setValorMaoDeObra(new BigDecimal("150.00"));
        execucao.setMecanicoResponsavelId(mecanicoId);
        execucao.setIniciadoEm(LocalDateTime.now());
        execucao.setAtualizadoEm(LocalDateTime.now());
        em.persist(execucao);
        execucaoId = execucao.getId();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("deve finalizar execução e persistir status FINALIZADO")
    void deveFinalizarExecucaoEPersistir() {
        useCase.execute(new FinalizarServicoUseCase.Input(osId, execucaoId, UserId.create(mecanicoUserId)));

        em.flush();
        em.clear();

        ExecucaoServicoEntity execEntity = em.find(ExecucaoServicoEntity.class, execucaoId);
        assertThat(execEntity.getStatusExecucaoServico()).isEqualTo(StatusExecucaoServico.FINALIZADO);
        assertThat(execEntity.getTerminadoEm()).isNotNull();
    }

    @Test
    @DisplayName("deve finalizar OS quando último serviço é finalizado")
    void deveFinalizarOsQuandoUltimoServico() {
        useCase.execute(new FinalizarServicoUseCase.Input(osId, execucaoId, UserId.create(mecanicoUserId)));

        em.flush();
        em.clear();

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        assertThat(osEntity.getStatus()).isEqualTo(StatusOrdemServico.FINALIZADA);
        assertThat(osEntity.getFinalizadoEm()).isNotNull();
        assertThat(osEntity.getMecanicoResponsavelId()).isEqualTo(mecanicoId);
    }

    @Test
    @DisplayName("deve manter OS EM_EXECUCAO quando existem outros serviços em andamento")
    void deveMantarOsEmExecucaoComOutrosServicos() {
        ServicoEntity servico2 = new ServicoEntity();
        servico2.setNome("Alinhamento");
        servico2.setDescricao("Alinhamento completo");
        em.persist(servico2);

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        ExecucaoServicoEntity execucao2 = new ExecucaoServicoEntity();
        execucao2.setOrdemServico(osEntity);
        execucao2.setServico(servico2);
        execucao2.setStatusExecucaoServico(StatusExecucaoServico.APROVADO);
        execucao2.setValorMaoDeObra(new BigDecimal("80.00"));
        execucao2.setAtualizadoEm(LocalDateTime.now());
        em.persist(execucao2);
        em.flush();

        useCase.execute(new FinalizarServicoUseCase.Input(osId, execucaoId, UserId.create(mecanicoUserId)));

        em.flush();
        em.clear();

        OrdemServicoEntity osAtualizada = em.find(OrdemServicoEntity.class, osId);
        assertThat(osAtualizada.getStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
    }

    @Test
    @DisplayName("deve instalar peças e atualizar status para INSTALADA ao finalizar")
    void deveInstalarPecasAoFinalizar() {
        PecaEntity peca = new PecaEntity();
        peca.setNome("Filtro de óleo");
        peca.setDescricao("Filtro premium");
        peca.setValor(new BigDecimal("35.00"));
        em.persist(peca);

        PecaAlocadaEntity pecaAlocada = new PecaAlocadaEntity();
        pecaAlocada.setExecucaoServicoId(execucaoId);
        pecaAlocada.setPecaId(peca.getId());
        pecaAlocada.setValorUnitario(new BigDecimal("35.00"));
        pecaAlocada.setQuantidadeSolicitada(2);
        pecaAlocada.setQuantidadeReservada(2);
        pecaAlocada.setQuantidadeEncomendada(0);
        pecaAlocada.setQuantidadeInstalada(0);
        pecaAlocada.setStatus(StatusPecaAlocada.RESERVADA);
        pecaAlocada.setAtualizado(LocalDateTime.now());
        em.persist(pecaAlocada);

        PecaEstoqueEntity estoqueEntity = new PecaEstoqueEntity();
        estoqueEntity.setPecaId(peca.getId());
        estoqueEntity.setQuantidadeArmazenada(10);
        estoqueEntity.setQuantidadeDisponivel(8);
        em.persist(estoqueEntity);
        em.flush();

        useCase.execute(new FinalizarServicoUseCase.Input(osId, execucaoId, UserId.create(mecanicoUserId)));

        em.flush();
        em.clear();

        ExecucaoServicoEntity execEntity = em.find(ExecucaoServicoEntity.class, execucaoId);
        PecaAlocadaEntity pecaAtualizada = execEntity.getPecas().iterator().next();
        assertThat(pecaAtualizada.getStatus()).isEqualTo(StatusPecaAlocada.INSTALADA);
        assertThat(pecaAtualizada.getQuantidadeInstalada()).isEqualTo(2);
    }

    @Test
    @DisplayName("deve reduzir a quantidade armazenada no estoque ao finalizar execução com peças")
    void deveRetirarEstoqueNoBancoDeDadosAoFinalizar() {
        PecaEntity peca = new PecaEntity();
        peca.setNome("Filtro de óleo");
        peca.setDescricao("Filtro premium");
        peca.setValor(new BigDecimal("35.00"));
        em.persist(peca);

        PecaAlocadaEntity pecaAlocada = new PecaAlocadaEntity();
        pecaAlocada.setExecucaoServicoId(execucaoId);
        pecaAlocada.setPecaId(peca.getId());
        pecaAlocada.setValorUnitario(new BigDecimal("35.00"));
        pecaAlocada.setQuantidadeSolicitada(2);
        pecaAlocada.setQuantidadeReservada(2);
        pecaAlocada.setQuantidadeEncomendada(0);
        pecaAlocada.setQuantidadeInstalada(0);
        pecaAlocada.setStatus(StatusPecaAlocada.RESERVADA);
        pecaAlocada.setAtualizado(LocalDateTime.now());
        em.persist(pecaAlocada);

        PecaEstoqueEntity estoqueEntity = new PecaEstoqueEntity();
        estoqueEntity.setPecaId(peca.getId());
        estoqueEntity.setQuantidadeArmazenada(10);
        estoqueEntity.setQuantidadeDisponivel(8);
        em.persist(estoqueEntity);
        em.flush();

        useCase.execute(new FinalizarServicoUseCase.Input(osId, execucaoId, UserId.create(mecanicoUserId)));

        em.flush();
        em.clear();

        PecaEstoqueEntity estoqueAtualizado = em.find(PecaEstoqueEntity.class, peca.getId());
        assertThat(estoqueAtualizado.getQuantidadeArmazenada()).isEqualTo(8);
        assertThat(estoqueAtualizado.getQuantidadeDisponivel()).isEqualTo(8);
    }

    @Test
    @DisplayName("deve reduzir estoque de múltiplas peças ao finalizar execução")
    void deveRetirarEstoqueDeMultiplasPecasAoFinalizar() {
        PecaEntity peca1 = new PecaEntity();
        peca1.setNome("Filtro de óleo");
        peca1.setDescricao("Filtro premium");
        peca1.setValor(new BigDecimal("35.00"));
        em.persist(peca1);

        PecaEntity peca2 = new PecaEntity();
        peca2.setNome("Filtro de ar");
        peca2.setDescricao("Filtro de ar premium");
        peca2.setValor(new BigDecimal("25.00"));
        em.persist(peca2);

        PecaAlocadaEntity pecaAlocada1 = new PecaAlocadaEntity();
        pecaAlocada1.setExecucaoServicoId(execucaoId);
        pecaAlocada1.setPecaId(peca1.getId());
        pecaAlocada1.setValorUnitario(new BigDecimal("35.00"));
        pecaAlocada1.setQuantidadeSolicitada(2);
        pecaAlocada1.setQuantidadeReservada(2);
        pecaAlocada1.setQuantidadeEncomendada(0);
        pecaAlocada1.setQuantidadeInstalada(0);
        pecaAlocada1.setStatus(StatusPecaAlocada.RESERVADA);
        pecaAlocada1.setAtualizado(LocalDateTime.now());
        em.persist(pecaAlocada1);

        PecaAlocadaEntity pecaAlocada2 = new PecaAlocadaEntity();
        pecaAlocada2.setExecucaoServicoId(execucaoId);
        pecaAlocada2.setPecaId(peca2.getId());
        pecaAlocada2.setValorUnitario(new BigDecimal("25.00"));
        pecaAlocada2.setQuantidadeSolicitada(3);
        pecaAlocada2.setQuantidadeReservada(3);
        pecaAlocada2.setQuantidadeEncomendada(0);
        pecaAlocada2.setQuantidadeInstalada(0);
        pecaAlocada2.setStatus(StatusPecaAlocada.RESERVADA);
        pecaAlocada2.setAtualizado(LocalDateTime.now());
        em.persist(pecaAlocada2);

        PecaEstoqueEntity estoque1 = new PecaEstoqueEntity();
        estoque1.setPecaId(peca1.getId());
        estoque1.setQuantidadeArmazenada(10);
        estoque1.setQuantidadeDisponivel(8);
        em.persist(estoque1);

        PecaEstoqueEntity estoque2 = new PecaEstoqueEntity();
        estoque2.setPecaId(peca2.getId());
        estoque2.setQuantidadeArmazenada(15);
        estoque2.setQuantidadeDisponivel(12);
        em.persist(estoque2);
        em.flush();

        useCase.execute(new FinalizarServicoUseCase.Input(osId, execucaoId, UserId.create(mecanicoUserId)));

        em.flush();
        em.clear();

        assertThat(em.find(PecaEstoqueEntity.class, peca1.getId()).getQuantidadeArmazenada()).isEqualTo(8);
        assertThat(em.find(PecaEstoqueEntity.class, peca2.getId()).getQuantidadeArmazenada()).isEqualTo(12);
    }

    @Test
    @DisplayName("deve lançar exceção quando não existe registro de estoque para peça da execução")
    void deveLancarExcecaoQuandoNaoHaRegistroDeEstoque() {
        PecaEntity peca = new PecaEntity();
        peca.setNome("Filtro de óleo");
        peca.setDescricao("Filtro premium");
        peca.setValor(new BigDecimal("35.00"));
        em.persist(peca);

        PecaAlocadaEntity pecaAlocada = new PecaAlocadaEntity();
        pecaAlocada.setExecucaoServicoId(execucaoId);
        pecaAlocada.setPecaId(peca.getId());
        pecaAlocada.setValorUnitario(new BigDecimal("35.00"));
        pecaAlocada.setQuantidadeSolicitada(2);
        pecaAlocada.setQuantidadeReservada(2);
        pecaAlocada.setQuantidadeEncomendada(0);
        pecaAlocada.setQuantidadeInstalada(0);
        pecaAlocada.setStatus(StatusPecaAlocada.RESERVADA);
        pecaAlocada.setAtualizado(LocalDateTime.now());
        em.persist(pecaAlocada);
        em.flush();

        assertThatThrownBy(() -> useCase.execute(new FinalizarServicoUseCase.Input(osId, execucaoId, UserId.create(mecanicoUserId))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("estoque");
    }
}
