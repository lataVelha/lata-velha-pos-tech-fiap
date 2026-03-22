package br.com.lata.velha.presentation.api;

import br.com.lata.velha.application.dto.request.VeiculoRequest;
import br.com.lata.velha.application.dto.response.VeiculoResponse;
import br.com.lata.velha.application.usecase.veiculo.*;
import io.swagger.v3.oas.annotations.Operation;
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

    private final CriarVeiculoUseCase criarUseCase;
    private final BuscarVeiculoUseCase buscarUseCase;
    private final ListarVeiculosUseCase listarUseCase;
    private final AtualizarVeiculoUseCase atualizarUseCase;
    private final DeletarVeiculoUseCase deletarUseCase;

    @PostMapping
    @Operation(summary = "Cadastrar veículo")
    @ApiResponse(responseCode = "201", description = "Veículo criado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    @ApiResponse(responseCode = "409", description = "Placa já cadastrada")
    public ResponseEntity<VeiculoResponse> criar(@Valid @RequestBody VeiculoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(criarUseCase.execute(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar veículo por ID")
    @ApiResponse(responseCode = "200", description = "Veículo encontrado")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    public ResponseEntity<VeiculoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarUseCase.porId(id));
    }

    @GetMapping("/proprietario/{proprietarioId}")
    @Operation(summary = "Listar veículos de um proprietário")
    public ResponseEntity<List<VeiculoResponse>> listarPorProprietario(@PathVariable Long proprietarioId) {
        return ResponseEntity.ok(buscarUseCase.porProprietario(proprietarioId));
    }

    @GetMapping
    @Operation(summary = "Listar todos os veículos")
    public ResponseEntity<List<VeiculoResponse>> listarTodos() {
        return ResponseEntity.ok(listarUseCase.execute());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar veículo")
    @ApiResponse(responseCode = "200", description = "Veículo atualizado")
    @ApiResponse(responseCode = "404", description = "Veículo ou proprietário não encontrado")
    public ResponseEntity<VeiculoResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody VeiculoRequest request) {
        return ResponseEntity.ok(atualizarUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar veículo")
    @ApiResponse(responseCode = "204", description = "Veículo deletado")
    @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        deletarUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}