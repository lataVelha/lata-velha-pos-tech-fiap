package br.com.lata.velha.presentation.api;

import br.com.lata.velha.application.dto.request.ProprietarioRequest;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.application.usecase.proprietario.*;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/proprietarios")
@Tag(name = "Proprietários", description = "Gerenciamento de Proprietários")
public class ProprietarioController {

    private final CriarProprietarioUseCase criarUseCase;
    private final BuscarProprietarioPorIdUseCase buscarPorIdUseCase;
    private final BuscarProprietarioPorDocumentoUseCase buscarPorDocumentoUseCase;
    private final ListarProprietariosUseCase listarUseCase;
    private final AtualizarProprietarioUseCase atualizarUseCase;
    private final DesativarProprietarioUseCase desativarUseCase;
    private final ReativarProprietarioUseCase reativarUseCase;

    @PostMapping
    @Operation(summary = "Cadastrar proprietário")
    @ApiResponse(responseCode = "201", description = "Proprietário criado")
    @ApiResponse(responseCode = "409", description = "Documento já cadastrado")
    public ResponseEntity<ProprietarioResponse> cadastrar(@Valid @RequestBody ProprietarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(criarUseCase.execute(request));
    }

    @GetMapping
    @Operation(summary = "Listar proprietários ativos paginado")
    @ApiResponse(responseCode = "200", description = "Proprietários listados")
    public ResponseEntity<PaginatedResult<ProprietarioResponse>> listar(
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(listarUseCase.execute(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar proprietário por ID")
    @ApiResponse(responseCode = "200", description = "Proprietário encontrado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<ProprietarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarPorIdUseCase.execute(id));
    }

    @GetMapping("/documento/{documento}")
    @Operation(summary = "Buscar proprietário por CPF/CNPJ")
    @ApiResponse(responseCode = "200", description = "Proprietário encontrado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<ProprietarioResponse> buscarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(buscarPorDocumentoUseCase.execute(documento));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar proprietário")
    @ApiResponse(responseCode = "200", description = "Proprietário atualizado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<ProprietarioResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody ProprietarioRequest request) {
        return ResponseEntity.ok(atualizarUseCase.execute(id, request));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar proprietário (soft delete)")
    @ApiResponse(responseCode = "204", description = "Proprietário desativado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        desativarUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    @Operation(summary = "Reativar proprietário")
    @ApiResponse(responseCode = "200", description = "Proprietário reativado")
    @ApiResponse(responseCode = "404", description = "Proprietário inativo não encontrado")
    public ResponseEntity<ProprietarioResponse> reativar(@PathVariable Long id) {
        return ResponseEntity.ok(reativarUseCase.execute(id));
    }
}