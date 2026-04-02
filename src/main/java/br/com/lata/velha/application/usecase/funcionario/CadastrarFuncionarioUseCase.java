package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.application.assembler.FuncionarioAssembler;
import br.com.lata.velha.application.dto.request.CadastrarFuncionarioRequest;
import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.repository.CargoRepository;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import br.com.lata.velha.domain.valueObject.Senha;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CadastrarFuncionarioUseCase {

    private final FuncionarioRepository funcionarioRepository;
    private final CargoRepository cargoRepository;
    private final FuncionarioAssembler assembler;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioResponse execute(CadastrarFuncionarioRequest request) {
        Cargo cargo = cargoRepository.findById(request.cargoId())
                .orElseThrow(() -> new IllegalArgumentException("Cargo não encontrado"));

        String encoded = passwordEncoder.encode(request.senha());
        Senha senha = Senha.fromHash(encoded, passwordEncoder::matches);
        
        Funcionario funcionario = assembler.toDomain(request, cargo, senha);
        Funcionario saved = funcionarioRepository.save(funcionario);
        
        return assembler.toResponse(saved);
    }
}