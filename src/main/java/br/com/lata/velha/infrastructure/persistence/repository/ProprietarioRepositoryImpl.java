package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.exception.ProprietarioNotFoundException;
import br.com.lata.velha.domain.exception.ResourceAlreadyExistsException;
import br.com.lata.velha.domain.model.Proprietario;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import br.com.lata.velha.infrastructure.persistence.mapper.ProprietarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProprietarioRepositoryImpl implements ProprietarioRepository {

    private final ProprietarioJpaRepository jpaRepository;
    private final ProprietarioPersistenceMapper mapper;

    @Override
    public Proprietario save(Proprietario proprietario) {
        if (proprietario.getId() == null
                && jpaRepository.existsByDocumento(proprietario.getDocumento().getValor())) {
            throw new ResourceAlreadyExistsException(
                    "Já existe um proprietário com este documento: " + proprietario.getDocumento().getFormatted());
        }
        var entity = mapper.toEntity(proprietario);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Proprietario findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new ProprietarioNotFoundException(id));
    }

    @Override
    public Proprietario findByDocumento(String documento) {
        return jpaRepository.findByDocumento(documento)
                .map(mapper::toDomain)
                .orElseThrow(() -> new ProprietarioNotFoundException(documento));
    }

    @Override
    public List<Proprietario> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public PaginatedResult<Proprietario> findAllPaginated(int page, int size) {
        var result = jpaRepository.findAll(PageRequest.of(page, size));
        var content = result.getContent().stream().map(mapper::toDomain).toList();
        return new PaginatedResult<>(content, page, size,
                result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public void deleteById(Long id) {
        if (!jpaRepository.existsById(id)) {
            throw new ProprietarioNotFoundException(id);
        }
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByDocumento(String documento) {
        return jpaRepository.existsByDocumento(documento);
    }
}