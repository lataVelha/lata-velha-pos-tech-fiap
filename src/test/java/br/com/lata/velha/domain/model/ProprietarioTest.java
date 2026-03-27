package br.com.lata.velha.domain.model;

import br.com.lata.velha.domain.valueObject.Documento;
import br.com.lata.velha.domain.valueObject.Endereco;
import br.com.lata.velha.domain.valueObject.NumeroCelular;
import br.com.lata.velha.domain.valueObject.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProprietarioTest {

    private Proprietario proprietario;

    @BeforeEach
    void setUp() {
        proprietario = new Proprietario(
                1L,
                "João da Silva",
                "joao@email.com",
                Documento.of("52998224725"),
                NumeroCelular.of("11999990001"),
                new Endereco("Rua das Flores", "01234567", "100")
        );
    }

    // ==================== CRIAÇÃO ====================

    @Nested
    @DisplayName("Criação")
    class Creation {

        @Test
        @DisplayName("deve criar proprietário com todos os campos")
        void shouldCreateWithAllFields() {
            assertEquals(1L, proprietario.getId());
            assertEquals("João da Silva", proprietario.getNome());
            assertEquals("joao@email.com", proprietario.getEmail());
            assertEquals("52998224725", proprietario.getDocumento().getValor());
            assertEquals("11999990001", proprietario.getNumeroCelular().getValor());
            assertNotNull(proprietario.getEndereco());
            assertTrue(proprietario.getVeiculos().isEmpty());
        }

        @Test
        @DisplayName("deve criar proprietário vazio com lista de veículos inicializada")
        void shouldCreateEmptyWithInitializedList() {
            Proprietario vazio = new Proprietario();

            assertNotNull(vazio.getVeiculos());
            assertTrue(vazio.getVeiculos().isEmpty());
        }
    }

    // ==================== VALIDAÇÕES ====================

    @Nested
    @DisplayName("Validações")
    class Validations {

        @Test
        @DisplayName("deve rejeitar nome nulo")
        void shouldRejectNullName() {
            assertThrows(IllegalArgumentException.class, () -> proprietario.setNome(null));
        }

        @Test
        @DisplayName("deve rejeitar nome vazio")
        void shouldRejectEmptyName() {
            assertThrows(IllegalArgumentException.class, () -> proprietario.setNome(""));
        }

        @Test
        @DisplayName("deve rejeitar email inválido")
        void shouldRejectInvalidEmail() {
            assertThrows(IllegalArgumentException.class, () -> proprietario.setEmail("invalido"));
        }

        @Test
        @DisplayName("deve rejeitar email nulo")
        void shouldRejectNullEmail() {
            assertThrows(IllegalArgumentException.class, () -> proprietario.setEmail(null));
        }

        @Test
        @DisplayName("deve aceitar email válido")
        void shouldAcceptValidEmail() {
            proprietario.setEmail("novo@email.com");

            assertEquals("novo@email.com", proprietario.getEmail());
        }
    }

    // ==================== VEÍCULOS ====================

    @Nested
    @DisplayName("Gestão de veículos")
    class VeiculoManagement {

        @Test
        @DisplayName("deve adicionar veículo")
        void shouldAddVeiculo() {
            Veiculo veiculo = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");

            proprietario.addVeiculo(veiculo);

            assertEquals(1, proprietario.getVeiculos().size());
        }

        @Test
        @DisplayName("deve remover veículo")
        void shouldRemoveVeiculo() {
            Veiculo veiculo = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
            proprietario.addVeiculo(veiculo);

            proprietario.removeVeiculo(veiculo);

            assertTrue(proprietario.getVeiculos().isEmpty());
        }

        @Test
        @DisplayName("deve rejeitar veículo nulo")
        void shouldRejectNullVeiculo() {
            assertThrows(NullPointerException.class, () -> proprietario.addVeiculo(null));
        }

        @Test
        @DisplayName("lista de veículos deve ser imutável")
        void shouldBeImmutableList() {
            assertThrows(UnsupportedOperationException.class,
                    () -> proprietario.getVeiculos().add(
                            new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata")));
        }
    }

    // ==================== EQUALS / HASHCODE ====================

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("proprietários com mesmo id devem ser equals")
        void shouldBeEqualWithSameId() {
            Proprietario outro = new Proprietario();
            outro.setId(1L);

            assertEquals(proprietario, outro);
            assertEquals(proprietario.hashCode(), outro.hashCode());
        }

        @Test
        @DisplayName("proprietários com ids diferentes não devem ser equals")
        void shouldNotBeEqualWithDifferentId() {
            Proprietario outro = new Proprietario();
            outro.setId(2L);

            assertNotEquals(proprietario, outro);
        }
    }
}