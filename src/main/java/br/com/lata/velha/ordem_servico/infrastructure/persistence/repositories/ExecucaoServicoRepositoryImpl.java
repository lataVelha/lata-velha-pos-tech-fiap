package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.ExecucaoServicoNotFoundException;
import br.com.lata.velha.ordem_servico.domain.repositories.ExecucaoServicoRepository;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.ExecucaoServicoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class ExecucaoServicoRepositoryImpl implements ExecucaoServicoRepository {

    private final ExecucaoServicoJpaRepository jpaRepository;
    private final ExecucaoServicoMapper execucaoServicoMapper;

    @Override
    public ExecucaoServico save(ExecucaoServico execucaoServico) {
        var entity = execucaoServicoMapper.toEntity(execucaoServico);
        var saved = jpaRepository.save(entity);
        return execucaoServicoMapper.toDomain(saved);
    }

    @Override
    public ExecucaoServico findById(Long id) {
        return jpaRepository.findById(id)
                .map(execucaoServicoMapper::toDomain)
                .orElseThrow(() -> ExecucaoServicoNotFoundException.fromId(id));
    }

}