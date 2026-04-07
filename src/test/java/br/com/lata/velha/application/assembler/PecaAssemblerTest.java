package br.com.lata.velha.application.assembler;

import br.com.lata.velha.application.dto.request.CadastrarPecaRequest;
import br.com.lata.velha.application.dto.response.PecaResponse;
import br.com.lata.velha.domain.model.Peca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PecaAssemblerTest {

    private PecaAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new PecaAssembler();
    }

    @Test
    @DisplayName("deve converter CadastrarPecaRequest para domínio")
    void shouldConvertRequestToDomain() {
        CadastrarPecaRequest request = new CadastrarPecaRequest(
                "Pastilha",
                "Pastilha dianteira",
                new BigDecimal("150.00")
        );

        Peca peca = assembler.toDomain(request);

        assertThat(peca.getId()).isNull();
        assertThat(peca.getNome()).isEqualTo("Pastilha");
        assertThat(peca.getDescricao()).isEqualTo("Pastilha dianteira");
        assertThat(peca.getValor()).isEqualByComparingTo("150.00");
        assertThat(peca.isAtivo()).isTrue();
    }

    @Test
    @DisplayName("deve converter domínio para PecaResponse")
    void shouldConvertDomainToResponse() {
        Peca peca = new Peca(10L, "Filtro", "Filtro de óleo", new BigDecimal("35.00"), true);

        PecaResponse response = assembler.toResponse(peca);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.nome()).isEqualTo("Filtro");
        assertThat(response.descricao()).isEqualTo("Filtro de óleo");
        assertThat(response.valor()).isEqualByComparingTo("35.00");
        assertThat(response.ativo()).isTrue();
    }

    @Test
    @DisplayName("deve preservar status inativo no response")
    void shouldPreserveInactiveStatusInResponse() {
        Peca peca = new Peca(11L, "Óleo", "Óleo sintético", new BigDecimal("59.90"), false);

        PecaResponse response = assembler.toResponse(peca);

        assertThat(response.ativo()).isFalse();
    }
}
