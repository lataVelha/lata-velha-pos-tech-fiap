package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.exception.InvalidLoginException;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.infrastructure.persistence.mapper.FuncionarioPersistenceMapper;
import br.com.lata.velha.infrastructure.security.PasswordHasherImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({FuncionarioRepositoryImpl.class, FuncionarioPersistenceMapper.class,
        PasswordHasherImpl.class, FuncionarioRepositoryImplTest.TestConfig.class})
class FuncionarioRepositoryImplTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Autowired
    private FuncionarioRepositoryImpl repository;

    @Test
    @DisplayName("deve encontrar funcionário pelo username")
    void shouldFindByUsername() {
        Funcionario funcionario = repository.findByUsername("admin");

        assertThat(funcionario).isNotNull();
        assertThat(funcionario.getUsername()).isEqualTo("admin");
        assertThat(funcionario.getNome()).isEqualTo("Fiap");
        assertThat(funcionario.getCargo()).isNotNull();
        assertThat(funcionario.getCargo().getNome()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("deve encontrar funcionário atendente pelo username")
    void shouldFindAtendenteByUsername() {
        Funcionario funcionario = repository.findByUsername("atendente");

        assertThat(funcionario).isNotNull();
        assertThat(funcionario.getUsername()).isEqualTo("atendente");
        assertThat(funcionario.getCargo().getNome()).isEqualTo("ATENDENTE");
    }

    @Test
    @DisplayName("deve encontrar funcionário mecânico pelo username")
    void shouldFindMecanicoByUsername() {
        Funcionario funcionario = repository.findByUsername("mecanico");

        assertThat(funcionario).isNotNull();
        assertThat(funcionario.getUsername()).isEqualTo("mecanico");
        assertThat(funcionario.getCargo().getNome()).isEqualTo("MECANICO");
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar username inexistente")
    void shouldThrowWhenUsernameNotFound() {
        assertThatThrownBy(() -> repository.findByUsername("naoexiste"))
                .isInstanceOf(InvalidLoginException.class);
    }

    @Test
    @DisplayName("deve retornar funcionário com senha que valida corretamente")
    void shouldReturnFuncionarioWithValidSenha() {
        Funcionario funcionario = repository.findByUsername("admin");

        assertThat(funcionario.getCredential().match("123456")).isTrue();
        assertThat(funcionario.getCredential().match("senhaerrada")).isFalse();
    }

    @Test
    @DisplayName("deve retornar funcionário admin com todas as roles")
    void shouldReturnAdminWithAllRoles() {
        Funcionario funcionario = repository.findByUsername("admin");

        assertThat(funcionario.getCargo().getRoles()).isNotEmpty();
        assertThat(funcionario.hasRole("ADMIN")).isTrue();
        assertThat(funcionario.hasRole("USER")).isTrue();
        assertThat(funcionario.hasRole("MECANICO")).isTrue();
    }
}
