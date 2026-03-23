package br.com.lata.velha.presentation.api;

import br.com.lata.velha.application.dto.request.ProprietarioRequest;
import br.com.lata.velha.application.dto.response.PaginatedResponse;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.application.usecase.proprietario.*;
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

    private final CriarProprietarioUseCase criarUseCase;
    private final BuscarProprietarioUseCase buscarUseCase;
    private final ListarProprietariosUseCase listarUseCase;
    private final AtualizarProprietarioUseCase atualizarUseCase;
    private final DeletarProprietarioUseCase deletarUseCase;

    @PostMapping
    @Operation(summary = "Cadastrar proprietário")
    @ApiResponse(responseCode = "201", description = "Proprietário criado")
    @ApiResponse(responseCode = "409", description = "Documento já cadastrado")
    public ResponseEntity<ProprietarioResponse> criar(@Valid @RequestBody ProprietarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(criarUseCase.execute(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar proprietário por ID")
    @ApiResponse(responseCode = "200", description = "Proprietário encontrado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<ProprietarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarUseCase.porId(id));
    }

    @GetMapping("/documento/{documento}")
    @Operation(summary = "Buscar proprietário por CPF/CNPJ")
    @ApiResponse(responseCode = "200", description = "Proprietário encontrado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<ProprietarioResponse> buscarPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(buscarUseCase.porDocumento(documento));
    }

    @GetMapping
    @Operation(summary = "Listar proprietários paginado")
    public ResponseEntity<PaginatedResponse<ProprietarioResponse>> listarTodos(
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(listarUseCase.execute(page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar proprietário")
    @ApiResponse(responseCode = "200", description = "Proprietário atualizado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<ProprietarioResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody ProprietarioRequest request) {
        return ResponseEntity.ok(atualizarUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar proprietário")
    @ApiResponse(responseCode = "204", description = "Proprietário deletado")
    @ApiResponse(responseCode = "404", description = "Proprietário não encontrado")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        deletarUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}