package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.PecaEstoqueEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.PecaEstoquePersistenceMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PecaEstoqueRepositoryImplTest {

    @Mock
    private PecaEstoqueJpaRepository jpaRepository;

    @Mock
    private PecaEstoquePersistenceMapper mapper;

    @InjectMocks
    private PecaEstoqueRepositoryImpl repository;

    @Test
    @DisplayName("deve salvar e mapear estoque")
    void deveSalvarEMapearEstoque() {
        PecaEstoque domain = new PecaEstoque(1L, 10);
        PecaEstoqueEntity entity = new PecaEstoqueEntity();
        PecaEstoqueEntity saved = new PecaEstoqueEntity();
        PecaEstoque expected = new PecaEstoque(1L, 10);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(saved);
        when(mapper.toDomain(saved)).thenReturn(expected);

        PecaEstoque result = repository.save(domain);

        assertThat(result.getPecaId()).isEqualTo(1L);
        assertThat(result.getQuantidadeArmazenada()).isEqualTo(10);
    }

    @Test
    @DisplayName("deve buscar por id e retornar mapeado quando existir")
    void deveBuscarPorIdQuandoExistir() {
        PecaEstoqueEntity entity = new PecaEstoqueEntity();
        entity.setPecaId(5L);
        entity.setQuantidadeArmazenada(9);

        when(jpaRepository.findById(5L)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(new PecaEstoque(5L, 9));

        PecaEstoque result = repository.findByPecaId(5L);

        assertThat(result).isNotNull();
        assertThat(result.getPecaId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("deve retornar null ao buscar por id inexistente")
    void deveRetornarNullAoBuscarPorIdInexistente() {
        when(jpaRepository.findById(99L)).thenReturn(Optional.empty());

        PecaEstoque result = repository.findByPecaId(99L);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("deve delegar baixa de estoque para jpa")
    void deveDelegarBaixaDeEstoque() {
        repository.baixarEstoque(3L, 2);

        verify(jpaRepository).baixarEstoque(3L, 2);
    }
}
