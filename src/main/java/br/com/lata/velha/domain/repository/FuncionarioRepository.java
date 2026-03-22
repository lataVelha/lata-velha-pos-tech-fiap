package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.model.Funcionario;
import java.util.Optional;
/**
 * Contrato de persistência do domínio.
 * A implementação concreta fica em infrastructure.persistence.repository
 */
public interface FuncionarioRepository {

    Optional<Funcionario> buscarPorNome(String username);
}