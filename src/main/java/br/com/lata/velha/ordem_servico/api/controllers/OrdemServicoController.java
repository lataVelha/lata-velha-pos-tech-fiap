package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.AprovarOrdemServicoRequest;
import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.AprovarOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.CriarOrdemServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.AddServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.TempoMedioExecucaoResponse;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.*;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/ordens-servico")
@RequiredArgsConstructor
@Tag(name = "Ordens de Serviço", description = "Gerenciamento do ciclo de vida das ordens de serviço da oficina")
public class OrdemServicoController {

    private final CriarOrdemServicoUseCase criarOrdemServicoUseCase;
    private final IniciarDiagnosticoUseCase iniciarDiagnosticoUseCase;
    private final BuscarOrdemServicoUseCase buscarOrdemServicoUseCase;
    private final AprovarOrdemServicoUseCase aprovarOrdemServicoUseCase;
    private final ReprovarOrdemServicoUseCase reprovarOrdemServicoUseCase;
    private final AdicionarServicoUseCase adicionarServicoUseCase;
    private final FinalizarDiagnosticoUseCase finalizarDiagnosticoUseCase;
    private final IniciarServicoUseCase iniciarServicoUseCase;
    private final FinalizarServicoUseCase finalizarServicoUseCase;
    private final RetirarVeiculoUseCase retirarVeiculoUseCase;
    private final BuscarTempoMedioExecucaoServicosFinalizadosUseCase buscarTempoMedioExecucaoServicosFinalizadosUseCase;

    @PostMapping
    @Operation(
            summary = "Abrir ordem de serviço",
            description = "Atendente registra a entrada do veículo e a reclamação do cliente. Status inicial: RECEBIDA."
    )
    @ApiResponse(responseCode = "201", description = "Ordem de serviço criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Veículo, proprietário ou atendente não encontrado")
    public ResponseEntity<OrdemServicoResponse> create(@Valid @RequestBody CriarOrdemServicoRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(criarOrdemServicoUseCase.execute(request.toCriarOsUseCaseInput()));
    }

