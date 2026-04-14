package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.ordem_servico.application.dtos.request.EnderecoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.valueObjects.Documento;
import br.com.lata.velha.ordem_servico.domain.valueObjects.Endereco;
import br.com.lata.velha.ordem_servico.domain.valueObjects.NumeroCelular;
import br.com.lata.velha.ordem_servico.domain.valueObjects.Placa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProprietarioAssemblerTest {

    private ProprietarioAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new ProprietarioAssembler();
    }

    @Test
    @DisplayName("deve converter ProprietarioRequest para domínio sem endereço")
    void shouldConvertRequestToDomainWithoutEndereco() {
        ProprietarioRequest request = new ProprietarioRequest(
                "João Silva", "joao@email.com", "11144477735", "11987654321", null);

        Proprietario proprietario = assembler.toDomain(request);

        assertThat(proprietario.getId()).isNull();
        assertThat(proprietario.getNome()).isEqualTo("João Silva");
        assertThat(proprietario.getEmail()).isEqualTo("joao@email.com");
        assertThat(proprietario.getDocumento().getValor()).isEqualTo("11144477735");
        assertThat(proprietario.getNumeroCelular().getValor()).isEqualTo("11987654321");
        assertThat(proprietario.getEndereco()).isNull();
        assertThat(proprietario.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("deve converter ProprietarioRequest para domínio com endereço")
    void shouldConvertRequestToDomainWithEndereco() {
        EnderecoRequest enderecoRequest = new EnderecoRequest("Rua das Flores", "01310100", "42");
        ProprietarioRequest request = new ProprietarioRequest(
                "João Silva", "joao@email.com", "11144477735", "11987654321", enderecoRequest);

        Proprietario proprietario = assembler.toDomain(request);

        assertThat(proprietario.getEndereco()).isNotNull();
        assertThat(proprietario.getEndereco().getRua()).isEqualTo("Rua das Flores");
        assertThat(proprietario.getEndereco().getCep()).isEqualTo("01310100");
        assertThat(proprietario.getEndereco().getNumeroCasa()).isEqualTo("42");
    }

    @Test
    @DisplayName("deve converter domínio para ProprietarioResponse sem veículos")
    void shouldConvertDomainToResponseWithoutVeiculos() {
        Proprietario proprietario = new Proprietario(1L, "João Silva", "joao@email.com",
                Documento.of("11144477735"), NumeroCelular.of("11987654321"), null);

        ProprietarioResponse response = assembler.toResponse(proprietario);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("João Silva");
        assertThat(response.email()).isEqualTo("joao@email.com");
        assertThat(response.documento()).isEqualTo("111.444.777-35");
        assertThat(response.numeroCelular()).isEqualTo("(11) 98765-4321");
        assertThat(response.endereco()).isNull();
        assertThat(response.veiculos()).isEmpty();
    }

    @Test
    @DisplayName("deve converter domínio para ProprietarioResponse com endereço")
    void shouldConvertDomainToResponseWithEndereco() {
        Endereco endereco = new Endereco("Rua das Flores", "01310100", "42");
        Proprietario proprietario = new Proprietario(1L, "João Silva", "joao@email.com",
                Documento.of("11144477735"), NumeroCelular.of("11987654321"), endereco);

        ProprietarioResponse response = assembler.toResponse(proprietario);

        assertThat(response.endereco()).isNotNull();
        assertThat(response.endereco().rua()).isEqualTo("Rua das Flores");
        assertThat(response.endereco().cep()).isEqualTo("01310100");
        assertThat(response.endereco().numeroCasa()).isEqualTo("42");
    }

    @Test
    @DisplayName("deve converter domínio para ProprietarioResponse com veículos")
    void shouldConvertDomainToResponseWithVeiculos() {
        Proprietario proprietario = new Proprietario(1L, "João Silva", "joao@email.com",
                Documento.of("11144477735"), NumeroCelular.of("11987654321"), null);
        proprietario.addVeiculo(new Veiculo(10L, 1L, Placa.of("ABC1234"), "Toyota", "Corolla", 2020, "Prata"));

        ProprietarioResponse response = assembler.toResponse(proprietario);

        assertThat(response.veiculos()).hasSize(1);
        assertThat(response.veiculos().get(0).id()).isEqualTo(10L);
        assertThat(response.veiculos().get(0).placa()).isEqualTo("ABC-1234");
        assertThat(response.veiculos().get(0).marca()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("deve atualizar domínio com dados do request sem endereço")
    void shouldUpdateDomainFromRequestWithoutEndereco() {
        Proprietario existente = new Proprietario(1L, "Nome Antigo", "antigo@email.com",
                Documento.of("11144477735"), NumeroCelular.of("11987654321"), null);
        ProprietarioRequest request = new ProprietarioRequest(
                "Nome Novo", "novo@email.com", "52998224725", "11999999999", null);

        assembler.updateDomain(existente, request);

        assertThat(existente.getNome()).isEqualTo("Nome Novo");
        assertThat(existente.getEmail()).isEqualTo("novo@email.com");
        assertThat(existente.getDocumento().getValor()).isEqualTo("52998224725");
        assertThat(existente.getNumeroCelular().getValor()).isEqualTo("11999999999");
    }

    @Test
    @DisplayName("deve atualizar endereço do domínio quando request possui endereço")
    void shouldUpdateEnderecoWhenRequestHasEndereco() {
        Proprietario existente = new Proprietario(1L, "João Silva", "joao@email.com",
                Documento.of("11144477735"), NumeroCelular.of("11987654321"), null);
        EnderecoRequest enderecoRequest = new EnderecoRequest("Av. Paulista", "01310100", "1000");
        ProprietarioRequest request = new ProprietarioRequest(
                "João Silva", "joao@email.com", "11144477735", "11987654321", enderecoRequest);

        assembler.updateDomain(existente, request);

        assertThat(existente.getEndereco()).isNotNull();
        assertThat(existente.getEndereco().getRua()).isEqualTo("Av. Paulista");
        assertThat(existente.getEndereco().getCep()).isEqualTo("01310100");
        assertThat(existente.getEndereco().getNumeroCasa()).isEqualTo("1000");
    }
}
