package br.com.lata.velha.ordem_servico.application.dtos.request;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ServicoRequest(

        @NotNull(message = "Serviço Id é obrigatório!")
        @Schema(example = "1", description = "servicos")
        Long servicoId,

        @Schema(description = "Lista de peças utilizadas no serviço")
        List<PecaRequest> pecas,

        @NotNull(message = "Valor de Mão de Obra é obrigatório!")
        @Schema(example = "150.00", description = "valor serviço")
        BigDecimal valorMaoDeObra

) {
    public ExecucaoServico toDomain() {
        Servico servico = new Servico();
        servico.setId(servicoId);
        return new ExecucaoServico(servico, valorMaoDeObra);
    }
}
