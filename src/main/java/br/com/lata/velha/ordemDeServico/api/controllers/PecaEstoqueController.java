package br.com.lata.velha.ordemDeServico.api.controllers;

import br.com.lata.velha.ordemDeServico.application.dtos.request.AjustarPecaEstoqueRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordemDeServico.application.useCases.pecaestoque.AjustarPecaEstoqueUseCase;
import br.com.lata.velha.ordemDeServico.application.useCases.pecaestoque.BuscarPecaEstoqueUseCase;
import br.com.lata.velha.ordemDeServico.application.useCases.pecaestoque.EntradaPecaEstoqueUseCase;
import br.com.lata.velha.ordemDeServico.application.useCases.pecaestoque.SaidaPecaEstoqueUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pecas/{pecaId}/estoque")
@Tag(name = "Estoque de Peças", description = "Movimentações e consulta de estoque de peças")
public class PecaEstoqueController {

    private final BuscarPecaEstoqueUseCase buscarPecaEstoqueUseCase;
    private final EntradaPecaEstoqueUseCase entradaPecaEstoqueUseCase;
    private final SaidaPecaEstoqueUseCase saidaPecaEstoqueUseCase;
    private final AjustarPecaEstoqueUseCase ajustarPecaEstoqueUseCase;

    @GetMapping
    @Operation(summary = "Consultar estoque da peça")
    @ApiResponse(responseCode = "200", description = "Estoque consultado")
    @ApiResponse(responseCode = "404", description = "Peça ou estoque não encontrado")
    public ResponseEntity<PecaEstoqueResponse> buscar(@PathVariable Long pecaId) {
        return ResponseEntity.ok(buscarPecaEstoqueUseCase.execute(pecaId));
    }

    @PostMapping("/entrada")
    @Operation(summary = "Registrar entrada no estoque")
    @ApiResponse(responseCode = "200", description = "Entrada registrada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Peça não encontrada")
    public ResponseEntity<PecaEstoqueResponse> entrada(
            @PathVariable Long pecaId,
            @Valid @RequestBody MovimentarPecaEstoqueRequest request) {
        return ResponseEntity.ok(entradaPecaEstoqueUseCase.execute(pecaId, request));
    }

    @PostMapping("/saida")
    @Operation(summary = "Registrar saída do estoque")
    @ApiResponse(responseCode = "200", description = "Saída registrada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos ou estoque insuficiente")
    @ApiResponse(responseCode = "404", description = "Peça ou estoque não encontrado")
    public ResponseEntity<PecaEstoqueResponse> saida(
            @PathVariable Long pecaId,
            @Valid @RequestBody MovimentarPecaEstoqueRequest request) {
        return ResponseEntity.ok(saidaPecaEstoqueUseCase.execute(pecaId, request));
    }

    @PatchMapping("/ajuste")
    @Operation(summary = "Ajustar saldo de estoque")
    @ApiResponse(responseCode = "200", description = "Saldo ajustado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Peça não encontrada")
    public ResponseEntity<PecaEstoqueResponse> ajustar(
            @PathVariable Long pecaId,
            @Valid @RequestBody AjustarPecaEstoqueRequest request) {
        return ResponseEntity.ok(ajustarPecaEstoqueUseCase.execute(pecaId, request));
    }
}
