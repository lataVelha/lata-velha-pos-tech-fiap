package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.TempoMedioExecucaoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.TempoMedioExecucaoServicoItemResponse;
import br.com.lata.velha.ordem_servico.domain.repositories.ExecucaoServicoMetricaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BuscarTempoMedioExecucaoServicosFinalizadosUseCase {

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final int DEFAULT_WINDOW_DAYS = 30;
    private static final int MAX_WINDOW_DAYS = 180;

    private final ExecucaoServicoMetricaRepository execucaoServicoMetricaRepository;

    public TempoMedioExecucaoResponse execute(LocalDate dataInicio, LocalDate dataFim) {
        LocalDate dataFimAplicada = dataFim != null ? dataFim : LocalDate.now(ZONE_ID);
        LocalDate dataInicioAplicada = dataInicio != null ? dataInicio : dataFimAplicada.minusDays(DEFAULT_WINDOW_DAYS - 1L);

        validarPeriodo(dataInicioAplicada, dataFimAplicada);

        LocalDateTime inicioDateTime = dataInicioAplicada.atStartOfDay();
        LocalDateTime fimDateTime = dataFimAplicada.atTime(LocalTime.MAX);

        List<TempoMedioExecucaoServicoItemResponse> servicos = execucaoServicoMetricaRepository
            .buscarTempoMedioExecucaoServicosFinalizados(inicioDateTime, fimDateTime)
            .stream()
            .map(item -> new TempoMedioExecucaoServicoItemResponse(
                item.servicoId(),
                item.servicoNome(),
                java.math.BigDecimal.valueOf(item.tempoMedioMinutos() == null ? 0.0 : item.tempoMedioMinutos())
                    .setScale(2, RoundingMode.HALF_UP)
            ))
            .toList();

        return new TempoMedioExecucaoResponse(servicos, dataInicioAplicada, dataFimAplicada, ZONE_ID.getId());
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
}
