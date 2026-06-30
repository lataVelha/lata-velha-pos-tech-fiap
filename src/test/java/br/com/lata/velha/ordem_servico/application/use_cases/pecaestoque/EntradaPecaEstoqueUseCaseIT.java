package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:entrada-peca-estoque-uc-it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH"
})
@Transactional
class EntradaPecaEstoqueUseCaseIT {

    @Autowired private EntradaPecaEstoqueGateway gateway;
    @Autowired private EntityManager em;

    private EntradaPecaEstoqueUseCase useCase;

    private Long pecaId;
    private Long execucaoId;

    @BeforeEach
    void setUp() {
        useCase = new EntradaPecaEstoqueUseCase(gateway);

        PecaEntity peca = new PecaEntity();
        peca.setNome("Filtro de óleo");
        peca.setDescricao("Filtro premium");
        peca.setValor(new BigDecimal("35.00"));
        em.persist(peca);
        pecaId = peca.getId();

        OrdemServicoEntity os = new OrdemServicoEntity();
        os.setProprietarioId(1L);
        os.setVeiculoId(1L);
        os.setReclamacaoProprietario("test");
        os.setStatus(StatusOrdemServico.EM_EXECUCAO);
        os.setAtendenteInicioId(1L);
        os.setAtualizadoEm(LocalDateTime.now());
        em.persist(os);

        ServicoEntity servico = new ServicoEntity();
        servico.setNome("Serviço teste");
        servico.setDescricao("desc");
        em.persist(servico);

        ExecucaoServicoEntity execucao = new ExecucaoServicoEntity();
        execucao.setOrdemServico(os);
        execucao.setServico(servico);
        execucao.setStatusExecucaoServico(StatusExecucaoServico.AGUARDANDO_PECA);
        execucao.setValorMaoDeObra(new BigDecimal("100.00"));
        execucao.setAtualizadoEm(LocalDateTime.now());
        em.persist(execucao);
        execucaoId = execucao.getId();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("deve criar estoque e persistir quando não existe")
    void deveCriarEstoqueQuandoNaoExiste() {
        PecaEstoque response = useCase.execute(pecaId, new MovimentarPecaEstoqueRequest(10));

        assertThat(response.getQuantidadeArmazenada()).isEqualTo(10);
        assertThat(response.getQuantidadeDisponivel()).isEqualTo(10);

        PecaEstoqueEntity estoque = em.find(PecaEstoqueEntity.class, pecaId);
        assertThat(estoque).isNotNull();
        assertThat(estoque.getQuantidadeArmazenada()).isEqualTo(10);
        assertThat(estoque.getQuantidadeDisponivel()).isEqualTo(10);
    }

    @Test
    @DisplayName("deve somar ao estoque existente")
    void deveSomarAoEstoqueExistente() {
        PecaEstoqueEntity existente = new PecaEstoqueEntity(pecaId, 5, 5);
        em.persist(existente);
        em.flush();
        em.clear();

        PecaEstoque response = useCase.execute(pecaId, new MovimentarPecaEstoqueRequest(3));

        assertThat(response.getQuantidadeArmazenada()).isEqualTo(8);
        assertThat(response.getQuantidadeDisponivel()).isEqualTo(8);
    }

    @Test
    @DisplayName("deve reservar peças pendentes ao adicionar estoque")
    void deveReservarPecasPendentesAoAdicionarEstoque() {
        PecaAlocadaEntity pendente = new PecaAlocadaEntity();
        pendente.setPecaId(pecaId);
        pendente.setExecucaoServicoId(execucaoId);
        pendente.setValorUnitario(new BigDecimal("35.00"));
        pendente.setQuantidadeSolicitada(4);
        pendente.setQuantidadeReservada(0);
        pendente.setQuantidadeEncomendada(4);
        pendente.setQuantidadeInstalada(0);
        pendente.setStatus(StatusPecaAlocada.ENCOMENDA);
        pendente.setAtualizado(LocalDateTime.now());
        em.persist(pendente);
        em.flush();
        em.clear();

        useCase.execute(pecaId, new MovimentarPecaEstoqueRequest(10));

        em.flush();
        em.clear();

        PecaAlocadaEntity atualizada = em.find(PecaAlocadaEntity.class, pendente.getId());
        assertThat(atualizada.getStatus()).isEqualTo(StatusPecaAlocada.RESERVADA);
        assertThat(atualizada.getQuantidadeReservada()).isEqualTo(4);

        PecaEstoqueEntity estoque = em.find(PecaEstoqueEntity.class, pecaId);
        assertThat(estoque.getQuantidadeDisponivel()).isEqualTo(6);
    }

    @Test
    @DisplayName("deve mudar status da execução para APROVADO quando todas as peças são reservadas")
    void deveMudarStatusExecucaoParaAprovadoQuandoTodasPecasReservadas() {
        PecaAlocadaEntity pendente = new PecaAlocadaEntity();
        pendente.setPecaId(pecaId);
        pendente.setExecucaoServicoId(execucaoId);
        pendente.setValorUnitario(new BigDecimal("35.00"));
        pendente.setQuantidadeSolicitada(2);
        pendente.setQuantidadeReservada(0);
        pendente.setQuantidadeEncomendada(2);
        pendente.setQuantidadeInstalada(0);
        pendente.setStatus(StatusPecaAlocada.ENCOMENDA);
        pendente.setAtualizado(LocalDateTime.now());
        em.persist(pendente);
        em.flush();
        em.clear();

        useCase.execute(pecaId, new MovimentarPecaEstoqueRequest(5));

        em.flush();
        em.clear();

        ExecucaoServicoEntity execucaoAtualizada = em.find(ExecucaoServicoEntity.class, execucaoId);
        assertThat(execucaoAtualizada.getStatusExecucaoServico()).isEqualTo(StatusExecucaoServico.APROVADO);

        PecaAlocadaEntity pecaAtualizada = em.find(PecaAlocadaEntity.class, pendente.getId());
        assertThat(pecaAtualizada.getStatus()).isEqualTo(StatusPecaAlocada.RESERVADA);
    }

    @Test
    @DisplayName("deve lançar exceção quando peça não existe")
    void deveLancarExcecaoQuandoPecaNaoExiste() {
        Long idInexistente = 9999L;

        assertThatThrownBy(() -> useCase.execute(idInexistente, new MovimentarPecaEstoqueRequest(5)))
                .isInstanceOf(RuntimeException.class);
    }
}
