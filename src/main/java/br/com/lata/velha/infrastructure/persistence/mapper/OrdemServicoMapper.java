package br.com.lata.velha.infrastructure.persistence.mapper;

import br.com.lata.velha.domain.model.OrdemServico;
import br.com.lata.velha.infrastructure.persistence.entity.OrdemServicoEntity;
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

        return entity;
    }
}