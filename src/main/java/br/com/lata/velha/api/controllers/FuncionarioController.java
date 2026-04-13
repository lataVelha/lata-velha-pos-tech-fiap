package br.com.lata.velha.api.controllers;

import br.com.lata.velha.ordemDeServico.application.dtos.request.AtualizarFuncionarioRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.request.CadastrarFuncionarioRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.ordemDeServico.application.useCases.funcionario.AtualizarFuncionarioUseCase;
import br.com.lata.velha.ordemDeServico.application.useCases.funcionario.BuscarFuncionarioPorIdUseCase;
import br.com.lata.velha.ordemDeServico.application.useCases.funcionario.CadastrarFuncionarioUseCase;
import br.com.lata.velha.ordemDeServico.application.useCases.funcionario.DesativarFuncionarioUseCase;
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
@Tag(name = "Funcionários", description = "Gerenciamento de Funcionários")
public class FuncionarioController {

    private final CadastrarFuncionarioUseCase cadastrarUseCase;
    private final BuscarFuncionarioPorIdUseCase buscarPorIdUseCase;
    private final AtualizarFuncionarioUseCase atualizarUseCase;
    private final DesativarFuncionarioUseCase desativarUseCase;

    @PostMapping
    @Operation(summary = "Cadastrar novo funcionário", description = "Cria um novo funcionário associado a um cargo")
    @ApiResponse(responseCode = "201", description = "Funcionário criado")
    @ApiResponse(responseCode = "404", description = "Cargo não encontrado")
    public ResponseEntity<FuncionarioResponse> cadastrar(@Valid @RequestBody CadastrarFuncionarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cadastrarUseCase.execute(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar funcionário por ID")
    @ApiResponse(responseCode = "200", description = "Funcionário encontrado")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<FuncionarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarPorIdUseCase.execute(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar informações do funcionário")
    @ApiResponse(responseCode = "200", description = "Funcionário atualizado")
    @ApiResponse(responseCode = "404", description = "Funcionário ou cargo não encontrado")
    public ResponseEntity<FuncionarioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarFuncionarioRequest request) {
        return ResponseEntity.ok(atualizarUseCase.execute(id, request));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar funcionário", description = "Inativa (Soft Delete) o funcionário no sistema")
    @ApiResponse(responseCode = "204", description = "Funcionário desativado")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        desativarUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}