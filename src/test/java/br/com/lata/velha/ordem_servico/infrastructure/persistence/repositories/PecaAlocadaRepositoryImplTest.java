package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.PecaAlocadaEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.PecaAlocadaPersistenceMapper;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PecaAlocadaRepositoryImplTest {

    @Mock
    private PecaAlocadaJpaRepository jpaRepository;

    @Mock
    private PecaAlocadaPersistenceMapper mapper;

    @InjectMocks
    private PecaAlocadaRepositoryImpl repository;

    @Test
    @DisplayName("deve salvar peca alocada")
    void deveSalvarPecaAlocada() {
        PecaAlocada domain = new PecaAlocada(1L, 2L, 3);
        PecaAlocadaEntity entity = new PecaAlocadaEntity();
        PecaAlocadaEntity saved = new PecaAlocadaEntity();
        PecaAlocada expected = new PecaAlocada(10L, 1L, 2L, 3, 0, 0, null, null);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(expected);

        PecaAlocada result = repository.save(domain);

        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("deve buscar por id existente")
    void deveBuscarPorIdExistente() {
        PecaAlocadaEntity entity = new PecaAlocadaEntity();
        PecaAlocada expected = new PecaAlocada(1L, 2L, 3L, 4, 1, 3, null, null);

        when(jpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expected);

        PecaAlocada result = repository.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("deve lançar erro ao buscar por id inexistente")
    void deveLancarErroAoBuscarPorIdInexistente() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repository.findById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Peça alocada não encontrada");
    }

    @Test
    @DisplayName("deve retornar pagina de pecas alocadas por servico")
    void deveRetornarPaginaPorServico() {
        PecaAlocadaEntity e1 = new PecaAlocadaEntity();
        PecaAlocadaEntity e2 = new PecaAlocadaEntity();
        var pageable = PageRequest.of(0, 2);
        var page = new PageImpl<>(List.of(e1, e2), pageable, 4);

        when(jpaRepository.findByExecucaoServico_Id(3L, pageable)).thenReturn(page);
        when(mapper.toDomain(e1)).thenReturn(new PecaAlocada(1L, 1L, 3L, 5, 1, 4, null, null));
        when(mapper.toDomain(e2)).thenReturn(new PecaAlocada(2L, 2L, 3L, 2, 2, 0, null, null));

        PaginatedResult<PecaAlocada> result = repository.findByServicoOsId(3L, 0, 2);

        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(4);
        assertThat(result.totalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("deve delegar delete")
    void deveDelegarDelete() {
        repository.delete(7L);

        verify(jpaRepository).deleteById(7L);
    }

    @Test
    @DisplayName("deve delegar soma de quantidade reservada")
    void deveDelegarSomaDeQuantidadeReservada() {
        when(jpaRepository.somarQuantidadeReservadaPorPeca(5L)).thenReturn(12);

        Integer result = repository.somarQuantidadeReservadaPorPeca(5L);

        assertThat(result).isEqualTo(12);
    }

    @Test
    @DisplayName("deve buscar pendentes por peca e mapear")
    void deveBuscarPendentesPorPecaEMapear() {
        PecaAlocadaEntity e1 = new PecaAlocadaEntity();
        PecaAlocadaEntity e2 = new PecaAlocadaEntity();

        when(jpaRepository.buscarPendentesPorPecaOrdenado(9L)).thenReturn(List.of(e1, e2));
        when(mapper.toDomain(e1)).thenReturn(new PecaAlocada(1L, 9L, 1L, 5, 2, 3, null, null));
        when(mapper.toDomain(e2)).thenReturn(new PecaAlocada(2L, 9L, 2L, 4, 0, 4, null, null));

        List<PecaAlocada> result = repository.buscarPendentesPorPecaOrdenado(9L);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("deve buscar por peca e servico quando existir")
    void deveBuscarPorPecaEServicoQuandoExistir() {
        PecaAlocadaEntity entity = new PecaAlocadaEntity();
        PecaAlocada expected = new PecaAlocada(3L, 5L, 7L, 2, 1, 1, null, null);

        when(jpaRepository.findByPeca_IdAndExecucaoServico_Id(5L, 7L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expected);

        PecaAlocada result = repository.findByPecaIdAndServicoOsId(5L, 7L);

        assertThat(result.getId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("deve lançar erro ao buscar por peca e servico inexistentes")
    void deveLancarErroAoBuscarPorPecaEServicoInexistentes() {
        when(jpaRepository.findByPeca_IdAndExecucaoServico_Id(5L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repository.findByPecaIdAndServicoOsId(5L, 7L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Peça alocada não encontrada");
    }
}
