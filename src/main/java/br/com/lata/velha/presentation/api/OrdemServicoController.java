package br.com.lata.velha.presentation.api;

import br.com.lata.velha.application.dto.request.AddServicoOsRequest;
import br.com.lata.velha.application.dto.request.AprovarOrdemSevicoRequest;
import br.com.lata.velha.application.dto.request.OrdemServicoRequest;
import br.com.lata.velha.application.dto.response.AprovarOrdemServicoResponse;
import br.com.lata.velha.application.dto.response.OrdemServicoResponse;
import br.com.lata.velha.application.usecase.ordemservico.*;
import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.enuns.StatusOrdemServico;
import io.swagger.v3.oas.annotations.Operation;
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

    private final CriarOrdemServicoUseCase criarOrdemServicoUseCase;
    private final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    private final BuscarOrdemServicoUseCase buscarOrdemServicoUseCase;
    private final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;
    private final ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase;
    private final AdicionarServicoUseCase adicionarServicoUseCase;
    private  final FinalizarDiagnosticoUseCase finalizarDiagnosticoUseCase;

    @PostMapping("/create")
    @Operation(summary = "Criar ordem de serviço")
    @ApiResponse(responseCode = "201", description = "Ordem de Serviço criada")
    @ApiResponse(responseCode = "409", description = "Ordem de Serviço cadastrado")
    public ResponseEntity<OrdemServicoResponse> create(
            @Valid @RequestBody OrdemServicoRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(criarOrdemServicoUseCase.execute(request));
    }

    @GetMapping
    @Operation(summary = "Listar Ordens paginado")
    @ApiResponse(responseCode = "200", description = "Ordem de Serviço encontrado")
    @ApiResponse(responseCode = "404", description = "Ordem de Serviço não encontrado")
    public ResponseEntity<PaginatedResult<OrdemServicoResponse>> getOrdems(@RequestParam(required = false) Long id,
                                                                            @RequestParam(required = false) StatusOrdemServico status,
                                                                            @RequestParam(required = false) Long proprietarioId,
                                                                            @RequestParam(required = false) Long mecanicoId,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(buscarOrdemServicoUseCase.execute(id,
                                                                    status,
                                                                    proprietarioId,
                                                                    mecanicoId,
                                                                    page,
                                                                    size));
    }

    @PatchMapping("/{idOs}/{idMecanico}/iniciar")
    @ApiResponse(responseCode = "200", description = "Ordem de Serviço iniciar")
    @ApiResponse(responseCode = "409", description = "Ordem de Serviço iniciada")
    public ResponseEntity<OrdemServicoResponse> startDiagnostic(@PathVariable Long idOs,
                                                                @PathVariable Long idMecanico) {
        return ResponseEntity.ok(iniciarDiagnosticoUseCase.execute(idOs,idMecanico));
    }
    @PatchMapping("/adiciona-servico")
    @ApiResponse(responseCode = "200", description = "Serviço adicionado à Ordem de Serviço")
    @ApiResponse(responseCode = "404", description = "Ordem de Serviço não encontrada")
    @ApiResponse(responseCode = "409", description = "Serviço já existe na Ordem de Serviço ou status inválido")
    public ResponseEntity<OrdemServicoResponse> addService(@Valid @RequestBody AddServicoOsRequest request) {
        return ResponseEntity.ok(adicionarServicoUseCase.execute(request));
    }

    @PatchMapping("/{idOs}/{idFunc}/finalizar-diagnostico")
    @ApiResponse(responseCode = "200", description = "Ordem de Serviço reprovada")
    @ApiResponse(responseCode = "404", description = "Ordem de Serviço não encontrada")
    @ApiResponse(responseCode = "409", description = "Ordem de Serviço já está reprovada ou em status inválido")
    public ResponseEntity<OrdemServicoResponse> finalDiagnostic(@PathVariable Long idOs,
                                                                @PathVariable Long idFunc) {
        return ResponseEntity.ok(finalizarDiagnosticoUseCase.execute(idOs,idFunc));
    }

    @PatchMapping("/aprovar")
    @ApiResponse(responseCode = "200", description = "Ordem de Serviço aprovada")
    @ApiResponse(responseCode = "404", description = "Ordem de Serviço não encontrada")
    @ApiResponse(responseCode = "409", description = "Ordem de Serviço já aprovada ou em status inválido")
    public ResponseEntity<AprovarOrdemServicoResponse> approve(@Valid @RequestBody AprovarOrdemSevicoRequest request) {
        return ResponseEntity.ok(aprovarOrdemServicoUseCase.execute(request));
    }

    @PatchMapping("/{idOs}/{idFunc}/reprovar")
    @ApiResponse(responseCode = "200", description = "Ordem de Serviço reprovada")
    @ApiResponse(responseCode = "404", description = "Ordem de Serviço não encontrada")
    @ApiResponse(responseCode = "409", description = "Ordem de Serviço já está reprovada ou em status inválido")
    public ResponseEntity<OrdemServicoResponse> reprove(@PathVariable Long idOs,
                                                              @PathVariable Long idFunc) {
        return ResponseEntity.ok(reprovarOrdemServicoUseCase.execute(idOs,idFunc));
    }
}