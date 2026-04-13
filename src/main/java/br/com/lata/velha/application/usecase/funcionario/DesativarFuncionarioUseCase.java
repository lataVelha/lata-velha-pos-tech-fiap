package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DesativarFuncionarioUseCase {
    private final FuncionarioRepository funcionarioRepository;
    private final UserRepository userRepository;

    public void execute(Long id) {
        Funcionario funcionario = funcionarioRepository.getById(id);

        var user = userRepository.getById(funcionario.getUserId());
        user.desativar();

        userRepository.save(user);
    }
}