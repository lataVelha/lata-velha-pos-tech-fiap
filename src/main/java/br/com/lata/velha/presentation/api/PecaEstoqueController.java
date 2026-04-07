package br.com.lata.velha.presentation.api;

import br.com.lata.velha.application.dto.request.AjustarPecaEstoqueRequest;
import br.com.lata.velha.application.dto.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.application.dto.response.PecaEstoqueResponse;
import br.com.lata.velha.application.usecase.pecaestoque.AjustarPecaEstoqueUseCase;
import br.com.lata.velha.application.usecase.pecaestoque.BuscarPecaEstoqueUseCase;
import br.com.lata.velha.application.usecase.pecaestoque.EntradaPecaEstoqueUseCase;
import br.com.lata.velha.application.usecase.pecaestoque.SaidaPecaEstoqueUseCase;
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
    public ResponseEntity<PecaEstoqueResponse> buscar(@PathVariable Long pecaId) {
        return ResponseEntity.ok(buscarPecaEstoqueUseCase.execute(pecaId));
    }

    @PostMapping("/entrada")
    @Operation(summary = "Registrar entrada no estoque")
    @ApiResponse(responseCode = "200", description = "Entrada registrada")
    public ResponseEntity<PecaEstoqueResponse> entrada(
            @PathVariable Long pecaId,
            @Valid @RequestBody MovimentarPecaEstoqueRequest request) {
        return ResponseEntity.ok(entradaPecaEstoqueUseCase.execute(pecaId, request));
    }

    @PostMapping("/saida")
    @Operation(summary = "Registrar saída do estoque")
    @ApiResponse(responseCode = "200", description = "Saída registrada")
    public ResponseEntity<PecaEstoqueResponse> saida(
            @PathVariable Long pecaId,
            @Valid @RequestBody MovimentarPecaEstoqueRequest request) {
        return ResponseEntity.ok(saidaPecaEstoqueUseCase.execute(pecaId, request));
    }

    @PatchMapping("/ajuste")
    @Operation(summary = "Ajustar saldo de estoque")
    @ApiResponse(responseCode = "200", description = "Saldo ajustado")
    public ResponseEntity<PecaEstoqueResponse> ajustar(
            @PathVariable Long pecaId,
            @Valid @RequestBody AjustarPecaEstoqueRequest request) {
        return ResponseEntity.ok(ajustarPecaEstoqueUseCase.execute(pecaId, request));
    }
}
