package br.com.lata.velha.application.assembler;

import br.com.lata.velha.application.dto.request.CadastrarFuncionarioRequest;
import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.valueObject.Senha;
import org.springframework.stereotype.Component;

@Component
public class FuncionarioAssembler {

    public Funcionario toDomain(CadastrarFuncionarioRequest request, Cargo cargo, Senha senha) {
        return new Funcionario(
                null,
                request.nome(),
                request.username(),
                senha,
                cargo,
                true
        );
    }

    public FuncionarioResponse toResponse(Funcionario model) {
        String cargoNome = null;
        if (model.getCargo() != null) {
            cargoNome = model.getCargo().getNome();
        }
        return new FuncionarioResponse(
                model.getId(),
                model.getNome(),
                model.getUsername(),
                model.isAtivo(),
                cargoNome
        );
    }
}