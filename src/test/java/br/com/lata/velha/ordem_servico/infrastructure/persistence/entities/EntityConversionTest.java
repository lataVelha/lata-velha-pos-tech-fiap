package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import br.com.lata.velha.ordem_servico.domain.entities.*;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EntityConversionTest {

    // ------------------------------------------------------------------ PecaEntity

    @Test
    void pecaEntity_fromDomain_deveMapearTodosCampos() {
        var peca = new Peca(1L, "Pastilha", "Pastilha dianteira", new BigDecimal("49.90"), true);
        var entity = PecaEntity.fromDomain(peca);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNome()).isEqualTo("Pastilha");
        assertThat(entity.getDescricao()).isEqualTo("Pastilha dianteira");
        assertThat(entity.getValor()).isEqualByComparingTo("49.90");
        assertThat(entity.isAtivo()).isTrue();
    }

    @Test
    void pecaEntity_toDomain_deveMapearTodosCampos() {
        var entity = new PecaEntity(2L, "Filtro", "Filtro de óleo", new BigDecimal("20.00"), false);
        var domain = entity.toDomain();

        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getNome()).isEqualTo("Filtro");
        assertThat(domain.getDescricao()).isEqualTo("Filtro de óleo");
        assertThat(domain.getValor()).isEqualByComparingTo("20.00");
        assertThat(domain.isAtivo()).isFalse();
    }

    @Test
    void pecaEntity_roundTrip_devePreservarDados() {
        var original = new Peca(3L, "Vela", "Vela de ignição", new BigDecimal("15.00"), true);
        var roundTrip = PecaEntity.fromDomain(original).toDomain();

        assertThat(roundTrip.getId()).isEqualTo(original.getId());
        assertThat(roundTrip.getNome()).isEqualTo(original.getNome());
        assertThat(roundTrip.getValor()).isEqualByComparingTo(original.getValor());
    }

    // ------------------------------------------------------------------ ServicoEntity

    @Test
    void servicoEntity_toDomain_deveMapearTodosCampos() {
        var entity = new ServicoEntity();
        entity.setId(10L);
        entity.setNome("Troca de óleo");
        entity.setDescricao("Troca do óleo do motor");
        entity.setAtivo(true);

        var domain = entity.toDomain();

        assertThat(domain.getId()).isEqualTo(10L);
        assertThat(domain.getNome()).isEqualTo("Troca de óleo");
        assertThat(domain.getDescricao()).isEqualTo("Troca do óleo do motor");
        assertThat(domain.isAtivo()).isTrue();
    }

    @Test
    void servicoEntity_toDomain_deveMapearAtivoFalse() {
        var entity = new ServicoEntity();
        entity.setId(11L);
        entity.setNome("Alinhamento");
        entity.setDescricao("Alinhamento de rodas");
        entity.setAtivo(false);

        assertThat(entity.toDomain().isAtivo()).isFalse();
    }

    // ------------------------------------------------------------------ PecaEstoqueEntity

    @Test
    void pecaEstoqueEntity_fromDomain_deveMapearTodosCampos() {
        var domain = new PecaEstoque(5L, 100, 80);
        var entity = PecaEstoqueEntity.fromDomain(domain);

        assertThat(entity.getPecaId()).isEqualTo(5L);
        assertThat(entity.getQuantidadeArmazenada()).isEqualTo(100);
        assertThat(entity.getQuantidadeDisponivel()).isEqualTo(80);
    }

    @Test
    void pecaEstoqueEntity_toDomain_deveMapearTodosCampos() {
        var entity = new PecaEstoqueEntity(7L, 50, 30);
        var domain = entity.toDomain();

        assertThat(domain.getPecaId()).isEqualTo(7L);
        assertThat(domain.getQuantidadeArmazenada()).isEqualTo(50);
        assertThat(domain.getQuantidadeDisponivel()).isEqualTo(30);
    }

    @Test
    void pecaEstoqueEntity_roundTrip_devePreservarDados() {
        var original = new PecaEstoque(9L, 200, 150);
        var roundTrip = PecaEstoqueEntity.fromDomain(original).toDomain();

        assertThat(roundTrip.getPecaId()).isEqualTo(original.getPecaId());
        assertThat(roundTrip.getQuantidadeArmazenada()).isEqualTo(original.getQuantidadeArmazenada());
        assertThat(roundTrip.getQuantidadeDisponivel()).isEqualTo(original.getQuantidadeDisponivel());
    }

    // ------------------------------------------------------------------ PecaAlocadaEntity

    @Test
    void pecaAlocadaEntity_fromDomain_deveMapearTodosCampos() {
        var now = LocalDateTime.now();
        var domain = new PecaAlocada(
                10L, 5L, 1L, new BigDecimal("49.90"),
                4, 2, 2, 0,
                StatusPecaAlocada.PARCIAL, now
        );
        var entity = PecaAlocadaEntity.fromDomain(domain);

        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getPecaId()).isEqualTo(5L);
        assertThat(entity.getExecucaoServicoId()).isEqualTo(1L);
        assertThat(entity.getValorUnitario()).isEqualByComparingTo("49.90");
        assertThat(entity.getQuantidadeSolicitada()).isEqualTo(4);
        assertThat(entity.getQuantidadeReservada()).isEqualTo(2);
        assertThat(entity.getQuantidadeEncomendada()).isEqualTo(2);
        assertThat(entity.getQuantidadeInstalada()).isZero();
        assertThat(entity.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
        assertThat(entity.getAtualizado()).isEqualTo(now);
    }

    @Test
    void pecaAlocadaEntity_fromDomain_deveRetornarNullParaDomainNull() {
        assertThat(PecaAlocadaEntity.fromDomain(null)).isNull();
    }

    @Test
    void pecaAlocadaEntity_toDomain_deveMapearTodosCampos() {
        var now = LocalDateTime.now();
        var entity = new PecaAlocadaEntity(
                20L, 6L, 2L, new BigDecimal("30.00"),
                3, 1, 2, 1,
                StatusPecaAlocada.RESERVADA, now
        );
        var domain = entity.toDomain();

        assertThat(domain.getId()).isEqualTo(20L);
        assertThat(domain.getPecaId()).isEqualTo(6L);
        assertThat(domain.getExecucaoServicoId()).isEqualTo(2L);
        assertThat(domain.getValorUnitarioPeca()).isEqualByComparingTo("30.00");
        assertThat(domain.getQuantidadeSolicitada()).isEqualTo(3);
        assertThat(domain.getQuantidadeReservada()).isEqualTo(1);
        assertThat(domain.getQuantidadeEncomendada()).isEqualTo(2);
        assertThat(domain.getQuantidadeInstalada()).isEqualTo(1);
        assertThat(domain.getStatus()).isEqualTo(StatusPecaAlocada.RESERVADA);
    }

    // ------------------------------------------------------------------ ExecucaoServicoEntity

    @Test
    void execucaoServicoEntity_fromDomain_deveMapearTodosCampos() {
        var now = LocalDateTime.now();
        var execucao = new ExecucaoServico(
                1L, 10L, 100L,
                StatusExecucaoServico.APROVADO,
                new BigDecimal("200.00"),
                Set.of(),
                5L, 8L,
                now.minusHours(2), now, now
        );
        var entity = ExecucaoServicoEntity.fromDomain(execucao);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getServicoId()).isEqualTo(10L);
        assertThat(entity.getOrdemServicoId()).isEqualTo(100L);
        assertThat(entity.getStatusExecucaoServico()).isEqualTo(StatusExecucaoServico.APROVADO);
        assertThat(entity.getValorMaoDeObra()).isEqualByComparingTo("200.00");
        assertThat(entity.getAtendenteAprovacaoId()).isEqualTo(5L);
        assertThat(entity.getMecanicoResponsavelId()).isEqualTo(8L);
        assertThat(entity.getPecas()).isEmpty();
    }

    @Test
    void execucaoServicoEntity_toDomain_deveMapearTodosCampos() {
        var entity = new ExecucaoServicoEntity();
        entity.setId(2L);
        entity.setOrdemServicoId(200L);
        entity.setServicoId(20L);
        entity.setStatusExecucaoServico(StatusExecucaoServico.FINALIZADO);
        entity.setValorMaoDeObra(new BigDecimal("350.00"));
        entity.setMecanicoResponsavelId(9L);

        var domain = entity.toDomain();

        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getOrdemServicoId()).isEqualTo(200L);
        assertThat(domain.getServicoId()).isEqualTo(20L);
        assertThat(domain.getStatus()).isEqualTo(StatusExecucaoServico.FINALIZADO);
        assertThat(domain.getValorMaoDeObra()).isEqualByComparingTo("350.00");
        assertThat(domain.getMecanicoResponsavelId()).isEqualTo(9L);
        assertThat(domain.getPecas()).isEmpty();
    }

    @Test
    void execucaoServicoEntity_roundTrip_comPecasDevePreservarDados() {
        var pecaAlocada = new PecaAlocada(
                30L, 7L, 1L, new BigDecimal("25.00"),
                2, 2, 0, 0,
                StatusPecaAlocada.RESERVADA, LocalDateTime.now()
        );
        var execucao = new ExecucaoServico(
                3L, 12L, 102L,
                StatusExecucaoServico.EM_EXECUCAO,
                new BigDecimal("150.00"),
                Set.of(pecaAlocada),
                null, 9L,
                null, null, null
        );
        var roundTrip = ExecucaoServicoEntity.fromDomain(execucao).toDomain();

        assertThat(roundTrip.getPecas()).hasSize(1);
        assertThat(roundTrip.getPecas().iterator().next().getId()).isEqualTo(30L);
    }
}
