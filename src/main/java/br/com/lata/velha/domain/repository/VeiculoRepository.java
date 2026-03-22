package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.model.Veiculo;
import java.util.List;
import java.util.Optional;

public interface VeiculoRepository {
    Veiculo salvar(Veiculo veiculo);
    Optional<Veiculo> buscarPorId(Long id);
    Optional<Veiculo> buscarPorPlaca(String placa);
    List<Veiculo> listarPorProprietario(Long proprietarioId);
    List<Veiculo> listarTodos();
    void deletar(Long id);
    boolean existePorPlaca(String placa);
}