package br.com.lata.velha.ordemDeServico.infrastructure.persistence.mappers;

import br.com.lata.velha.ordemDeServico.domain.entities.Peca;
import br.com.lata.velha.ordemDeServico.infrastructure.persistence.entities.PecaEntity;
import org.springframework.stereotype.Component;

@Component
public class PecaPersistenceMapper {

    public Peca toDomain(PecaEntity entity) {
        if (entity == null) return null;

        return new Peca(
                entity.getId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getValor(),
                entity.isAtivo()
        );
    }

    public PecaEntity toEntity(Peca model) {
        if (model == null) return null;

        var entity = new PecaEntity();
        entity.setId(model.getId());
        entity.setNome(model.getNome());
        entity.setDescricao(model.getDescricao());
        entity.setValor(model.getValor());
        entity.setAtivo(model.isAtivo());

        return entity;
    }
}
