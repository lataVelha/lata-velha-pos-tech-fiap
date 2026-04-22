package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarFuncionarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarFuncionarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.ordem_servico.application.use_cases.funcionario.AtualizarFuncionarioUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.funcionario.BuscarFuncionarioPorIdUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.funcionario.CadastrarFuncionarioUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.funcionario.DesativarFuncionarioUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/funcionarios")
@Tag(name = "Funcionários", description = "Cadastro e gerenciamento de funcionários. O cargo define as permissões: ATENDENTE, MECANICO ou ADMIN.")
public class FuncionarioController {

    private final CadastrarFuncionarioUseCase cadastrarUseCase;
    private final BuscarFuncionarioPorIdUseCase buscarPorIdUseCase;
    private final AtualizarFuncionarioUseCase atualizarUseCase;
    private final DesativarFuncionarioUseCase desativarUseCase;

    @PostMapping
    @Operation(summary = "Cadastrar novo funcionário", description = "Cria funcionário e usuário de acesso (login/senha) vinculados a um cargo existente.")
    @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Cargo não encontrado")
    public ResponseEntity<FuncionarioResponse> cadastrar(@Valid @RequestBody CadastrarFuncionarioRequest request) {
        var output = cadastrarUseCase.execute(request.toCadastrarInput());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new FuncionarioResponse(output.id(), output.nome(), output.cargo(), output.userId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar funcionário por ID")
    @ApiResponse(responseCode = "200", description = "Funcionário encontrado")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<FuncionarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarPorIdUseCase.execute(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar funcionário", description = "Atualiza nome e cargo. Senha e username não são alterados por este endpoint.")
    @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Funcionário ou cargo não encontrado")
    public ResponseEntity<FuncionarioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarFuncionarioRequest request) {
        var output = atualizarUseCase.execute(request.toUpdateUseCaseInput(id));
        return ResponseEntity.ok(new FuncionarioResponse(output.id(), output.nome(), output.cargo(), output.userId().getValue()));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar funcionário", description = "Soft delete — o funcionário é inativado e não poderá fazer login.")
    @ApiResponse(responseCode = "204", description = "Funcionário desativado com sucesso")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        desativarUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}