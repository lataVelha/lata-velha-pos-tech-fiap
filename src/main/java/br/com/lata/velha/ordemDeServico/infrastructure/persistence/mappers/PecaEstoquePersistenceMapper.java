package br.com.lata.velha.ordemDeServico.infrastructure.persistence.mappers;

import br.com.lata.velha.ordemDeServico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordemDeServico.infrastructure.persistence.entities.PecaEstoqueEntity;
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
