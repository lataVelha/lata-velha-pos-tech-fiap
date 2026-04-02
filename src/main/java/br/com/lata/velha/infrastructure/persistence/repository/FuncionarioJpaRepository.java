package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.infrastructure.persistence.entity.FuncionarioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FuncionarioJpaRepository extends JpaRepository<FuncionarioEntity, Long> {

    Optional<FuncionarioEntity> findByUsernameAndAtivoTrue(String username);

    Optional<FuncionarioEntity> findByIdAndAtivoTrue(Long id);

    List<FuncionarioEntity> findByAtivoTrue();

    Page<FuncionarioEntity> findByAtivoTrue(Pageable pageable);
}