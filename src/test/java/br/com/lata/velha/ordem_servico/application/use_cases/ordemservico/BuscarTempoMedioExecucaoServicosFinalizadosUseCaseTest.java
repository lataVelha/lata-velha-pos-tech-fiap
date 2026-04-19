package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.TempoMedioExecucaoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.TempoMedioExecucaoPorServico;
import br.com.lata.velha.ordem_servico.domain.repositories.ExecucaoServicoMetricaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarTempoMedioExecucaoServicosFinalizadosUseCaseTest {

    @Mock
    private ExecucaoServicoMetricaRepository execucaoServicoMetricaRepository;

    @InjectMocks
    private BuscarTempoMedioExecucaoServicosFinalizadosUseCase useCase;

    @Test
    void deveRetornarTempoMedioAgrupadoPorServico() {
        LocalDate dataInicio = LocalDate.parse("2026-01-01");
        LocalDate dataFim = LocalDate.parse("2026-01-31");

        when(execucaoServicoMetricaRepository.buscarTempoMedioExecucaoServicosFinalizados(
                dataInicio.atStartOfDay(),
                dataFim.atTime(LocalTime.MAX)
        )).thenReturn(List.of(
                new TempoMedioExecucaoPorServico(1L, "Balanceamento", 12.345),
                new TempoMedioExecucaoPorServico(5L, "Troca freio", 37.2)
        ));

        TempoMedioExecucaoResponse response = useCase.execute(dataInicio, dataFim);

        assertThat(response.servicos()).hasSize(2);
        assertThat(response.servicos().get(0).servicoId()).isEqualTo(1L);
        assertThat(response.servicos().get(0).servicoNome()).isEqualTo("Balanceamento");
        assertThat(response.servicos().get(0).tempoMedioMinutos()).isEqualTo(new BigDecimal("12.35"));
        assertThat(response.servicos().get(1).tempoMedioMinutos()).isEqualTo(new BigDecimal("37.20"));
        assertThat(response.dataInicio()).isEqualTo(dataInicio);
        assertThat(response.dataFim()).isEqualTo(dataFim);
        assertThat(response.timezone()).isEqualTo("America/Sao_Paulo");

        verify(execucaoServicoMetricaRepository).buscarTempoMedioExecucaoServicosFinalizados(
                dataInicio.atStartOfDay(),
                dataFim.atTime(LocalTime.MAX)
        );
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverDados() {
        LocalDate dataInicio = LocalDate.parse("2026-01-01");
        LocalDate dataFim = LocalDate.parse("2026-01-31");

        when(execucaoServicoMetricaRepository.buscarTempoMedioExecucaoServicosFinalizados(
                dataInicio.atStartOfDay(),
                dataFim.atTime(LocalTime.MAX)
        )).thenReturn(List.of());

        TempoMedioExecucaoResponse response = useCase.execute(dataInicio, dataFim);

        assertThat(response.servicos()).isEmpty();
        assertThat(response.dataInicio()).isEqualTo(dataInicio);
        assertThat(response.dataFim()).isEqualTo(dataFim);
    }

    @Test
    void deveLancarExcecaoQuandoPeriodoForInvalido() {
        LocalDate dataInicio = LocalDate.parse("2026-02-01");
        LocalDate dataFim = LocalDate.parse("2026-01-31");

        assertThatThrownBy(() -> useCase.execute(dataInicio, dataFim))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Data inicial não pode ser maior que data final");
    }

    @Test
    void deveLancarExcecaoQuandoPeriodoExcederLimite() {
        LocalDate dataInicio = LocalDate.parse("2026-01-01");
        LocalDate dataFim = LocalDate.parse("2026-07-31");

        assertThatThrownBy(() -> useCase.execute(dataInicio, dataFim))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Período máximo permitido é de 180 dias");
    }

    @Test
    void deveAplicarPeriodoPadraoQuandoNaoInformado() {
        when(execucaoServicoMetricaRepository.buscarTempoMedioExecucaoServicosFinalizados(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(new TempoMedioExecucaoPorServico(1L, "Balanceamento", 20.0)));

        TempoMedioExecucaoResponse response = useCase.execute(null, null);

        assertThat(response.servicos()).hasSize(1);
        assertThat(response.servicos().get(0).tempoMedioMinutos()).isEqualTo(new BigDecimal("20.00"));
        assertThat(response.dataFim()).isNotNull();
        assertThat(response.dataInicio()).isEqualTo(response.dataFim().minusDays(29));
    }
}
