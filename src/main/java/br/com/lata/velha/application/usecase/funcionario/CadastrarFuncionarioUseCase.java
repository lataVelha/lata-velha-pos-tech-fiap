package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.application.dto.request.CadastrarFuncionarioRequest;
import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.authentication.domain.entities.User;
import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.domain.valueObjects.Credential;
import br.com.lata.velha.authentication.domain.valueObjects.Senha;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.domain.entities.Cargo;
import br.com.lata.velha.domain.entities.Funcionario;
import br.com.lata.velha.domain.repository.CargoRepository;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import br.com.lata.velha.shared.domain.valueObjects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CadastrarFuncionarioUseCase {
    private final FuncionarioRepository funcionarioRepository;
    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;
    private final PasswordHasher passwordHasher;

    public FuncionarioResponse execute(CadastrarFuncionarioRequest request) {
        //TODO user creation should be responsibility of a shared User Service
        var email = Email.fromString(request.username());
        if(userRepository.existsByEmail(email))
            throw new ResourceAlreadyExistsException("Usuário já existe com o email: " + email);

        var senha = Senha.fromString(request.senha());
        var credential = Credential.fromSenha(senha, passwordHasher);

        Cargo cargo = cargoRepository.getByIdWithRoles(request.cargoId());
        User user = User.create(email, credential, cargo.getRoles());
        userRepository.save(user);

        var funcionario = Funcionario.create(request.nome(), cargo, user.getId());
        var saved = funcionarioRepository.save(funcionario);

        return FuncionarioResponse.fromEntity(saved);
    }
}
