package br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ExecucaoServicoEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.PecaAlocadaEntity;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ServicoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExecucaoServicoMapper {

    public ExecucaoServico toDomain(ExecucaoServicoEntity entity) {

        if (entity == null) return null;

        Servico servico = null;

        if (entity.getServico() != null) {
            servico = new Servico(
                    entity.getServico().getId(),
                    entity.getServico().getNome(),
                    entity.getServico().getDescricao()
            );
        }

        ExecucaoServico domain = new ExecucaoServico(
                servico,
                entity.getValorMaoDeObra()
        );

        domain.setId(entity.getId());
        domain.setStatus(entity.getStatusExecucaoServico());
        domain.setMecanicoResponsavelId(entity.getMecanicoResponsavelId());
        domain.setIniciadoEm(entity.getIniciadoEm());
        domain.setTerminadoEm(entity.getTerminadoEm());
        domain.setAtualizadoEm(entity.getAtualizadoEm());

        // 🔥 CORREÇÃO PRINCIPAL: não usar regra de negócio no mapper
        List<PecaAlocada> pecas = new ArrayList<>();

        if (entity.getPecas() != null) {
            entity.getPecas().forEach(pe -> {

                PecaAlocada pecaDomain = new PecaAlocada(
                        pe.getId(),
                        pe.getPecaId(),
                        entity.getId(),
                        pe.getQuantidadeSolicitada(),
                        pe.getQuantidadeReservada(),
                        pe.getQuantidadeEncomendada(),
                        pe.getStatus(),
                        pe.getAtualizado()
                );

                pecas.add(pecaDomain);
            });
        }

        domain.getPecas().addAll(pecas);

        return domain;
    }

    public ExecucaoServicoEntity toEntity(ExecucaoServico domain) {

        if (domain == null) return null;

        ExecucaoServicoEntity entity = new ExecucaoServicoEntity();

        entity.setId(domain.getId());
        entity.setValorMaoDeObra(domain.getValorMaoDeObra());
        entity.setAtendenteId(domain.getAtendenteId());
        entity.setMecanicoResponsavelId(domain.getMecanicoResponsavelId());
        entity.setIniciadoEm(domain.getIniciadoEm());
        entity.setTerminadoEm(domain.getTerminadoEm());
        entity.setAtualizadoEm(domain.getAtualizadoEm());
        entity.setStatusExecucaoServico(domain.getStatus());

        if (domain.getServico() != null) {
            ServicoEntity servicoEntity = new ServicoEntity();
            servicoEntity.setId(domain.getServico().getId());
            entity.setServico(servicoEntity);
        }

        if (domain.getPecas() != null) {

            domain.getPecas().forEach(p -> {

                PecaAlocadaEntity pe = new PecaAlocadaEntity();

                pe.setId(p.getId());
                pe.setQuantidadeSolicitada(p.getQuantidadeSolicitada());
                pe.setQuantidadeReservada(p.getQuantidadeReservada());
                pe.setQuantidadeEncomendada(p.getQuantidadeEncomendada());
                pe.setStatus(p.getStatus());
                pe.setAtualizado(p.getAtualizado());
                pe.setExecucaoServicoId(entity.getId());
                pe.setPecaId(p.getPecaId());

                entity.getPecas().add(pe);
            });
        }

        return entity;
    }
}