package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.exception.OrdemServicoNotFoundException;
import br.com.lata.velha.domain.model.OrdemServico;
import br.com.lata.velha.domain.repository.OrdemServicoRepository;
import br.com.lata.velha.infrastructure.persistence.mapper.OrdemServicoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrdemServicoRepositoryImpl implements OrdemServicoRepository {

    private final OrdemServicoJpaRepository jpaRepository;

    @Override
    public OrdemServico save(OrdemServico ordemServico) {
        var entity = OrdemServicoMapper.toEntity(ordemServico);
        var saved = jpaRepository.save(entity);
        return OrdemServicoMapper.toDomain(saved);
    }

    @Override
    public OrdemServico findById(Long id) {
        return jpaRepository.findById(id)
                .map(OrdemServicoMapper::toDomain)
                .orElseThrow(() -> new OrdemServicoNotFoundException(id));
    }

}