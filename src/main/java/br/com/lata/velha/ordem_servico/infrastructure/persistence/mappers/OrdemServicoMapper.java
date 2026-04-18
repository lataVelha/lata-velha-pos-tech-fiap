package br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.OrdemServicoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdemServicoMapper {

    private final ExecucaoServicoMapper execucaoServicoMapper;

    public OrdemServico toDomain(OrdemServicoEntity entity) {

        if (entity == null) {
            return null;
        }

        OrdemServico os = new OrdemServico(
                entity.getId(),
                entity.getProprietarioId(),
                entity.getVeiculoId(),
                entity.getReclamacaoCliente(),
                entity.getAtendenteInicioId()
        );

        os.setStatus(entity.getStatus());
        os.setIniciadoEm(entity.getIniciadoEm());
        os.setAtualizadoEm(entity.getAtualizadoEm());
        os.setFinalizadoEm(entity.getFinalizadoEm());
        os.setEntregueEm(entity.getEntregueEm());
        os.setMecanicoResponsavelId(entity.getMecanicoFinalId());

        if (entity.getServicos() != null) {
            entity.getServicos().forEach(servicoEntity -> {

                var execucao = execucaoServicoMapper.toDomain(servicoEntity);

                os.getExecucaoServicos().add(execucao);
            });
        }

        return os;
    }

    public OrdemServicoEntity toEntity(OrdemServico domain) {

        if (domain == null) {
            return null;
        }

        OrdemServicoEntity entity = new OrdemServicoEntity();

        entity.setId(domain.getId());
        entity.setProprietarioId(domain.getProprietarioId());
        entity.setVeiculoId(domain.getVeiculoId());
        entity.setReclamacaoCliente(domain.getReclamacaoCliente());
        entity.setAtendenteInicioId(domain.getAtendenteInicioId());

        entity.setStatus(domain.getStatus());
        entity.setIniciadoEm(domain.getIniciadoEm());
        entity.setAtualizadoEm(domain.getAtualizadoEm());
        entity.setFinalizadoEm(domain.getFinalizadoEm());
        entity.setEntregueEm(domain.getEntregueEm());
        entity.setMecanicoFinalId(domain.getMecanicoResponsavelId());

        if (domain.getExecucaoServicos() != null) {
            entity.setServicos(
                    domain.getExecucaoServicos()
                            .stream()
                            .map(execucao -> {
                                var servicoEntity = execucaoServicoMapper.toEntity(execucao);

                                servicoEntity.setOrdemServico(entity);

                                return servicoEntity;
                            })
                            .toList()
            );
        }

        return entity;
    }
}