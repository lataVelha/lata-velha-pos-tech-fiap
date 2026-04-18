package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.ordem_servico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PecaAlocadaAssemblerTest {

    @Test
    void deveConverterParaResponse() {
        // Arrange
        PecaAlocada pecaAlocada = new PecaAlocada(10L, 99L, 2);
        pecaAlocada.setId(1L);

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