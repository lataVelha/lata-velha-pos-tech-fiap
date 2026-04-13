package br.com.lata.velha.infrastructure.persistence.mapper;

import br.com.lata.velha.domain.entities.Veiculo;
import br.com.lata.velha.domain.valueObject.Placa;
import br.com.lata.velha.infrastructure.persistence.entity.ProprietarioEntity;
import br.com.lata.velha.infrastructure.persistence.entity.VeiculoEntity;
import org.springframework.stereotype.Component;

@Component
public class VeiculoPersistenceMapper {

    // --- Entity → Domain ---

    public Veiculo toDomain(VeiculoEntity entity) {
        if (entity == null) return null;

        Veiculo veiculo = new Veiculo(
                entity.getId(),
                entity.getProprietario().getId(),
                Placa.of(entity.getPlaca()),
                entity.getMarca(),
                entity.getModelo(),
                entity.getAno(),
                entity.getCor()
        );
        veiculo.setAtivo(entity.isAtivo());
        return veiculo;
    }

    // --- Domain → Entity ---

    public VeiculoEntity toEntity(Veiculo domain, ProprietarioEntity proprietarioEntity) {
        if (domain == null) return null;

        VeiculoEntity entity = new VeiculoEntity();
        entity.setId(domain.getId());
        entity.setProprietario(proprietarioEntity);
        entity.setPlaca(domain.getPlaca().getValor());
        entity.setMarca(domain.getMarca());
        entity.setModelo(domain.getModelo());
        entity.setAno(domain.getAno());
        entity.setCor(domain.getCor());
        entity.setAtivo(domain.isAtivo());
        return entity;
    }
}