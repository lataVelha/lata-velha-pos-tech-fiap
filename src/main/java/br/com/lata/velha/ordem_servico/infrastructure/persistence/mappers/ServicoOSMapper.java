package br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers;


import org.springframework.stereotype.Component;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.entities.ServicoOS;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ServicoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ServicoOSEntity;

@Component
public class ServicoOSMapper {

    public ServicoOS toDomain(ServicoOSEntity entity) {

        if (entity == null) {
            return null;
        }

        Servico servico = null;
        if (entity.getServico() != null) {
            servico = new Servico(
                    entity.getServico().getId(),
                    entity.getServico().getNome(),
                    entity.getServico().getDescricao()
            );
        }

        ServicoOS domain = new ServicoOS(
                servico,
                entity.getValorMaoDeObra()
        );

        domain.setId(entity.getId()); // ← FALTAVA

        if (entity.getStatusServico() != null) {
            domain.setStatus(
                    entity.getStatusServico()
            );
        }

        domain.setMecanicoResponsavelId(entity.getMecanicoResponsavelId());
        domain.setIniciadoEm(entity.getIniciadoEm());
        domain.setTerminadoEm(entity.getTerminadoEm());
        domain.setAtualizadoEm(entity.getAtualizadoEm());

        return domain;
    }

    public ServicoOSEntity toEntity(ServicoOS domain) {

        if (domain == null) {
            return null;
        }

        ServicoOSEntity entity = new ServicoOSEntity();

        entity.setId(domain.getId());
        entity.setValorMaoDeObra(domain.getValorMaoDeObra());

        if (domain.getStatus() != null) {
            entity.setStatusServico(domain.getStatus());
        }

        entity.setMecanicoResponsavelId(domain.getMecanicoResponsavelId());
        entity.setIniciadoEm(domain.getIniciadoEm());
        entity.setTerminadoEm(domain.getTerminadoEm());
        entity.setAtualizadoEm(domain.getAtualizadoEm());

        if (domain.getServico() != null) {
            ServicoEntity servicoEntity = new ServicoEntity();
            servicoEntity.setId(domain.getServico().getId());
            entity.setServico(servicoEntity);
        }

        return entity;
    }
}