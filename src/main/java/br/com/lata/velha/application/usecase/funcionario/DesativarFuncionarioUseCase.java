package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DesativarFuncionarioUseCase {

    private final FuncionarioRepository repository;

    public void execute(Long id) {
        Funcionario funcionario = repository.findActiveById(id);

        funcionario.desativar();
        repository.save(funcionario);
    }
}