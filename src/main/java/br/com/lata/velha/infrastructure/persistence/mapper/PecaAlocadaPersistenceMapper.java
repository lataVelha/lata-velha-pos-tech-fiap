package br.com.lata.velha.infrastructure.persistence.mapper;

import br.com.lata.velha.domain.model.PecaAlocada;
import br.com.lata.velha.infrastructure.persistence.entity.PecaAlocadaEntity;
import br.com.lata.velha.infrastructure.persistence.entity.PecaEntity;
import br.com.lata.velha.infrastructure.persistence.entity.ServicoOSEntity;
import org.springframework.stereotype.Component;

@Component
public class PecaAlocadaPersistenceMapper {

    public PecaAlocada toDomain(PecaAlocadaEntity entity) {
        if (entity == null) return null;

        Long pecaId = entity.getPeca() != null ? entity.getPeca().getId() : null;
        Long servicoOsId = entity.getServicoOS() != null ? entity.getServicoOS().getId() : null;

        return new PecaAlocada(
                entity.getId(),
                pecaId,
                servicoOsId,
                entity.getQuantidadeAlocada()
        );
    }

    public PecaAlocadaEntity toEntity(PecaAlocada model) {
        if (model == null) return null;

        var entity = new PecaAlocadaEntity();
        entity.setId(model.getId());
        entity.setQuantidadeAlocada(model.getQuantidadeAlocada());

        if (model.getPecaId() != null) {
            PecaEntity peca = new PecaEntity();
            peca.setId(model.getPecaId());
            entity.setPeca(peca);
        }

        if (model.getServicoOsId() != null) {
            ServicoOSEntity servicoOs = new ServicoOSEntity();
            servicoOs.setId(model.getServicoOsId());
            entity.setServicoOS(servicoOs);
        }

        return entity;
    }
}