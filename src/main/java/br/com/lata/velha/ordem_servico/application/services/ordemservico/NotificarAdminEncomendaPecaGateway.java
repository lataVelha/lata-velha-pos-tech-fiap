package br.com.lata.velha.ordem_servico.application.services.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.util.List;
import java.util.Optional;

public interface NotificarAdminEncomendaPecaGateway {
    Optional<Peca> findPecaPorId(Long id);
    List<Funcionario> getFuncionariosAdmin();
    String getEmailDoUsuario(UserId userId);
}
