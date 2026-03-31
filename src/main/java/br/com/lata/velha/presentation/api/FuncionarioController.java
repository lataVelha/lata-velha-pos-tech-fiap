package br.com.lata.velha.presentation.api;

import br.com.lata.velha.application.dto.request.AtualizarFuncionarioRequest;
import br.com.lata.velha.application.dto.request.CadastrarFuncionarioRequest;
import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.application.usecase.funcionario.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Funcionários", description = "Gerenciamento de Funcionários")
public class FuncionarioController {

    private final CadastrarFuncionarioUseCase cadastrarUseCase;
    private final BuscarFuncionariosUseCase buscarTodosUseCase;
    private final BuscarFuncionarioPorIdUseCase buscarPorIdUseCase;
    private final AtualizarFuncionarioUseCase atualizarUseCase;
    private final DesativarFuncionarioUseCase desativarUseCase;

    @PostMapping("/cadastrar-funcionario")
    @Operation(summary = "Cadastrar novo funcionário", description = "Cria um novo funcionário associado a um cargo")
    public ResponseEntity<FuncionarioResponse> cadastrar(@Valid @RequestBody CadastrarFuncionarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cadastrarUseCase.execute(request));
    }

    @GetMapping("/funcionarios")
    @Operation(summary = "Listar todos os funcionários ativos", description = "Retorna lista de funcionários ativos no sistema")
    public ResponseEntity<List<FuncionarioResponse>> listar() {
        return ResponseEntity.ok(buscarTodosUseCase.execute());
    }

    @GetMapping("/funcionarios/{id}")
    @Operation(summary = "Buscar funcionário por ID")
    public ResponseEntity<FuncionarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarPorIdUseCase.execute(id));
    }

    @PutMapping("/atualizar-funcionario/{id}")
    @Operation(summary = "Atualizar informações do funcionário")
    public ResponseEntity<FuncionarioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarFuncionarioRequest request) {
        return ResponseEntity.ok(atualizarUseCase.execute(id, request));
    }

    @PatchMapping("/desativar-funcionario/{id}")
    @Operation(summary = "Desativar funcionário", description = "Inativa (Soft Delete) o funcionário no sistema")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        desativarUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}