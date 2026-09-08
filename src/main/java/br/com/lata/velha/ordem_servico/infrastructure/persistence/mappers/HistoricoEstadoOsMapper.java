package br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers;

import br.com.lata.velha.ordem_servico.domain.entities.HistoricoEstadoOs;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.HistoricoEstadoOsEntity;
import org.springframework.stereotype.Component;

@Component
public class HistoricoEstadoOsMapper {

    public HistoricoEstadoOsEntity toEntity(HistoricoEstadoOs domain) {
        HistoricoEstadoOsEntity entity = new HistoricoEstadoOsEntity();
        entity.setOsId(domain.getOsId());
        entity.setEstadoOs(domain.getEstadoOs());
        entity.setDataInicio(domain.getDataInicio());
        entity.setDataFim(domain.getDataFim());
        return entity;
    }
}
