package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.ordem_servico.application.dtos.request.AjustarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque.AjustarPecaEstoqueUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque.BuscarPecaEstoqueUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque.EntradaPecaEstoqueUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque.SaidaPecaEstoqueUseCase;
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
@Tag(
        name = "Estoque de Peças",
        description = "Controle de estoque. `quantidadeArmazenada` = total físico; `quantidadeDisponivel` = total menos reservas de OS ativas. Peças são reservadas ao aprovar serviços e baixadas ao retirar o veículo."
)
public class PecaEstoqueController {

    private final BuscarPecaEstoqueUseCase buscarPecaEstoqueUseCase;
    private final EntradaPecaEstoqueUseCase entradaPecaEstoqueUseCase;
    private final SaidaPecaEstoqueUseCase saidaPecaEstoqueUseCase;
    private final AjustarPecaEstoqueUseCase ajustarPecaEstoqueUseCase;

    @GetMapping
    @Operation(summary = "Consultar estoque da peça", description = "Retorna quantidade armazenada e disponível (descontando reservas de OS ativas).")
    @ApiResponse(responseCode = "200", description = "Estoque consultado com sucesso")
    @ApiResponse(responseCode = "404", description = "Peça não encontrada ou estoque ainda não inicializado")
    public ResponseEntity<PecaEstoqueResponse> buscar(@PathVariable Long pecaId) {
        return ResponseEntity.ok(buscarPecaEstoqueUseCase.execute(pecaId));
    }

    @PostMapping("/entrada")
    @Operation(summary = "Registrar entrada no estoque", description = "Incrementa a quantidade armazenada. Use ao receber peças de fornecedores.")
    @ApiResponse(responseCode = "200", description = "Entrada registrada — retorna estoque atualizado")
    @ApiResponse(responseCode = "400", description = "Quantidade inválida (deve ser maior que zero)")
    @ApiResponse(responseCode = "404", description = "Peça não encontrada")
    public ResponseEntity<PecaEstoqueResponse> entrada(
            @PathVariable Long pecaId,
            @Valid @RequestBody MovimentarPecaEstoqueRequest request) {
        return ResponseEntity.ok(entradaPecaEstoqueUseCase.execute(pecaId, request));
    }

    @PostMapping("/saida")
    @Operation(summary = "Registrar saída manual do estoque", description = "Saída manual de estoque. Saídas vinculadas a OS são processadas automaticamente ao retirar o veículo.")
    @ApiResponse(responseCode = "200", description = "Saída registrada — retorna estoque atualizado")
    @ApiResponse(responseCode = "400", description = "Quantidade inválida ou saldo insuficiente para a saída solicitada")
    @ApiResponse(responseCode = "404", description = "Peça ou estoque não encontrado")
    public ResponseEntity<PecaEstoqueResponse> saida(
            @PathVariable Long pecaId,
            @Valid @RequestBody MovimentarPecaEstoqueRequest request) {
        return ResponseEntity.ok(saidaPecaEstoqueUseCase.execute(pecaId, request));
    }

    @PatchMapping("/ajuste")
    @Operation(summary = "Ajustar saldo do estoque", description = "Substitui o saldo atual pelo valor informado. Use para corrigir divergências de inventário.")
    @ApiResponse(responseCode = "200", description = "Saldo ajustado para o valor informado")
    @ApiResponse(responseCode = "400", description = "Valor de ajuste inválido (não pode ser negativo)")
    @ApiResponse(responseCode = "404", description = "Peça não encontrada")
    public ResponseEntity<PecaEstoqueResponse> ajustar(
            @PathVariable Long pecaId,
            @Valid @RequestBody AjustarPecaEstoqueRequest request) {
        return ResponseEntity.ok(ajustarPecaEstoqueUseCase.execute(pecaId, request));
    }
}
