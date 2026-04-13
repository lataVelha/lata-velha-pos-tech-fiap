package br.com.lata.velha.ordemDeServico.application.assemblers;

import br.com.lata.velha.ordemDeServico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Veiculo;
import br.com.lata.velha.ordemDeServico.domain.valueObjects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VeiculoAssemblerTest {

    private VeiculoAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new VeiculoAssembler();
    }

    @Test
    @DisplayName("deve converter VeiculoRequest para domínio")
    void shouldConvertRequestToDomain() {
        VeiculoRequest request = new VeiculoRequest(1L, "ABC1234", "Toyota", "Corolla", 2020, "Prata");

        Veiculo veiculo = assembler.toDomain(request);

        assertThat(veiculo.getId()).isNull();
        assertThat(veiculo.getProprietarioId()).isEqualTo(1L);
        assertThat(veiculo.getPlaca().getValor()).isEqualTo("ABC1234");
        assertThat(veiculo.getMarca()).isEqualTo("Toyota");
        assertThat(veiculo.getModelo()).isEqualTo("Corolla");
        assertThat(veiculo.getAno()).isEqualTo(2020);
        assertThat(veiculo.getCor()).isEqualTo("Prata");
        assertThat(veiculo.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("deve converter domínio para VeiculoResponse")
    void shouldConvertDomainToResponse() {
        Veiculo veiculo = new Veiculo(5L, 2L, Placa.of("ABC1234"), "Toyota", "Corolla", 2020, "Prata");

        VeiculoResponse response = assembler.toResponse(veiculo);

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.proprietarioId()).isEqualTo(2L);
        assertThat(response.placa()).isEqualTo("ABC-1234");
        assertThat(response.marca()).isEqualTo("Toyota");
        assertThat(response.modelo()).isEqualTo("Corolla");
        assertThat(response.ano()).isEqualTo(2020);
        assertThat(response.cor()).isEqualTo("Prata");
    }

    @Test
    @DisplayName("deve atualizar domínio com dados do request")
    void shouldUpdateDomainFromRequest() {
        Veiculo existente = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Toyota", "Corolla", 2020, "Prata");
        VeiculoRequest request = new VeiculoRequest(2L, "DEF5678", "Honda", "Civic", 2023, "Azul");

        assembler.updateDomain(existente, request);

        assertThat(existente.getProprietarioId()).isEqualTo(2L);
        assertThat(existente.getPlaca().getValor()).isEqualTo("DEF5678");
        assertThat(existente.getMarca()).isEqualTo("Honda");
        assertThat(existente.getModelo()).isEqualTo("Civic");
        assertThat(existente.getAno()).isEqualTo(2023);
        assertThat(existente.getCor()).isEqualTo("Azul");
    }

    @Test
    @DisplayName("deve formatar placa no padrão antigo corretamente")
    void shouldFormatOldPatternPlaca() {
        Veiculo veiculo = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2010, "Branco");

        VeiculoResponse response = assembler.toResponse(veiculo);

        assertThat(response.placa()).isEqualTo("ABC-1234");
    }

    @Test
    @DisplayName("deve formatar placa no padrão Mercosul corretamente")
    void shouldFormatMercosulPlaca() {
        Veiculo veiculo = new Veiculo(1L, 1L, Placa.of("ABC1D23"), "Fiat", "Argo", 2022, "Vermelho");

        VeiculoResponse response = assembler.toResponse(veiculo);

        assertThat(response.placa()).isEqualTo("ABC-1D23");
    }
}
