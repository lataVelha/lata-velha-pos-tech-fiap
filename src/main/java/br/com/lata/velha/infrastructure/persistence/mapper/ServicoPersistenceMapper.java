package br.com.lata.velha.infrastructure.persistence.mapper;

import br.com.lata.velha.domain.model.Servico;
import br.com.lata.velha.infrastructure.persistence.entity.ServicoEntity;
import org.springframework.stereotype.Component;

@Component
public class ServicoPersistenceMapper {

    public Servico toDomain(ServicoEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Servico(
                entity.getId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.isAtivo()
        );
    }

    public ServicoEntity toEntity(Servico model) {
        if (model == null) {
            return null;
        }

        var entity = new ServicoEntity();
        entity.setId(model.getId());
        entity.setNome(model.getNome());
        entity.setDescricao(model.getDescricao());
        entity.setAtivo(model.isAtivo());
        return entity;
    }
}