    @GetMapping
    @Operation(
            summary = "Listar ordens de serviço",
            description = "Retorna lista paginada de ordens de serviço com filtros opcionais por ID, status, proprietário e mecânico."
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<PaginatedResult<OrdemServicoResponse>> getOrdems(
            @Parameter(description = "Filtrar por ID da OS") @RequestParam(required = false) Long id,
            @Parameter(description = "Filtrar por status da OS") @RequestParam(required = false) StatusOrdemServico status,
            @Parameter(description = "Filtrar por ID do proprietário") @RequestParam(required = false) Long proprietarioId,
            @Parameter(description = "Filtrar por ID do mecânico") @RequestParam(required = false) Long mecanicoId,
            @Parameter(description = "Número da página (começa em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Itens por página") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(buscarOrdemServicoUseCase.execute(id, status, proprietarioId, mecanicoId, page, size));
    }

    @GetMapping("/metricas/tempo-medio-execucao")
    @Operation(summary = "Tempo médio de execução por serviço finalizado [ADMIN] - formato de data: dd/MM/aaaa")
    @ApiResponse(responseCode = "200", description = "Métrica calculada com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetros de período inválidos")
    public ResponseEntity<TempoMedioExecucaoResponse> getAverageExecutionTime(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy")
            LocalDate dataInicio,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy")
            LocalDate dataFim) {

        return ResponseEntity.ok(
                buscarTempoMedioExecucaoServicosFinalizadosUseCase.execute(dataInicio, dataFim)
        );
    }

    @PatchMapping("/{idOs}/{idMecanico}/iniciar-diagnostico")
    @Operation(
            summary = "Iniciar diagnóstico",
            description = "Mecânico assume a OS e inicia o diagnóstico do veículo. Status: RECEBIDA → EM_DIAGNOSTICO."
    )
    @ApiResponse(responseCode = "200", description = "Diagnóstico iniciado com sucesso")
    @ApiResponse(responseCode = "404", description = "OS ou mecânico não encontrado")
    @ApiResponse(responseCode = "422", description = "OS não está no status RECEBIDA")
    public ResponseEntity<OrdemServicoResponse> startDiagnostic(
            @Parameter(description = "ID da ordem de serviço", example = "20") @PathVariable Long idOs,
            @Parameter(description = "ID do mecânico responsável", example = "3") @PathVariable Long idMecanico) {
        return ResponseEntity.ok(iniciarDiagnosticoUseCase.execute(idOs, idMecanico));
    }

    @PatchMapping("/adiciona-servico")
    @Operation(
            summary = "Adicionar serviços à OS",
            description = "Adiciona um ou mais serviços (com peças e mão de obra) à ordem de serviço."
    )
    @ApiResponse(responseCode = "200", description = "Serviços adicionados com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "OS, serviço ou peça não encontrada")
    @ApiResponse(responseCode = "422", description = "Serviço já adicionado ou OS em status que não permite adição")
    public ResponseEntity<OrdemServicoResponse> addService(@Valid @RequestBody AddServicoRequest request) {
        return ResponseEntity.ok(adicionarServicoUseCase.execute(request));
    }

    @PatchMapping("/{idOs}/{idFunc}/finalizar-diagnostico")
    @Operation(
            summary = "Finalizar diagnóstico",
            description = "Mecânico conclui o diagnóstico e envia a OS para aprovação do cliente. Status: EM_DIAGNOSTICO → AGUARDANDO_APROVACAO."
    )
    @ApiResponse(responseCode = "200", description = "Diagnóstico finalizado com sucesso")
    @ApiResponse(responseCode = "404", description = "OS ou mecânico não encontrado")
    @ApiResponse(responseCode = "422", description = "OS não está no status EM_DIAGNOSTICO")
    public ResponseEntity<OrdemServicoResponse> finalDiagnostic(
            @Parameter(description = "ID da ordem de serviço", example = "20") @PathVariable Long idOs,
            @Parameter(description = "ID do mecânico responsável", example = "3") @PathVariable Long idFunc) {
        return ResponseEntity.ok(finalizarDiagnosticoUseCase.execute(idOs, idFunc));
    }

    @PatchMapping("/aprovar")
    @Operation(
            summary = "Aprovar ou reprovar serviços"
    )
    @ApiResponse(responseCode = "200", description = "Serviços avaliados com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "OS ou funcionário não encontrado")
    @ApiResponse(responseCode = "422", description = "OS não está no status AGUARDANDO_APROVACAO")
    public ResponseEntity<AprovarOrdemServicoResponse> approve(@Valid @RequestBody AprovarOrdemServicoRequest request) {
        var output = aprovarOrdemServicoUseCase.execute(request.toInput());
        return ResponseEntity.ok(AprovarOrdemServicoResponse.fromOutput(output));
    }

    @PatchMapping("/{idOs}/{idFunc}/reprovar")
    @Operation(
            summary = "Reprovar ordem de serviço",
            description = "Atendente reprova a OS inteira, recusando todos os serviços pendentes. Status: AGUARDANDO_APROVACAO → REPROVADA."
    )
    @ApiResponse(responseCode = "200", description = "OS reprovada com sucesso")
    @ApiResponse(responseCode = "404", description = "OS ou funcionário não encontrado")
    @ApiResponse(responseCode = "422", description = "OS não está em um status que permite reprovação")
    public ResponseEntity<OrdemServicoResponse> reprove(
            @Parameter(description = "ID da ordem de serviço", example = "20") @PathVariable Long idOs,
            @Parameter(description = "ID do funcionário (atendente)", example = "1") @PathVariable Long idFunc) {
        return ResponseEntity.ok(reprovarOrdemServicoUseCase.execute(idOs, idFunc));
    }

    @PatchMapping("/{idOs}/{idFunc}/iniciar-servico")
    @Operation(
            summary = "Iniciar execução dos serviços",
            description = "Mecânico inicia a execução dos serviços aprovados, reservando as peças do estoque. Status: EM_EXECUCAO (sem transição de OS, apenas inicia os serviços internos)."
    )
    @ApiResponse(responseCode = "200", description = "Execução iniciada com sucesso")
    @ApiResponse(responseCode = "404", description = "OS ou mecânico não encontrado")
    @ApiResponse(responseCode = "422", description = "OS não está no status EM_EXECUCAO")
    public ResponseEntity<OrdemServicoResponse> startService(
            @Parameter(description = "ID da ordem de serviço", example = "20") @PathVariable Long idOs,
            @Parameter(description = "ID do mecânico responsável", example = "3") @PathVariable Long idFunc) {
        return ResponseEntity.ok(iniciarServicoUseCase.execute(idOs, idFunc));
    }

    @PatchMapping("/{idOs}/{idFunc}/finalizar-servico")
    @Operation(
            summary = "Finalizar execução dos serviços",
            description = "Mecânico conclui todos os serviços e finaliza a OS. As peças reservadas são marcadas como instaladas. Status: EM_EXECUCAO → FINALIZADA."
    )
    @ApiResponse(responseCode = "200", description = "OS finalizada com sucesso")
    @ApiResponse(responseCode = "404", description = "OS ou mecânico não encontrado")
    @ApiResponse(responseCode = "422", description = "OS não está no status EM_EXECUCAO ou há serviços/peças pendentes")
    public ResponseEntity<OrdemServicoResponse> finishService(
            @Parameter(description = "ID da ordem de serviço", example = "20") @PathVariable Long idOs,
            @Parameter(description = "ID do mecânico responsável", example = "3") @PathVariable Long idFunc) {
        return ResponseEntity.ok(finalizarServicoUseCase.execute(idOs, idFunc));
    }

    @PatchMapping("/{idOs}/{idFunc}/retirar-veiculo")
    @Operation(
            summary = "Retirar veículo (entrega)",
            description = "Atendente registra a entrega do veículo ao cliente. Baixa o estoque das peças instaladas e calcula o valor total da OS. Status: FINALIZADA → ENTREGUE."
    )
    @ApiResponse(responseCode = "200", description = "Veículo entregue com sucesso, totais calculados")
    @ApiResponse(responseCode = "404", description = "OS ou funcionário não encontrado")
    @ApiResponse(responseCode = "422", description = "OS não está no status FINALIZADA")
    public ResponseEntity<OrdemServicoResponse> removeVehicle(
            @Parameter(description = "ID da ordem de serviço", example = "20") @PathVariable Long idOs,
            @Parameter(description = "ID do funcionário (atendente)", example = "1") @PathVariable Long idFunc) {
        return ResponseEntity.ok(retirarVeiculoUseCase.execute(idOs, idFunc));
    }
}
