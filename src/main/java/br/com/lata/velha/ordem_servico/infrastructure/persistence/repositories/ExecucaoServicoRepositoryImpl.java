package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.ExecucaoServicoNotFoundException;
import br.com.lata.velha.ordem_servico.domain.repositories.ExecucaoServicoRepository;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ExecucaoServicoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class ExecucaoServicoRepositoryImpl implements ExecucaoServicoRepository {

    private final ExecucaoServicoJpaRepository jpaRepository;

    @Override
    public ExecucaoServico save(ExecucaoServico execucaoServico) {
        var entity = ExecucaoServicoEntity.fromDomain(execucaoServico);
        var saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public ExecucaoServico findById(Long id) {
        return jpaRepository.findById(id)
                .map(ExecucaoServicoEntity::toDomain)
                .orElseThrow(() -> ExecucaoServicoNotFoundException.fromId(id));
    }

}