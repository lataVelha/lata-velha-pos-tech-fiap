package br.com.lata.velha.presentation.api;

import br.com.lata.velha.application.dto.request.VeiculoRequest;
import br.com.lata.velha.application.dto.response.VeiculoResponse;
import br.com.lata.velha.application.usecase.veiculo.*;
import br.com.lata.velha.domain.common.PaginatedResult;
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
@RequestMapping("/veiculos")
@RequiredArgsConstructor
@Tag(name = "Veículos", description = "CRUD de veículos")
public class VeiculoController {

    private final CriarVeiculoUseCase createUseCase;
    private final BuscarVeiculoPorIdUseCase findByIdUseCase;
    private final ListarVeiculosPorProprietarioUseCase listByProprietarioUseCase;
    private final ListarVeiculosUseCase listUseCase;
    private final AtualizarVeiculoUseCase updateUseCase;
    private final DeletarVeiculoUseCase deleteUseCase;
    private final ReativarVeiculoUseCase reactivateUseCase;


    @PostMapping
    @Operation(summary = "Cadastrar veículo")
    @ApiResponse(responseCode = "201", description = "Veículo criado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    @ApiResponse(responseCode = "409", description = "Placa já cadastrada")
    public ResponseEntity<VeiculoResponse> create(@Valid @RequestBody VeiculoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUseCase.execute(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar veículo por ID")
    @ApiResponse(responseCode = "200", description = "Veículo encontrado")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    public ResponseEntity<VeiculoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(findByIdUseCase.execute(id));
    }

    @GetMapping("/proprietario/{proprietarioId}")
    @Operation(summary = "Listar veículos de um proprietário")
    public ResponseEntity<List<VeiculoResponse>> listByProprietario(@PathVariable Long proprietarioId) {
        return ResponseEntity.ok(listByProprietarioUseCase.execute(proprietarioId));
    }

    @GetMapping
    @Operation(summary = "Listar veículos paginado")
    public ResponseEntity<PaginatedResult<VeiculoResponse>> listAll(
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(listUseCase.execute(page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar veículo")
    @ApiResponse(responseCode = "200", description = "Veículo atualizado")
    @ApiResponse(responseCode = "404", description = "Veículo ou proprietário não encontrado")
    public ResponseEntity<VeiculoResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody VeiculoRequest request) {
        return ResponseEntity.ok(updateUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar veículo")
    @ApiResponse(responseCode = "204", description = "Veículo desativado")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Reativar veículo")
    @ApiResponse(responseCode = "200", description = "Veículo reativado")
    @ApiResponse(responseCode = "404", description = "Veículo inativo não encontrado")
    public ResponseEntity<VeiculoResponse> reactivate(@PathVariable Long id) {
        return ResponseEntity.ok(reactivateUseCase.execute(id));
    }
}