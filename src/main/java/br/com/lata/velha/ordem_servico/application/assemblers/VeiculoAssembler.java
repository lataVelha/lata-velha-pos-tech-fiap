package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import org.springframework.stereotype.Component;

@Component
public class VeiculoAssembler {

    public Veiculo toDomain(VeiculoRequest request) {
        return request.toDomain();
    }

    public void updateDomain(Veiculo existente, VeiculoRequest request) {
        request.updateDomain(existente);
    }

    public VeiculoResponse toResponse(Veiculo veiculo) {
        return VeiculoResponse.from(veiculo);
    }
}
