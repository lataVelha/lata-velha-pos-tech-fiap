package br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.OrdemServicoEntity;
import org.springframework.stereotype.Component;

@Component
public class OrdemServicoMapper {

    public static OrdemServico toDomain(OrdemServicoEntity entity) {

        OrdemServico os = new OrdemServico(
                entity.getId(),
                entity.getProprietarioId(),
                entity.getVeiculoId(),
                entity.getReclamacaoCliente(),
                entity.getAtendenteInicioId()
        );

        return os;
    }

    public static OrdemServicoEntity toEntity(OrdemServico domain) {

        OrdemServicoEntity entity = new OrdemServicoEntity();

        entity.setId(domain.getId());
        entity.setProprietarioId(domain.getProprietarioId());
        entity.setVeiculoId(domain.getVeiculoId());
        entity.setReclamacaoCliente(domain.getReclamacaoCliente());
        entity.setAtendenteInicioId(domain.getAtendenteInicioId());
        return entity;
    }
}