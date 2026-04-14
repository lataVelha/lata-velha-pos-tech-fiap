package br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers;

import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.PecaAlocadaEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.PecaEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ServicoOSEntity;
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
                entity.getQuantidadeSolicitada(),
                entity.getQuantidadeReservada(),
                entity.getQuantidadeEncomendada(),
                entity.getStatus(),
                entity.getAtualizado()
        );
    }

    public PecaAlocadaEntity toEntity(PecaAlocada model) {
        if (model == null) return null;

        var entity = new PecaAlocadaEntity();

        entity.setId(model.getId());
        entity.setQuantidadeSolicitada(model.getQuantidadeSolicitada());
        entity.setQuantidadeReservada(model.getQuantidadeReservada());
        entity.setQuantidadeEncomendada(model.getQuantidadeEncomendada());
        entity.setStatus(model.getStatus());
        entity.setAtualizado(model.getAtualizado());

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