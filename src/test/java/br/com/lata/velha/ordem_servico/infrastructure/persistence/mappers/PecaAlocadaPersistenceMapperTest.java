package br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers;

import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ExecucaoServicoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.PecaAlocadaEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.PecaEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PecaAlocadaPersistenceMapperTest {

    private final PecaAlocadaPersistenceMapper mapper = new PecaAlocadaPersistenceMapper();

    @Test
    void deveRetornarNullQuandoToDomainReceberNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void deveMapearEntityParaDomainComRelacionamentos() {
        PecaAlocadaEntity entity = new PecaAlocadaEntity();
        PecaEntity peca = new PecaEntity();
        ExecucaoServicoEntity execucaoServico = new ExecucaoServicoEntity();
        LocalDateTime atualizado = LocalDateTime.now();

        peca.setId(9L);
        execucaoServico.setId(15L);

        entity.setId(1L);
        entity.setPeca(peca);
        entity.setExecucaoServico(execucaoServico);
        entity.setQuantidadeSolicitada(5);
        entity.setQuantidadeReservada(2);
        entity.setQuantidadeEncomendada(3);
        entity.setStatus(StatusPecaAlocada.PARCIAL);
        entity.setAtualizado(atualizado);

        PecaAlocada domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getPecaId()).isEqualTo(9L);
        assertThat(domain.getExecucaoServicoId()).isEqualTo(15L);
        assertThat(domain.getQuantidadeSolicitada()).isEqualTo(5);
        assertThat(domain.getQuantidadeReservada()).isEqualTo(2);
        assertThat(domain.getQuantidadeEncomendada()).isEqualTo(3);
        assertThat(domain.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
        assertThat(domain.getAtualizado()).isEqualTo(atualizado);
    }

    @Test
    void deveMapearEntityParaDomainSemRelacionamentos() {
        PecaAlocadaEntity entity = new PecaAlocadaEntity();
        entity.setId(2L);
        entity.setQuantidadeSolicitada(4);
        entity.setQuantidadeReservada(0);
        entity.setQuantidadeEncomendada(4);
        entity.setStatus(StatusPecaAlocada.ENCOMENDA);

        PecaAlocada domain = mapper.toDomain(entity);

        assertThat(domain.getPecaId()).isNull();
        assertThat(domain.getExecucaoServicoId()).isNull();
    }

    @Test
    void deveRetornarNullQuandoToEntityReceberNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void deveMapearDomainParaEntityComIdsRelacionados() {
        LocalDateTime atualizado = LocalDateTime.now();
        PecaAlocada domain = new PecaAlocada(1L, 8L, 10L, 6, 2, 4, StatusPecaAlocada.PARCIAL, atualizado);

        PecaAlocadaEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getQuantidadeSolicitada()).isEqualTo(6);
        assertThat(entity.getQuantidadeReservada()).isEqualTo(2);
        assertThat(entity.getQuantidadeEncomendada()).isEqualTo(4);
        assertThat(entity.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
        assertThat(entity.getAtualizado()).isEqualTo(atualizado);
        assertThat(entity.getPeca()).isNotNull();
        assertThat(entity.getPeca().getId()).isEqualTo(8L);
        assertThat(entity.getExecucaoServico()).isNotNull();
        assertThat(entity.getExecucaoServico().getId()).isEqualTo(10L);
    }

    @Test
    void deveMapearDomainParaEntitySemIdsRelacionados() {
        PecaAlocada domain = new PecaAlocada(1L, null, null, 6, 0, 6, StatusPecaAlocada.ENCOMENDA, null);

        PecaAlocadaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getPeca()).isNull();
        assertThat(entity.getExecucaoServico()).isNull();
    }
}
