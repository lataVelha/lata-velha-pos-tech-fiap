package br.com.lata.velha.api.controllers;

import br.com.lata.velha.ordemDeServico.application.dtos.request.AtualizarServicoRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.request.CadastrarServicoRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordemDeServico.application.useCases.servico.AtualizarServicoUseCase;
import br.com.lata.velha.ordemDeServico.application.useCases.servico.BuscarServicoPorIdUseCase;
import br.com.lata.velha.ordemDeServico.application.useCases.servico.BuscarServicosUseCase;
import br.com.lata.velha.ordemDeServico.application.useCases.servico.CadastrarServicoUseCase;
import br.com.lata.velha.ordemDeServico.application.useCases.servico.DesativarServicoUseCase;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/servicos")
@Tag(name = "Serviços", description = "Gerenciamento de Serviços")
public class ServicoController {

    private final CadastrarServicoUseCase cadastrarUseCase;
    private final BuscarServicosUseCase buscarTodosUseCase;
    private final BuscarServicoPorIdUseCase buscarPorIdUseCase;
    private final AtualizarServicoUseCase atualizarUseCase;
    private final DesativarServicoUseCase desativarUseCase;

    @PostMapping
    @Operation(summary = "Cadastrar novo serviço", description = "Cria um novo serviço para execução em ordens de serviço")
    @ApiResponse(responseCode = "201", description = "Serviço criado")
    public ResponseEntity<ServicoResponse> cadastrar(@Valid @RequestBody CadastrarServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cadastrarUseCase.execute(request));
    }

    @GetMapping
    @Operation(summary = "Listar todos os serviços ativos", description = "Retorna lista de serviços ativos no sistema")
    @ApiResponse(responseCode = "200", description = "Serviços listados")
    public ResponseEntity<PaginatedResult<ServicoResponse>> listar(
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(buscarTodosUseCase.execute(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID")
    @ApiResponse(responseCode = "200", description = "Serviço encontrado")
    @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    public ResponseEntity<ServicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarPorIdUseCase.execute(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar informações do serviço")
    @ApiResponse(responseCode = "200", description = "Serviço atualizado")
    @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    public ResponseEntity<ServicoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarServicoRequest request) {
        return ResponseEntity.ok(atualizarUseCase.execute(id, request));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar serviço", description = "Inativa (Soft Delete) o serviço no sistema")
    @ApiResponse(responseCode = "204", description = "Serviço desativado")
    @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        desativarUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
