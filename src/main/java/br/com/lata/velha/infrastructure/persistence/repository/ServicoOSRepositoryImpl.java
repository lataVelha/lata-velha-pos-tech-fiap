package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.exception.ServicoOsNotFoundException;
import br.com.lata.velha.domain.model.ServicoOS;
import br.com.lata.velha.domain.repository.ServicoOSRepository;
import br.com.lata.velha.infrastructure.persistence.mapper.ServicoOSMapper;
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
                .orElseThrow(() -> new ServicoOsNotFoundException(id));
    }

}