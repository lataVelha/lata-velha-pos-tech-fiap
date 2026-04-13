package br.com.lata.velha.ordemDeServico.api.controllers;

import br.com.lata.velha.ordemDeServico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordemDeServico.application.useCases.veiculo.*;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/veiculos")
@Tag(name = "Veículos", description = "Gerenciamento de Veículos")
public class VeiculoController {

    private final CriarVeiculoUseCase cadastrarUseCase;
    private final BuscarVeiculoPorIdUseCase buscarPorIdUseCase;
    private final ListarVeiculosPorProprietarioUseCase listarPorProprietarioUseCase;
    private final ListarVeiculosUseCase listarUseCase;
    private final AtualizarVeiculoUseCase atualizarUseCase;
    private final DesativarVeiculoUseCase desativarUseCase;
    private final ReativarVeiculoUseCase reativarUseCase;

    @PostMapping
    @Operation(summary = "Cadastrar veículo")
    @ApiResponse(responseCode = "201", description = "Veículo criado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    @ApiResponse(responseCode = "409", description = "Placa já cadastrada")
    public ResponseEntity<VeiculoResponse> cadastrar(@Valid @RequestBody VeiculoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cadastrarUseCase.execute(request));
    }

    @GetMapping
    @Operation(summary = "Listar veículos ativos paginado")
    @ApiResponse(responseCode = "200", description = "Veículos listados")
    public ResponseEntity<PaginatedResult<VeiculoResponse>> listar(
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(listarUseCase.execute(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar veículo por ID")
    @ApiResponse(responseCode = "200", description = "Veículo encontrado")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    public ResponseEntity<VeiculoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarPorIdUseCase.execute(id));
    }

    @GetMapping("/proprietario/{proprietarioId}")
    @Operation(summary = "Listar veículos de um proprietário")
    @ApiResponse(responseCode = "200", description = "Veículos listados")
    public ResponseEntity<List<VeiculoResponse>> listarPorProprietario(@PathVariable Long proprietarioId) {
        return ResponseEntity.ok(listarPorProprietarioUseCase.execute(proprietarioId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar veículo")
    @ApiResponse(responseCode = "200", description = "Veículo atualizado")
    @ApiResponse(responseCode = "404", description = "Veículo ou proprietário não encontrado")
    public ResponseEntity<VeiculoResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody VeiculoRequest request) {
        return ResponseEntity.ok(atualizarUseCase.execute(id, request));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar veículo (soft delete)")
    @ApiResponse(responseCode = "204", description = "Veículo desativado")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        desativarUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar veículo")
    @ApiResponse(responseCode = "200", description = "Veículo reativado")
    @ApiResponse(responseCode = "404", description = "Veículo inativo não encontrado")
    public ResponseEntity<VeiculoResponse> reativar(@PathVariable Long id) {
        return ResponseEntity.ok(reativarUseCase.execute(id));
    }
}