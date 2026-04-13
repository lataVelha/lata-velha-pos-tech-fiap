package br.com.lata.velha.ordemDeServico.infrastructure.persistence.mappers;

import br.com.lata.velha.ordemDeServico.domain.entities.Peca;
import br.com.lata.velha.ordemDeServico.infrastructure.persistence.entities.PecaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PecaPersistenceMapperTest {

    private final PecaPersistenceMapper mapper = new PecaPersistenceMapper();

    @Test
    @DisplayName("deve retornar null ao converter entity nula para domínio")
    void shouldReturnNullWhenEntityIsNull() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("deve converter entity para domínio corretamente")
    void shouldMapEntityToDomain() {
        PecaEntity entity = buildEntity(10L, "Pastilha", "Pastilha dianteira", new BigDecimal("150.00"), true);

        Peca result = mapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Pastilha", result.getNome());
        assertEquals("Pastilha dianteira", result.getDescricao());
        assertEquals(new BigDecimal("150.00"), result.getValor());
        assertTrue(result.isAtivo());
    }

    @Test
    @DisplayName("deve preservar ativo=false ao converter entity para domínio")
    void shouldPreserveInactiveStatusWhenMappingToDomain() {
        PecaEntity entity = buildEntity(11L, "Óleo", "Óleo sintético", new BigDecimal("59.90"), false);

        Peca result = mapper.toDomain(entity);

        assertNotNull(result);
        assertFalse(result.isAtivo());
    }

    @Test
    @DisplayName("deve retornar null ao converter domínio nulo para entity")
    void shouldReturnNullWhenDomainIsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("deve converter domínio para entity corretamente")
    void shouldMapDomainToEntity() {
        Peca domain = new Peca(20L, "Filtro", "Filtro de óleo", new BigDecimal("35.00"), true);

        PecaEntity result = mapper.toEntity(domain);

        assertNotNull(result);
        assertEquals(20L, result.getId());
        assertEquals("Filtro", result.getNome());
        assertEquals("Filtro de óleo", result.getDescricao());
        assertEquals(new BigDecimal("35.00"), result.getValor());
        assertTrue(result.isAtivo());
    }

    @Test
    @DisplayName("deve preservar ativo=false ao converter domínio para entity")
    void shouldPreserveInactiveStatusWhenMappingToEntity() {
        Peca domain = new Peca(21L, "Disco", "Disco de freio", new BigDecimal("220.00"), false);

        PecaEntity result = mapper.toEntity(domain);

        assertNotNull(result);
        assertFalse(result.isAtivo());
    }

    private PecaEntity buildEntity(Long id, String nome, String descricao, BigDecimal valor, boolean ativo) {
        PecaEntity entity = new PecaEntity();
        entity.setId(id);
        entity.setNome(nome);
        entity.setDescricao(descricao);
        entity.setValor(valor);
        entity.setAtivo(ativo);
        return entity;
    }
}
