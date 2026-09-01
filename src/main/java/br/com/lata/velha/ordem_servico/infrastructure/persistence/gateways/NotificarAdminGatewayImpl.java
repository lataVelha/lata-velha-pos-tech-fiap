package br.com.lata.velha.ordem_servico.infrastructure.persistence.gateways;

import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarAdminEncomendaPecaGateway;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NotificarAdminGatewayImpl implements NotificarAdminEncomendaPecaGateway {

    private final FuncionarioRepository funcionarioRepository;
    private final PecaRepository pecaRepository;
    private final UserRepository userRepository;
    private final Logger logger;

    @Override
    public Optional<Peca> findPecaPorId(Long id) {
        try {
            return Optional.of(pecaRepository.getActiveById(id));
        } catch (Exception e) {
            logger.logError("Falha ao buscar peça para notificação de encomenda - pecaId=" + id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Funcionario> getFuncionariosAdmin() {
        return funcionarioRepository.findAllByCargoNome("ADMIN");
    }

    @Override
    public String getEmailDoUsuario(UserId userId) {
        return userRepository.getById(userId).getEmail().getValor();
    }
}
