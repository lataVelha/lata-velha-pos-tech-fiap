package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.ordem_servico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PecaAlocadaAssemblerTest {

    @Test
    void deveConverterParaResponse() {
        // Arrange
        Peca peca = new Peca(10L, "Filtro de Óleo", "Filtro Bosh", new BigDecimal("50.0"));
        PecaAlocada pecaAlocada = new PecaAlocada(1L, 10L, 99L, 2, 0, 0, StatusPecaAlocada.ORCAMENTO, LocalDateTime.now());

        // Act
        PecaAlocadaResponse response = PecaAlocadaAssembler.toResponse(pecaAlocada);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.pecaId()).isEqualTo(10L);
        assertThat(response.pecaNome()).isNull();
        assertThat(response.quantidadeAlocada()).isEqualTo(2);
        assertThat(response.servicoOsId()).isEqualTo(99L);
    }
}