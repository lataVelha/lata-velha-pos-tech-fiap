package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.domain.exception.notFoundExceptions.ProprietarioNotFoundException;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.domain.entities.Proprietario;
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
        if (proprietario.getId() == null) {
            validateDocumentoAvailability(proprietario.getDocumento().getValor());
        }
        var entity = mapper.toEntity(proprietario);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Proprietario findActiveById(Long id) {
        return jpaRepository.findByIdAndAtivoTrue(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> ProprietarioNotFoundException.fromId(id));
    }

    @Override
    public Proprietario findActiveByDocumento(String documento) {
        return jpaRepository.findByDocumentoAndAtivoTrue(documento)
                .map(mapper::toDomain)
                .orElseThrow(() -> ProprietarioNotFoundException.fromDocumento(documento));
    }

    @Override
    public Proprietario findInactiveById(Long id) {
        return jpaRepository.findByIdAndAtivoFalse(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> ProprietarioNotFoundException.fromId(id));
    }

    @Override
    public List<Proprietario> findAllActive() {
        return jpaRepository.findByAtivoTrue()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public PaginatedResult<Proprietario> findAllActivePaginated(int page, int size) {
        var result = jpaRepository.findByAtivoTrue(PageRequest.of(page, size));
        var content = result.getContent().stream().map(mapper::toDomain).toList();
        return new PaginatedResult<>(content, page, size,
                result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public boolean existsByDocumento(String documento) {
        return jpaRepository.existsByDocumento(documento);
    }

    // --- private ---

    private void validateDocumentoAvailability(String documento) {
        if (jpaRepository.existsByDocumento(documento)) {
            throw new ResourceAlreadyExistsException(
                    "Já existe um proprietário cadastrado com este documento");
        }
    }
}