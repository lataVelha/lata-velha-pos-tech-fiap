package br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers;

import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.PecaEstoqueEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PecaEstoquePersistenceMapperTest {

    private final PecaEstoquePersistenceMapper mapper = new PecaEstoquePersistenceMapper();

    @Test
    void deveRetornarNullQuandoToDomainReceberNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void deveMapearEntityParaDomain() {
        PecaEstoqueEntity entity = new PecaEstoqueEntity();
        entity.setPecaId(3L);
        entity.setQuantidadeArmazenada(11);

        PecaEstoque domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getPecaId()).isEqualTo(3L);
        assertThat(domain.getQuantidadeArmazenada()).isEqualTo(11);
    }

    @Test
    void deveRetornarNullQuandoToEntityReceberNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void deveMapearDomainParaEntity() {
        PecaEstoque domain = new PecaEstoque(4L, 9);

        PecaEstoqueEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getPecaId()).isEqualTo(4L);
        assertThat(entity.getQuantidadeArmazenada()).isEqualTo(9);
    }
}
