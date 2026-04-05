package br.com.lata.velha.presentation.api;

import br.com.lata.velha.application.dto.request.ProprietarioRequest;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.application.usecase.proprietario.*;
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

@RestController
@RequestMapping("/proprietarios")
@RequiredArgsConstructor
@Tag(name = "Proprietários", description = "CRUD de proprietários / clientes")
public class ProprietarioController {

    private final CriarProprietarioUseCase createUseCase;
    private final BuscarProprietarioPorIdUseCase findByIdUseCase;
    private final BuscarProprietarioPorDocumentoUseCase findByDocumentoUseCase;
    private final ListarProprietariosUseCase listUseCase;
    private final AtualizarProprietarioUseCase updateUseCase;
    private final DeletarProprietarioUseCase deleteUseCase;
    private final ReativarProprietarioUseCase reactivateUseCase;

    @PostMapping
    @Operation(summary = "Cadastrar proprietário")
    @ApiResponse(responseCode = "201", description = "Proprietário criado")
    @ApiResponse(responseCode = "409", description = "Documento já cadastrado")
    public ResponseEntity<ProprietarioResponse> create(@Valid @RequestBody ProprietarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUseCase.execute(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar proprietário por ID")
    @ApiResponse(responseCode = "200", description = "Proprietário encontrado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<ProprietarioResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(findByIdUseCase.execute(id));
    }

    @GetMapping("/documento/{documento}")
    @Operation(summary = "Buscar proprietário por CPF/CNPJ")
    @ApiResponse(responseCode = "200", description = "Proprietário encontrado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<ProprietarioResponse> findByDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(findByDocumentoUseCase.execute(documento));
    }

    @GetMapping
    @Operation(summary = "Listar proprietários paginado")
    public ResponseEntity<PaginatedResult<ProprietarioResponse>> listAll(
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(listUseCase.execute(page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar proprietário")
    @ApiResponse(responseCode = "200", description = "Proprietário atualizado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<ProprietarioResponse> update(@PathVariable Long id,
                                                       @Valid @RequestBody ProprietarioRequest request) {
        return ResponseEntity.ok(updateUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar proprietário")
    @ApiResponse(responseCode = "204", description = "Proprietário desativado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    @Operation(summary = "Reativar proprietário")
    @ApiResponse(responseCode = "200", description = "Proprietário reativado")
    @ApiResponse(responseCode = "404", description = "Proprietário inativo não encontrado")
    public ResponseEntity<ProprietarioResponse> reactivate(@PathVariable Long id) {
        return ResponseEntity.ok(reactivateUseCase.execute(id));
    }
}