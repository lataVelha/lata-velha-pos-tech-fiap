package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.application.assembler.FuncionarioAssembler;
import br.com.lata.velha.application.dto.request.CadastrarFuncionarioRequest;
import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.domain.valueObjects.Credential;
import br.com.lata.velha.authentication.domain.valueObjects.Senha;
import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.CargoRepository;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CadastrarFuncionarioUseCase {

    private final FuncionarioRepository funcionarioRepository;
    private final CargoRepository cargoRepository;
    private final FuncionarioAssembler assembler;
    private final PasswordHasher passwordHasher;

    public FuncionarioResponse execute(CadastrarFuncionarioRequest request) {
        Cargo cargo = cargoRepository.findById(request.cargoId())
                .orElseThrow(() -> new IllegalArgumentException("Cargo não encontrado"));

        Senha senha = Senha.fromString(request.senha());
        Credential credential = Credential.fromSenha(senha, passwordHasher);

        Funcionario funcionario = assembler.toDomain(request, cargo, credential);
        Funcionario saved = funcionarioRepository.save(funcionario);

        return assembler.toResponse(saved);
    }
}
