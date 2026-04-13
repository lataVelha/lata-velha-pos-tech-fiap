package br.com.lata.velha.authentication.infrastructure.persistence.repositories;

import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.authentication.domain.exceptions.notFoundExceptions.RoleNotFoundException;
import br.com.lata.velha.authentication.infrastructure.persistence.jpa.RoleJpaRepository;
import br.com.lata.velha.authentication.infrastructure.persistence.entities.RoleEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleRepositoryImplTest {

    @Mock
    private RoleJpaRepository jpaRepository;

    @InjectMocks
    private RoleRepositoryImpl repository;

    @Test
    @DisplayName("getByNome deve retornar Role quando encontrada")
    void shouldReturnRoleWhenFound() {
        RoleEntity entity = new RoleEntity(UUID.randomUUID(), "ADMIN");
        when(jpaRepository.findByNome("ADMIN")).thenReturn(Optional.of(entity));

        Role result = repository.getByNome("ADMIN");

        assertNotNull(result);
        assertEquals("ADMIN", result.getNome());
        verify(jpaRepository).findByNome("ADMIN");
    }

    @Test
    @DisplayName("getByNome deve lançar RoleNotFoundException quando não encontrada")
    void shouldThrowWhenRoleNotFound() {
        when(jpaRepository.findByNome("INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> repository.getByNome("INEXISTENTE"));
    }

    @Test
    @DisplayName("getByNome deve mapear corretamente o nome da role")
    void shouldMapRoleNameCorrectly() {
        RoleEntity entity = new RoleEntity(UUID.randomUUID(), "MECANICO");
        when(jpaRepository.findByNome("MECANICO")).thenReturn(Optional.of(entity));

        Role result = repository.getByNome("MECANICO");

        assertEquals("MECANICO", result.getNome());
        assertNotNull(result.getRoleId());
    }
}
