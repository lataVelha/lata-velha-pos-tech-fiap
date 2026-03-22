package br.com.lata.velha.application.assembler;

import br.com.lata.velha.application.dto.request.VeiculoRequest;
import br.com.lata.velha.application.dto.response.VeiculoResponse;
import br.com.lata.velha.domain.model.Veiculo;
import br.com.lata.velha.domain.valueObject.Placa;
import org.springframework.stereotype.Component;

@Component
public class VeiculoAssembler {

    public Veiculo toDomain(VeiculoRequest req) {
        return new Veiculo(null, req.proprietarioId(), Placa.of(req.placa()),
                req.marca(), req.modelo(), req.ano(), req.cor());
    }

    public void updateDomain(Veiculo existente, VeiculoRequest req) {
        existente.setProprietarioId(req.proprietarioId());
        existente.setPlaca(Placa.of(req.placa()));
        existente.setMarca(req.marca());
        existente.setModelo(req.modelo());
        existente.setAno(req.ano());
        existente.setCor(req.cor());
    }

    public VeiculoResponse toResponse(Veiculo v) {
        return new VeiculoResponse(v.getId(), v.getProprietarioId(), v.getPlaca().getFormatado(),
                v.getMarca(), v.getModelo(), v.getAno(), v.getCor());
    }
}