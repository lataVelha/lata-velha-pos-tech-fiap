package br.com.lata.velha.ordem_servico.application.dtos.request;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ServicoRequest(

        @NotNull(message = "Serviço Id é obrigatório!")
        @Schema(description = "ID do serviço a executar", example = "1")
        Long servicoId,

        @Schema(description = "Peças necessárias para o serviço")
        List<PecaRequest> pecas,

        @NotNull(message = "Valor de Mão de Obra é obrigatório!")
        @Schema(description = "Valor cobrado pela mão de obra", example = "150.00")
        BigDecimal valorMaoDeObra

) {
    public ExecucaoServico toDomain() {
        Servico servico = new Servico();
        servico.setId(servicoId);
        return new ExecucaoServico(servico, valorMaoDeObra);
    }
}
