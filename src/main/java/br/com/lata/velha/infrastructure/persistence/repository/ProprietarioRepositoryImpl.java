package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.domain.model.Proprietario;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import br.com.lata.velha.infrastructure.persistence.mapper.ProprietarioPersistenceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProprietarioRepositoryImpl implements ProprietarioRepository {

    private final ProprietarioJpaRepository jpaRepository;
    private final ProprietarioPersistenceMapper mapper;

    public ProprietarioRepositoryImpl(ProprietarioJpaRepository jpaRepository,
                                      ProprietarioPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Proprietario salvar(Proprietario proprietario) {
        var entity = mapper.toEntity(proprietario);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Proprietario> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Proprietario> buscarPorDocumento(String documento) {
        return jpaRepository.findByDocumento(documento).map(mapper::toDomain);
    }

    @Override
    public List<Proprietario> listarTodos() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deletar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorDocumento(String documento) {
        return jpaRepository.existsByDocumento(documento);
    }
}