package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import org.springframework.stereotype.Component;

@Component
public class ProprietarioAssembler {

    public Proprietario toDomain(ProprietarioRequest request) {
        return request.toDomain();
    }

    public void updateDomain(Proprietario existente, ProprietarioRequest request) {
        request.updateDomain(existente);
    }

    public ProprietarioResponse toResponse(Proprietario proprietario) {
        return ProprietarioResponse.from(proprietario);
    }
}
