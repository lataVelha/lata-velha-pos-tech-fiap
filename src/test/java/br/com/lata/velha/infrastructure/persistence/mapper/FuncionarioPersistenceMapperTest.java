package br.com.lata.velha.infrastructure.persistence.mapper;

import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.model.Role;
import br.com.lata.velha.infrastructure.persistence.entity.CargoEntity;
import br.com.lata.velha.infrastructure.persistence.entity.FuncionarioEntity;
import br.com.lata.velha.infrastructure.persistence.entity.RoleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FuncionarioPersistenceMapperTest {

    private FuncionarioPersistenceMapper mapper;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        mapper = new FuncionarioPersistenceMapper(passwordEncoder);
    }

    @Test
    @DisplayName("deve retornar null ao converter entity nula para domain")
    void shouldReturnNullWhenFuncionarioEntityIsNull() {
        assertNull(mapper.toDomain((FuncionarioEntity) null));
    }

    @Test
    @DisplayName("deve converter funcionário com cargo e roles para domain")
    void shouldMapFuncionarioWithCargoAndRolesToDomain() {
        String hash = passwordEncoder.encode("senha123");
        FuncionarioEntity entity = buildFuncionarioEntity(hash, buildCargoEntity());

        Funcionario result = mapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Carlos", result.getNome());
        assertEquals("carlos", result.getUsername());
        assertTrue(result.getSenha().matches("senha123"));

        Cargo cargo = result.getCargo();
        assertNotNull(cargo);
        assertEquals(10L, cargo.getId());
        assertEquals("Gerente", cargo.getNome());
        assertEquals(1, cargo.getRoles().size());

        Role role = cargo.getRoles().iterator().next();
        assertEquals(1L, role.getId());
        assertEquals("ROLE_ADMIN", role.getNome());
    }

    @Test
    @DisplayName("deve rejeitar senha incorreta ao verificar hash")
    void shouldRejectIncorrectPassword() {
        String hash = passwordEncoder.encode("senha123");
        FuncionarioEntity entity = buildFuncionarioEntity(hash, buildCargoEntity());

        Funcionario result = mapper.toDomain(entity);

        assertFalse(result.getSenha().matches("senhaErrada"));
    }

    @Test
    @DisplayName("deve retornar null ao converter CargoEntity nula para domain")
    void shouldReturnNullWhenCargoEntityIsNull() {
        assertNull(mapper.toDomain((CargoEntity) null));
    }

    @Test
    @DisplayName("deve converter cargo com roles para domain")
    void shouldMapCargoWithRolesToDomain() {
        Cargo result = mapper.toDomain(buildCargoEntity());

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Gerente", result.getNome());
        assertFalse(result.getRoles().isEmpty());
    }

    @Test
    @DisplayName("deve retornar null ao converter RoleEntity nula para domain")
    void shouldReturnNullWhenRoleEntityIsNull() {
        assertNull(mapper.toDomain((RoleEntity) null));
    }

    @Test
    @DisplayName("deve converter role para domain")
    void shouldMapRoleToDomain() {
        RoleEntity entity = new RoleEntity();
        entity.setId(1L);
        entity.setNome("ROLE_USER");

        Role result = mapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ROLE_USER", result.getNome());
    }

    private CargoEntity buildCargoEntity() {
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setNome("ROLE_ADMIN");

        CargoEntity cargo = new CargoEntity();
        cargo.setId(10L);
        cargo.setNome("Gerente");
        cargo.setRoles(Set.of(role));
        return cargo;
    }

    private FuncionarioEntity buildFuncionarioEntity(String hash, CargoEntity cargo) {
        FuncionarioEntity entity = new FuncionarioEntity();
        entity.setId(100L);
        entity.setNome("Carlos");
        entity.setUsername("carlos");
        entity.setPassword(hash);
        entity.setCargo(cargo);
        return entity;
    }
}
