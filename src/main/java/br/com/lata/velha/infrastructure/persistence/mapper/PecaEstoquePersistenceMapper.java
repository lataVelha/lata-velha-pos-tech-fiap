package br.com.lata.velha.infrastructure.persistence.mapper;

import br.com.lata.velha.domain.entities.PecaEstoque;
import br.com.lata.velha.infrastructure.persistence.entity.PecaEstoqueEntity;
import org.springframework.stereotype.Component;

@Component
public class PecaEstoquePersistenceMapper {

    public PecaEstoque toDomain(PecaEstoqueEntity entity) {
        if (entity == null) {
            return null;
        }

        return new PecaEstoque(entity.getPecaId(), entity.getQuantidadeArmazenada());
    }

    public PecaEstoqueEntity toEntity(PecaEstoque model) {
        if (model == null) {
            return null;
        }

        PecaEstoqueEntity entity = new PecaEstoqueEntity();
        entity.setPecaId(model.getPecaId());
        entity.setQuantidadeArmazenada(model.getQuantidadeArmazenada());
        return entity;
    }
}
