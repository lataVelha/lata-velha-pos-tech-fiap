package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.exception.ProprietarioNotFoundException;
import br.com.lata.velha.domain.exception.ResourceAlreadyExistsException;
import br.com.lata.velha.domain.exception.VeiculoNotFoundException;
import br.com.lata.velha.domain.model.Veiculo;
import br.com.lata.velha.domain.repository.VeiculoRepository;
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
        if (veiculo.getId() == null && jpaRepository.existsByPlaca(veiculo.getPlaca().getValor())) {
            throw new ResourceAlreadyExistsException(
                    "Já existe um veículo com a placa: " + veiculo.getPlaca().getFormatted());
        }

        ProprietarioEntity proprietarioEntity = proprietarioJpaRepository
                .findById(veiculo.getProprietarioId())
                .orElseThrow(() -> new ProprietarioNotFoundException(veiculo.getProprietarioId()));

        var entity = mapper.toEntity(veiculo, proprietarioEntity);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Veiculo findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new VeiculoNotFoundException(id));
    }

    @Override
    public Veiculo findByPlaca(String placa) {
        return jpaRepository.findByPlaca(placa)
                .map(mapper::toDomain)
                .orElseThrow(() -> new VeiculoNotFoundException(
                        "Veículo não encontrado com placa: " + placa));
    }

    @Override
    public List<Veiculo> findByProprietarioId(Long proprietarioId) {
        return jpaRepository.findByProprietarioId(proprietarioId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Veiculo> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public PaginatedResult<Veiculo> findAllPaginated(int page, int size) {
        var result = jpaRepository.findAll(PageRequest.of(page, size));
        var content = result.getContent().stream().map(mapper::toDomain).toList();
        return new PaginatedResult<>(content, page, size,
                result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public void deleteById(Long id) {
        if (!jpaRepository.existsById(id)) {
            throw new VeiculoNotFoundException(id);
        }
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByPlaca(String placa) {
        return jpaRepository.existsByPlaca(placa);
    }
}