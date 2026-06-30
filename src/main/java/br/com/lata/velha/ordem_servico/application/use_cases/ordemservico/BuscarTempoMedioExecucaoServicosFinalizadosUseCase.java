package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.TempoMedioExecucaoPorServico;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class BuscarTempoMedioExecucaoServicosFinalizadosUseCase {

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final int DEFAULT_WINDOW_DAYS = 30;
    private static final int MAX_WINDOW_DAYS = 180;

    private final BuscarTempoMedioExecucaoGateway gateway;

    public BuscarTempoMedioExecucaoServicosFinalizadosUseCase(BuscarTempoMedioExecucaoGateway gateway) {
        this.gateway = gateway;
    }

    public Result execute(LocalDate dataInicio, LocalDate dataFim) {
        LocalDate dataFimAplicada = dataFim != null ? dataFim : LocalDate.now(ZONE_ID);
        LocalDate dataInicioAplicada = dataInicio != null ? dataInicio : dataFimAplicada.minusDays(DEFAULT_WINDOW_DAYS - 1L);

        validarPeriodo(dataInicioAplicada, dataFimAplicada);

        LocalDateTime inicioDateTime = dataInicioAplicada.atStartOfDay();
        LocalDateTime fimDateTime = dataFimAplicada.atTime(LocalTime.MAX);

        List<TempoMedioExecucaoPorServico> itens = gateway.buscarTempoMedioExecucao(inicioDateTime, fimDateTime);

        return new Result(itens, dataInicioAplicada, dataFimAplicada);
    }

    private void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data inicial não pode ser maior que data final");
        }
        long quantidadeDias = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
        if (quantidadeDias > MAX_WINDOW_DAYS) {
            throw new IllegalArgumentException("Período máximo permitido é de 180 dias");
        }
    }

    public record Result(List<TempoMedioExecucaoPorServico> itens, LocalDate dataInicio, LocalDate dataFim) {}
}
