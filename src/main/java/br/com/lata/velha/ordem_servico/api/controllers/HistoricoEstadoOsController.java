package br.com.lata.velha.ordem_servico.api.controllers;

import br.com.lata.velha.ordem_servico.application.controllers.historicoestadoos.HistoricoEstadoOsCleanController;
import br.com.lata.velha.ordem_servico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.ordem_servico.domain.view.TempoPorEstado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/historico")
@Tag(name = "Historico", description = "Endpoints relacionados ao histórico de estados das ordens de serviço.")
public class HistoricoEstadoOsController {

    private final HistoricoEstadoOsCleanController cleanController;

    @GetMapping("/status")
    @Operation(summary = "Buscar ordem de serviço por status")
    @ApiResponse(responseCode = "200", description = "Historico encontrado")
    @ApiResponse(responseCode = "404", description = "Historico não encontrado")
    public ResponseEntity<List<TempoPorEstado>> buscarOrdensPorStatus(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
                                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(cleanController.buscarHistoricoEstadoOs(inicio, fim));
    }
}
