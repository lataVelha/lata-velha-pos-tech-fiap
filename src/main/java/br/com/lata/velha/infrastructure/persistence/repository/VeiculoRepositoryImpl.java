package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.exception.ProprietarioNotFoundException;
import br.com.lata.velha.domain.model.Veiculo;
import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import br.com.lata.velha.infrastructure.persistence.entity.ProprietarioEntity;
import br.com.lata.velha.infrastructure.persistence.mapper.VeiculoPersistenceMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VeiculoRepositoryImpl implements VeiculoRepository {

    private final VeiculoJpaRepository jpaRepository;
    private final ProprietarioJpaRepository proprietarioJpaRepository;
    private final VeiculoPersistenceMapper mapper;

    public VeiculoRepositoryImpl(VeiculoJpaRepository jpaRepository,
                                 ProprietarioJpaRepository proprietarioJpaRepository,
                                 VeiculoPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.proprietarioJpaRepository = proprietarioJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Veiculo salvar(Veiculo veiculo) {
        ProprietarioEntity proprietarioEntity = proprietarioJpaRepository
                .findById(veiculo.getProprietarioId())
                .orElseThrow(() -> new ProprietarioNotFoundException(veiculo.getProprietarioId()));

        var entity = mapper.toEntity(veiculo, proprietarioEntity);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Veiculo> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Veiculo> buscarPorPlaca(String placa) {
        return jpaRepository.findByPlaca(placa).map(mapper::toDomain);
    }

    @Override
    public List<Veiculo> listarPorProprietario(Long proprietarioId) {
        return jpaRepository.findByProprietarioId(proprietarioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Veiculo> listarTodos() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public PaginatedResult<Veiculo> listarPaginado(int page, int size) {
        var resultado = jpaRepository.findAll(PageRequest.of(page, size));
        var content = resultado.getContent().stream().map(mapper::toDomain).toList();
        return new PaginatedResult<>(content, page, size,
                resultado.getTotalElements(), resultado.getTotalPages());
    }

    @Override
    public void deletar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorPlaca(String placa) {
        return jpaRepository.existsByPlaca(placa);
    }
}