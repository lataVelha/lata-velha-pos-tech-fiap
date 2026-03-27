package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.infrastructure.persistence.entity.ProprietarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProprietarioJpaRepository extends JpaRepository<ProprietarioEntity, Long> {

    Optional<ProprietarioEntity> findByIdAndAtivoTrue(Long id);

    Optional<ProprietarioEntity> findByIdAndAtivoFalse(Long id);

    Optional<ProprietarioEntity> findByDocumentoAndAtivoTrue(String documento);

    List<ProprietarioEntity> findByAtivoTrue();

    Page<ProprietarioEntity> findByAtivoTrue(Pageable pageable);

    boolean existsByDocumento(String documento);
}