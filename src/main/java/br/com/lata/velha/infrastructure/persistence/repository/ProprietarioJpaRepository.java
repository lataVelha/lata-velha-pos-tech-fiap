package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.infrastructure.persistence.entity.ProprietarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProprietarioJpaRepository extends JpaRepository<ProprietarioEntity, Long> {

    Optional<ProprietarioEntity> findByDocumento(String documento);

    boolean existsByDocumento(String documento);
}