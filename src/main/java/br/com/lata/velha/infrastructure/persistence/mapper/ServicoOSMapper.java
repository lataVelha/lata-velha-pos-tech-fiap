package br.com.lata.velha.infrastructure.persistence.mapper;


import org.springframework.stereotype.Component;

import br.com.lata.velha.domain.enuns.StatusServico;
import br.com.lata.velha.domain.model.Servico;
import br.com.lata.velha.domain.model.ServicoOS;
import br.com.lata.velha.infrastructure.persistence.entity.ServicoEntity;
import br.com.lata.velha.infrastructure.persistence.entity.ServicoOSEntity;
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
                entity.getId(),
                servico,
                entity.getValorMaoDeObra()
        );

        if (entity.getStatusServico() != null) {
            domain.setStatus(
                    StatusServico.valueOf(entity.getStatusServico())
            );
        }

        domain.setMecanicoResponsavelId(entity.getMecanicoResponsavelId());
        domain.setIniciadoEm(entity.getIniciadoEm());
        domain.setTerminadoEm(entity.getTerminadoEm());
        domain.setAtualizadoEm(entity.getAtualizadoEm());

        return domain;
    }

    public static ServicoOSEntity toEntity(ServicoOS domain) {

        if (domain == null) {
            return null;
        }

        ServicoOSEntity entity = new ServicoOSEntity();

        entity.setId(domain.getId());
        entity.setValorMaoDeObra(domain.getValorMaoDeObra());

        if (domain.getStatus() != null) {
            entity.setStatusServico(domain.getStatus().name());
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