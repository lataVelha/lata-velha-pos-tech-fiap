package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import br.com.lata.velha.ordem_servico.application.ports.EmailProvider;
import br.com.lata.velha.ordem_servico.application.ports.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.*;
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

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:aprovar-os-uc-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class AprovarOrdemServicoUseCaseIT {

    @Autowired private AprovarOrdemServicoUseCase useCase;
    @Autowired private EntityManager em;

    @MockBean private EmailProvider emailProvider;
    @MockBean private EmailTemplateProvider emailTemplateProvider;

    private Long funcionarioId;
    private Long osId;
    private Long execucaoId;
    private Long pecaId;

    @BeforeEach
    void setUp() {
        RoleEntity role = new RoleEntity(null, "ATENDENTE");
        em.persist(role);

        CargoEntity cargo = new CargoEntity();
        cargo.setNome("ATENDENTE");
        cargo.setRoles(Set.of(role));
        em.persist(cargo);

        FuncionarioEntity funcionario = new FuncionarioEntity();
        funcionario.setNome("Ana Atendente");
        funcionario.setCargo(cargo);
        funcionario.setUserId(UUID.randomUUID());
        em.persist(funcionario);
        funcionarioId = funcionario.getId();

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

        PecaEntity peca = new PecaEntity();
        peca.setNome("Filtro de óleo");
        peca.setDescricao("Filtro premium");
        peca.setValor(new BigDecimal("35.00"));
        em.persist(peca);
        pecaId = peca.getId();

        OrdemServicoEntity os = new OrdemServicoEntity();
        os.setProprietarioId(proprietario.getId());
        os.setVeiculoId(veiculo.getId());
        os.setReclamacaoCliente("Troca de óleo periódica");
        os.setStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
        os.setAtendenteInicioId(funcionarioId);
        os.setIniciadoEm(LocalDateTime.now());
        os.setAtualizadoEm(LocalDateTime.now());
        em.persist(os);
        osId = os.getId();

        ExecucaoServicoEntity execucao = new ExecucaoServicoEntity();
        execucao.setOrdemServico(os);
        execucao.setServico(servico);
        execucao.setStatusExecucaoServico(StatusExecucaoServico.PENDENTE);
        execucao.setValorMaoDeObra(new BigDecimal("150.00"));
        execucao.setAtualizadoEm(LocalDateTime.now());
        em.persist(execucao);
        execucaoId = execucao.getId();

        PecaAlocadaEntity pecaAlocada = new PecaAlocadaEntity();
        pecaAlocada.setExecucaoServicoId(execucao.getId());
        pecaAlocada.setPecaId(pecaId);
        pecaAlocada.setQuantidadeSolicitada(3);
        pecaAlocada.setQuantidadeReservada(0);
        pecaAlocada.setQuantidadeEncomendada(0);
        pecaAlocada.setStatus(StatusPecaAlocada.ORCAMENTO);
        pecaAlocada.setAtualizado(LocalDateTime.now());
        em.persist(pecaAlocada);

        PecaEstoqueEntity estoque = new PecaEstoqueEntity();
        estoque.setPecaId(pecaId);
        estoque.setQuantidadeArmazenada(10);
        estoque.setQuantidadeDisponivel(10);
        em.persist(estoque);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("deve aprovar OS e serviço com estoque suficiente e persistir no banco")
    void deveAprovarOsComEstoqueSuficiente() {
        var input = new AprovarOrdemServicoUseCase.Input(osId, funcionarioId,
                List.of(new AprovarOrdemServicoUseCase.Input.Servicos(execucaoId, StatusExecucaoServico.APROVADO)));

        var output = useCase.execute(input);

        assertThat(output.status()).isEqualTo(StatusOrdemServico.EM_EXECUCAO.name());

        em.flush();
        em.clear();

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        assertThat(osEntity.getStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);

        ExecucaoServicoEntity execEntity = em.find(ExecucaoServicoEntity.class, execucaoId);
        assertThat(execEntity.getStatusExecucaoServico()).isEqualTo(StatusExecucaoServico.APROVADO);

        PecaEstoqueEntity estoqueEntity = em.find(PecaEstoqueEntity.class, pecaId);
        assertThat(estoqueEntity.getQuantidadeDisponivel()).isEqualTo(7);
    }

    @Test
    @DisplayName("deve recusar execução quando não informada no input e persistir status RECUSADO")
    void deveRecusarExecucaoNaoInformada() {
        var input = new AprovarOrdemServicoUseCase.Input(osId, funcionarioId, List.of());

        useCase.execute(input);

        em.flush();
        em.clear();

        ExecucaoServicoEntity execEntity = em.find(ExecucaoServicoEntity.class, execucaoId);
        assertThat(execEntity.getStatusExecucaoServico()).isEqualTo(StatusExecucaoServico.RECUSADO);

        OrdemServicoEntity osEntity = em.find(OrdemServicoEntity.class, osId);
        assertThat(osEntity.getStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
    }

    @Test
    @DisplayName("deve reservar parcialmente quando estoque insuficiente e persistir peça como PARCIAL")
    void deveReservarParcialmenteQuandoEstoqueInsuficiente() {
        PecaEstoqueEntity estoqueEntity = em.find(PecaEstoqueEntity.class, pecaId);
        estoqueEntity.setQuantidadeArmazenada(2);
        estoqueEntity.setQuantidadeDisponivel(2);
        em.flush();

        var input = new AprovarOrdemServicoUseCase.Input(osId, funcionarioId,
                List.of(new AprovarOrdemServicoUseCase.Input.Servicos(execucaoId, StatusExecucaoServico.APROVADO)));

        useCase.execute(input);

        em.flush();
        em.clear();

        PecaEstoqueEntity estoqueAtualizado = em.find(PecaEstoqueEntity.class, pecaId);
        assertThat(estoqueAtualizado.getQuantidadeDisponivel()).isZero();

        ExecucaoServicoEntity execEntity = em.find(ExecucaoServicoEntity.class, execucaoId);
        PecaAlocadaEntity pecaAlocada = execEntity.getPecas().get(0);
        assertThat(pecaAlocada.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
        assertThat(pecaAlocada.getQuantidadeReservada()).isEqualTo(2);
        assertThat(pecaAlocada.getQuantidadeEncomendada()).isEqualTo(1);
    }

    @Test
    @DisplayName("deve encomendar tudo quando não há estoque disponível e persistir peça como PARCIAL")
    void deveEncomendarTudoQuandoSemEstoque() {
        PecaEstoqueEntity estoqueEntity = em.find(PecaEstoqueEntity.class, pecaId);
        estoqueEntity.setQuantidadeArmazenada(5);
        estoqueEntity.setQuantidadeDisponivel(0);
        em.flush();

        var input = new AprovarOrdemServicoUseCase.Input(osId, funcionarioId,
                List.of(new AprovarOrdemServicoUseCase.Input.Servicos(execucaoId, StatusExecucaoServico.APROVADO)));

        useCase.execute(input);

        em.flush();
        em.clear();

        ExecucaoServicoEntity execEntity = em.find(ExecucaoServicoEntity.class, execucaoId);
        PecaAlocadaEntity pecaAlocada = execEntity.getPecas().get(0);
        assertThat(pecaAlocada.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
        assertThat(pecaAlocada.getQuantidadeReservada()).isZero();
        assertThat(pecaAlocada.getQuantidadeEncomendada()).isEqualTo(3);
    }

    @Test
    @DisplayName("deve persistir atendenteId na execução após aprovação")
    void devePersistirAtendenteNaExecucao() {
        var input = new AprovarOrdemServicoUseCase.Input(osId, funcionarioId,
                List.of(new AprovarOrdemServicoUseCase.Input.Servicos(execucaoId, StatusExecucaoServico.APROVADO)));

        useCase.execute(input);

        em.flush();
        em.clear();

        ExecucaoServicoEntity execEntity = em.find(ExecucaoServicoEntity.class, execucaoId);
        assertThat(execEntity.getAtendenteId()).isEqualTo(funcionarioId);
    }
}
