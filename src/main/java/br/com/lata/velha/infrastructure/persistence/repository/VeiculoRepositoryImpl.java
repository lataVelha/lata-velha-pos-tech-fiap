package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.exception.ProprietarioNotFoundException;
import br.com.lata.velha.domain.exception.ResourceAlreadyExistsException;
import br.com.lata.velha.domain.exception.VeiculoNotFoundException;
import br.com.lata.velha.domain.model.Veiculo;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import br.com.lata.velha.domain.valueObject.Placa;
import br.com.lata.velha.infrastructure.persistence.entity.ProprietarioEntity;
import br.com.lata.velha.infrastructure.persistence.mapper.VeiculoPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class VeiculoRepositoryImpl implements VeiculoRepository {

    private final VeiculoJpaRepository jpaRepository;
    private final ProprietarioJpaRepository proprietarioJpaRepository;
    private final VeiculoPersistenceMapper mapper;

    @Override
    public Veiculo save(Veiculo veiculo) {
        if (veiculo.getId() == null) {
            validatePlacaAvailability(veiculo.getPlaca());
        }

        ProprietarioEntity proprietarioEntity = proprietarioJpaRepository
                .findById(veiculo.getProprietarioId())
                .orElseThrow(() -> new ProprietarioNotFoundException(veiculo.getProprietarioId()));

        var entity = mapper.toEntity(veiculo, proprietarioEntity);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Veiculo findActiveById(Long id) {
        return jpaRepository.findByIdAndAtivoTrue(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new VeiculoNotFoundException(id));
    }

    @Override
    public List<Veiculo> findActiveByProprietarioId(Long proprietarioId) {
        return jpaRepository.findByProprietarioIdAndAtivoTrue(proprietarioId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Veiculo> findAllActive() {
        return jpaRepository.findByAtivoTrue()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PaginatedResult<Veiculo> findAllActivePaginated(int page, int size) {
        var result = jpaRepository.findByAtivoTrue(PageRequest.of(page, size));
        var content = result.getContent().stream().map(mapper::toDomain).toList();
        return new PaginatedResult<>(content, page, size,
                result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public Veiculo findInactiveById(Long id) {
        return jpaRepository.findByIdAndAtivoFalse(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new VeiculoNotFoundException(id));
    }

    private void validatePlacaAvailability(Placa placa) {
        if (jpaRepository.existsByPlaca(placa.getValor())) {
            throw new ResourceAlreadyExistsException(
                    "Já existe um veículo cadastrado com a placa: " + placa.getFormatted());
        }
    }
}