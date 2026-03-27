package br.com.lata.velha.domain.model;

import br.com.lata.velha.domain.valueObject.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VeiculoTest {

    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        veiculo = new Veiculo(1L, 1L, Placa.of("ABC1D23"), "Fiat", "Uno", 2020, "Prata");
    }

    // ==================== CRIAÇÃO ====================

    @Nested
    @DisplayName("Criação")
    class Creation {

        @Test
        @DisplayName("deve criar veículo com todos os campos")
        void shouldCreateWithAllFields() {
            assertEquals(1L, veiculo.getId());
            assertEquals(1L, veiculo.getProprietarioId());
            assertEquals("ABC1D23", veiculo.getPlaca().getValor());
            assertEquals("Fiat", veiculo.getMarca());
            assertEquals("Uno", veiculo.getModelo());
            assertEquals(2020, veiculo.getAno());
            assertEquals("Prata", veiculo.getCor());
        }

        @Test
        @DisplayName("deve criar veículo vazio")
        void shouldCreateEmpty() {
            Veiculo vazio = new Veiculo();

            assertNull(vazio.getId());
            assertNull(vazio.getPlaca());
        }
    }

    // ==================== VALIDAÇÕES ====================

    @Nested
    @DisplayName("Validações")
    class Validations {

        @Test
        @DisplayName("deve rejeitar marca nula")
        void shouldRejectNullMarca() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setMarca(null));
        }

        @Test
        @DisplayName("deve rejeitar marca vazia")
        void shouldRejectEmptyMarca() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setMarca(""));
        }

        @Test
        @DisplayName("deve rejeitar modelo nulo")
        void shouldRejectNullModelo() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setModelo(null));
        }

        @Test
        @DisplayName("deve rejeitar modelo vazio")
        void shouldRejectEmptyModelo() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setModelo(""));
        }

        @Test
        @DisplayName("deve rejeitar cor nula")
        void shouldRejectNullCor() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setCor(null));
        }

        @Test
        @DisplayName("deve rejeitar cor vazia")
        void shouldRejectEmptyCor() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setCor(""));
        }

        @Test
        @DisplayName("deve rejeitar ano nulo")
        void shouldRejectNullAno() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setAno(null));
        }

        @Test
        @DisplayName("deve rejeitar ano anterior a 1886")
        void shouldRejectAnoBefore1886() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setAno(1885));
        }

        @Test
        @DisplayName("deve rejeitar ano futuro além do próximo")
        void shouldRejectFutureAno() {
            int futureYear = java.time.Year.now().getValue() + 2;
            assertThrows(IllegalArgumentException.class, () -> veiculo.setAno(futureYear));
        }

        @Test
        @DisplayName("deve aceitar ano atual + 1")
        void shouldAcceptNextYearAno() {
            int nextYear = java.time.Year.now().getValue() + 1;
            veiculo.setAno(nextYear);

            assertEquals(nextYear, veiculo.getAno());
        }

        @Test
        @DisplayName("deve aceitar ano 1886 (primeiro carro)")
        void shouldAcceptMinimumAno() {
            veiculo.setAno(1886);

            assertEquals(1886, veiculo.getAno());
        }
    }

    // ==================== EQUALS / HASHCODE ====================

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("veículos com mesmo id devem ser equals")
        void shouldBeEqualWithSameId() {
            Veiculo outro = new Veiculo();
            outro.setId(1L);

            assertEquals(veiculo, outro);
            assertEquals(veiculo.hashCode(), outro.hashCode());
        }

        @Test
        @DisplayName("veículos com ids diferentes não devem ser equals")
        void shouldNotBeEqualWithDifferentId() {
            Veiculo outro = new Veiculo();
            outro.setId(2L);

            assertNotEquals(veiculo, outro);
        }
    }
}