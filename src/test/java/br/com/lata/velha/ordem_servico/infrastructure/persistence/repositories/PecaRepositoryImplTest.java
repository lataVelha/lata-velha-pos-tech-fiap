package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.PecaEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.PecaPersistenceMapper;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PecaRepositoryImplTest {

    @Mock
    private PecaJpaRepository jpaRepository;

    @Mock
    private PecaPersistenceMapper mapper;

    @InjectMocks
    private PecaRepositoryImpl repository;

    @Test
    @DisplayName("deve salvar peca e retornar dominio mapeado")
    void deveSalvarPeca() {
        Peca domain = new Peca(1L, "Filtro", "Filtro de óleo", new BigDecimal("30.00"));
        PecaEntity entity = new PecaEntity();
        PecaEntity saved = new PecaEntity();
        Peca expected = new Peca(1L, "Filtro", "Filtro de óleo", new BigDecimal("30.00"));

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(expected);

        Peca result = repository.save(domain);

        assertThat(result.getNome()).isEqualTo("Filtro");
        assertThat(result.getValor()).isEqualByComparingTo("30.00");
    }

    @Test
    @DisplayName("deve buscar peca ativa por id")
    void deveBuscarPecaAtivaPorId() {
        PecaEntity entity = new PecaEntity();
        Peca expected = new Peca(2L, "Pastilha", "Pastilha dianteira", new BigDecimal("120.00"));

        when(jpaRepository.findByIdAndAtivoTrue(2L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(expected);

        Peca result = repository.findActiveById(2L);

        assertThat(result.getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("deve lancar erro ao buscar peca ativa inexistente")
    void deveLancarErroAoBuscarPecaAtivaInexistente() {
        when(jpaRepository.findByIdAndAtivoTrue(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repository.findActiveById(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Peça não encontrada");
    }

    @Test
    @DisplayName("deve listar pecas ativas")
    void deveListarPecasAtivas() {
        PecaEntity e1 = new PecaEntity();
        PecaEntity e2 = new PecaEntity();

        when(jpaRepository.findByAtivoTrue()).thenReturn(List.of(e1, e2));
        when(mapper.toDomain(e1)).thenReturn(new Peca(1L, "A", "A", new BigDecimal("1.00")));
        when(mapper.toDomain(e2)).thenReturn(new Peca(2L, "B", "B", new BigDecimal("2.00")));

        List<Peca> result = repository.findAllActive();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("deve retornar pagina de pecas ativas")
    void deveRetornarPaginaDePecasAtivas() {
        PecaEntity e1 = new PecaEntity();
        PecaEntity e2 = new PecaEntity();
        var pageable = PageRequest.of(0, 2);
        var page = new PageImpl<>(List.of(e1, e2), pageable, 5);

        when(jpaRepository.findByAtivoTrue(pageable)).thenReturn(page);
        when(mapper.toDomain(e1)).thenReturn(new Peca(1L, "A", "A", new BigDecimal("1.00")));
        when(mapper.toDomain(e2)).thenReturn(new Peca(2L, "B", "B", new BigDecimal("2.00")));

        PaginatedResult<Peca> result = repository.findAllActivePaginated(0, 2);

        assertThat(result.content()).hasSize(2);
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.totalElements()).isEqualTo(5);
        assertThat(result.totalPages()).isEqualTo(3);
    }
}
