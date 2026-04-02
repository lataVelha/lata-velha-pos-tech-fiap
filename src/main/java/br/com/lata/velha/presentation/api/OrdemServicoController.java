package br.com.lata.velha.presentation.api;

import br.com.lata.velha.application.dto.request.OrdemServicoRequest;
import br.com.lata.velha.application.dto.response.OrdemServicoResponse;
import br.com.lata.velha.application.dto.response.PaginatedResponse;
import br.com.lata.velha.application.usecase.ordemservico.*;
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
@RequestMapping("/ordens-servico")
@RequiredArgsConstructor
@Tag(name = "Ordens de Serviço")
public class OrdemServicoController {

    private final CriarOrdemServicoUseCase createUseCase;

    @PostMapping("/create")
    @Operation(summary = "Criar ordem de serviço")
    @ApiResponse(responseCode = "201", description = "Ordem de Serviço criada")
    @ApiResponse(responseCode = "409", description = "Ordem de Serviço cadastrado")
    public ResponseEntity<OrdemServicoResponse> create(
            @Valid @RequestBody OrdemServicoRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createUseCase.execute(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ordem por ID")
    @ApiResponse(responseCode = "200", description = "Ordem de Serviço encontrado")
    @ApiResponse(responseCode = "404", description = "Ordem de Serviço não encontrado")
    public ResponseEntity<OrdemServicoResponse> findById(@PathVariable Long id) {
        return null; //ResponseEntity.ok(findByIdUseCase.execute(id));
    }

    @GetMapping
    @Operation(summary = "Listar ordens paginado")
    public ResponseEntity<PaginatedResponse<OrdemServicoResponse>> list(
            @Parameter(description = "Página")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Tamanho")
            @RequestParam(defaultValue = "10") int size) {

        return null; //ResponseEntity.ok(listUseCase.execute(page, size));
    }

    @PatchMapping("/{id}/iniciar")
    @ApiResponse(responseCode = "201", description = "Ordem de Serviço iniciar")
    @ApiResponse(responseCode = "409", description = "Ordem de Serviço iniciada")
    public ResponseEntity<OrdemServicoResponse> iniciar(@PathVariable Long id) {
        return null; //ResponseEntity.ok(iniciarUseCase.execute(id));
    }

}