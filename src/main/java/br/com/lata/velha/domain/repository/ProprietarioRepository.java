package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.model.Proprietario;
import java.util.List;
import java.util.Optional;

public interface ProprietarioRepository {
    Proprietario salvar(Proprietario proprietario);
    Optional<Proprietario> buscarPorId(Long id);
    Optional<Proprietario> buscarPorDocumento(String documento);
    List<Proprietario> listarTodos();
    void deletar(Long id);
    boolean existePorDocumento(String documento);
}