package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.ServicoOsNotFoundException;
import br.com.lata.velha.ordem_servico.domain.entities.ServicoOS;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoOSRepository;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.ServicoOSMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class ServicoOSRepositoryImpl implements ServicoOSRepository {

    private final ServicoOSJpaRepository jpaRepository;
    private final ServicoOSMapper servicoOSMapper;

    @Override
    public ServicoOS save(ServicoOS servicoOS) {
        var entity = servicoOSMapper.toEntity(servicoOS);
        var saved = jpaRepository.save(entity);
        return servicoOSMapper.toDomain(saved);
    }

    @Override
    public ServicoOS findById(Long id) {
        return jpaRepository.findById(id)
                .map(servicoOSMapper::toDomain)
                .orElseThrow(() -> ServicoOsNotFoundException.fromId(id));
    }

}