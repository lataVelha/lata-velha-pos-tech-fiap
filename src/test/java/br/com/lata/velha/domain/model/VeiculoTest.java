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
    class Criacao {

        @Test
        @DisplayName("deve criar veículo com todos os campos")
        void deveCriarComTodosCampos() {
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
        void deveCriarVazio() {
            Veiculo vazio = new Veiculo();

            assertNull(vazio.getId());
            assertNull(vazio.getPlaca());
        }
    }

    // ==================== VALIDAÇÕES ====================

    @Nested
    @DisplayName("Validações")
    class Validacoes {

        @Test
        @DisplayName("deve rejeitar marca nula")
        void deveRejeitarMarcaNula() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setMarca(null));
        }

        @Test
        @DisplayName("deve rejeitar marca vazia")
        void deveRejeitarMarcaVazia() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setMarca(""));
        }

        @Test
        @DisplayName("deve rejeitar modelo nulo")
        void deveRejeitarModeloNulo() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setModelo(null));
        }

        @Test
        @DisplayName("deve rejeitar modelo vazio")
        void deveRejeitarModeloVazio() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setModelo(""));
        }

        @Test
        @DisplayName("deve rejeitar cor nula")
        void deveRejeitarCorNula() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setCor(null));
        }

        @Test
        @DisplayName("deve rejeitar cor vazia")
        void deveRejeitarCorVazia() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setCor(""));
        }

        @Test
        @DisplayName("deve rejeitar ano nulo")
        void deveRejeitarAnoNulo() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setAno(null));
        }

        @Test
        @DisplayName("deve rejeitar ano anterior a 1886")
        void deveRejeitarAnoMuitoAntigo() {
            assertThrows(IllegalArgumentException.class, () -> veiculo.setAno(1885));
        }

        @Test
        @DisplayName("deve rejeitar ano futuro além do próximo")
        void deveRejeitarAnoFuturo() {
            int anoFuturo = java.time.Year.now().getValue() + 2;
            assertThrows(IllegalArgumentException.class, () -> veiculo.setAno(anoFuturo));
        }

        @Test
        @DisplayName("deve aceitar ano atual + 1")
        void deveAceitarAnoProximo() {
            int proximoAno = java.time.Year.now().getValue() + 1;
            veiculo.setAno(proximoAno);

            assertEquals(proximoAno, veiculo.getAno());
        }

        @Test
        @DisplayName("deve aceitar ano 1886 (primeiro carro)")
        void deveAceitarAnoMinimo() {
            veiculo.setAno(1886);

            assertEquals(1886, veiculo.getAno());
        }
    }

    // ==================== EQUALS / HASHCODE ====================

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("veículos com mesmo id devem ser equals")
        void mesmoIdDevemSerEquals() {
            Veiculo outro = new Veiculo();
            outro.setId(1L);

            assertEquals(veiculo, outro);
            assertEquals(veiculo.hashCode(), outro.hashCode());
        }

        @Test
        @DisplayName("veículos com ids diferentes não devem ser equals")
        void idsDiferentesNaoDevemSerEquals() {
            Veiculo outro = new Veiculo();
            outro.setId(2L);

            assertNotEquals(veiculo, outro);
        }
    }
}