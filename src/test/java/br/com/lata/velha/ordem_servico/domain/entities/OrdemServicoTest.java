package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OrdemServico")
class OrdemServicoTest {

    private static OrdemServico recebida() {
        return OrdemServico.create(4L, 3L, "Barulho ao frear", 2L);
    }

    private static OrdemServico emDiagnostico() {
        OrdemServico os = recebida();
        os.iniciarDiagnostico(10L);
        return os;
    }

    private static OrdemServico aguardandoAprovacao() {
        OrdemServico os = emDiagnostico();
        os.finalizarDiagnostico(10L);
        return os;
    }

    private static OrdemServico emExecucao() {
        OrdemServico os = aguardandoAprovacao();
        os.aprovar(2L);
        return os;
    }

    private static OrdemServico finalizada() {
        OrdemServico os = emExecucao();
        os.finalizar(10L);
        return os;
    }

    private static ExecucaoServico execucaoServico(Long servicoId) {
        Servico servico = new Servico(servicoId, "Troca de óleo", "Desc");
        return new ExecucaoServico(servico, new BigDecimal("150.00"));
    }

    @Nested
    @DisplayName("create")
    class Criacao {

        @Test
        @DisplayName("deve criar com status RECEBIDA")
        void deveCriarComStatusRecebida() {
            OrdemServico os = OrdemServico.create(4L, 3L, "Barulho ao frear", 2L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.RECEBIDA);
        }

        @Test
        @DisplayName("deve criar com os IDs corretos")
        void deveCriarComIdCorretos() {
            OrdemServico os = OrdemServico.create(4L, 3L, "Barulho ao frear", 2L);

            assertThat(os.getProprietarioId()).isEqualTo(4L);
            assertThat(os.getVeiculoId()).isEqualTo(3L);
            assertThat(os.getAtendenteInicioId()).isEqualTo(2L);
            assertThat(os.getReclamacaoCliente()).isEqualTo("Barulho ao frear");
        }

        @Test
        @DisplayName("deve criar com id nulo e lista de serviços vazia")
        void deveCriarSemIdEComListaVazia() {
            OrdemServico os = OrdemServico.create(4L, 3L, "Barulho", 2L);

            assertThat(os.getId()).isNull();
            assertThat(os.getExecucaoServicos()).isEmpty();
        }
    }

    @Nested
    @DisplayName("iniciarDiagnostico")
    class IniciarDiagnostico {

        @Test
        @DisplayName("deve transitar para EM_DIAGNOSTICO e definir mecânico")
        void deveIniciarDiagnosticoComSucesso() {
            OrdemServico os = recebida();

            os.iniciarDiagnostico(10L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
            assertThat(os.getMecanicoResponsavelId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("deve lançar exceção se status não for RECEBIDA")
        void deveLancarExcecaoSeStatusNaoForRecebida() {
            OrdemServico os = emDiagnostico();

            assertThatThrownBy(() -> os.iniciarDiagnostico(10L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("finalizarDiagnostico")
    class FinalizarDiagnostico {

        @Test
        @DisplayName("deve transitar para AGUARDANDO_APROVACAO")
        void deveFinalizarDiagnosticoComSucesso() {
            OrdemServico os = emDiagnostico();

            os.finalizarDiagnostico(10L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.AGUARDANDO_APROVACAO);
            assertThat(os.getMecanicoResponsavelId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("deve lançar exceção se status não for EM_DIAGNOSTICO")
        void deveLancarExcecaoSeStatusInvalido() {
            OrdemServico os = recebida();

            assertThatThrownBy(() -> os.finalizarDiagnostico(10L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("aprovar")
    class Aprovar {

        @Test
        @DisplayName("deve transitar para EM_EXECUCAO")
        void deveAprovarComSucesso() {
            OrdemServico os = aguardandoAprovacao();

            os.aprovar(2L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
        }

        @Test
        @DisplayName("deve lançar exceção se status não for AGUARDANDO_APROVACAO")
        void deveLancarExcecaoSeStatusInvalido() {
            OrdemServico os = recebida();

            assertThatThrownBy(() -> os.aprovar(2L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("reprovar")
    class Reprovar {

        @Test
        @DisplayName("deve transitar para FINALIZADA e definir finalizadoEm")
        void deveReprovarComSucesso() {
            OrdemServico os = aguardandoAprovacao();

            os.reprovar(2L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.FINALIZADA);
            assertThat(os.getFinalizadoEm()).isNotNull();
        }

        @Test
        @DisplayName("deve lançar exceção se status não for AGUARDANDO_APROVACAO")
        void deveLancarExcecaoSeStatusInvalido() {
            OrdemServico os = recebida();

            assertThatThrownBy(() -> os.reprovar(2L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("finalizar")
    class Finalizar {

        @Test
        @DisplayName("deve transitar para FINALIZADA quando não há serviços")
        void deveFinalizarSemServicosComSucesso() {
            OrdemServico os = emExecucao();

            os.finalizar(10L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.FINALIZADA);
            assertThat(os.getFinalizadoEm()).isNotNull();
            assertThat(os.getMecanicoResponsavelId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("deve lançar exceção se status não for EM_EXECUCAO")
        void deveLancarExcecaoSeStatusInvalido() {
            OrdemServico os = recebida();

            assertThatThrownBy(() -> os.finalizar(10L))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("deve lançar exceção se existir serviço em execução")
        void deveLancarExcecaoSeExistirServicoEmExecucao() {
            OrdemServico os = emExecucao();
            ExecucaoServico servico = execucaoServico(1L);
            servico.setStatus(StatusExecucaoServico.EM_EXECUCAO);
            os.adicionarServico(servico);

            assertThatThrownBy(() -> os.finalizar(10L))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("serviços em execução");
        }
    }

    @Nested
    @DisplayName("entregar")
    class Entregar {

        @Test
        @DisplayName("deve transitar para ENTREGUE e definir entregueEm")
        void deveEntregarComSucesso() {
            OrdemServico os = finalizada();

            os.entregar(2L);

            assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.ENTREGUE);
            assertThat(os.getEntregueEm()).isNotNull();
        }

        @Test
        @DisplayName("deve lançar exceção se status não for FINALIZADA")
        void deveLancarExcecaoSeStatusInvalido() {
            OrdemServico os = recebida();

            assertThatThrownBy(() -> os.entregar(2L))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("adicionarServico")
    class AdicionarServico {

        @Test
        @DisplayName("deve adicionar serviço à lista")
        void deveAdicionarServicoComSucesso() {
            OrdemServico os = recebida();
            ExecucaoServico servico = execucaoServico(1L);

            os.adicionarServico(servico);

            assertThat(os.getExecucaoServicos()).hasSize(1);
            assertThat(os.getExecucaoServicos().get(0)).isEqualTo(servico);
        }

        @Test
        @DisplayName("deve lançar exceção para serviço nulo")
        void deveLancarExcecaoParaServicoNulo() {
            OrdemServico os = recebida();

            assertThatThrownBy(() -> os.adicionarServico(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Serviço inválido");
        }

        @Test
        @DisplayName("deve lançar exceção se ordem estiver FINALIZADA")
        void deveLancarExcecaoSeOrdemFinalizada() {
            OrdemServico os = finalizada();
            ExecucaoServico servico = execucaoServico(99L);

            assertThatThrownBy(() -> os.adicionarServico(servico))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Não é possível adicionar serviço");
        }

        @Test
        @DisplayName("deve lançar exceção se ordem estiver ENTREGUE")
        void deveLancarExcecaoSeOrdemEntregue() {
            OrdemServico os = finalizada();
            os.entregar(2L);
            ExecucaoServico servico = execucaoServico(99L);

            assertThatThrownBy(() -> os.adicionarServico(servico))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Não é possível adicionar serviço");
        }

        @Test
        @DisplayName("deve lançar exceção se serviço já estiver na ordem")
        void deveLancarExcecaoSeServicoDuplicado() {
            OrdemServico os = recebida();
            ExecucaoServico servico1 = execucaoServico(1L);
            ExecucaoServico servico2 = execucaoServico(1L);
            os.adicionarServico(servico1);

            assertThatThrownBy(() -> os.adicionarServico(servico2))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Serviço já adicionado");
        }
    }

    @Nested
    @DisplayName("calcularValorTotal")
    class CalcularValorTotal {

        @Test
        @DisplayName("deve retornar zero para lista vazia")
        void deveRetornarZeroParaListaVazia() {
            OrdemServico os = recebida();

            assertThat(os.calcularValorTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("deve somar o valor de um único serviço")
        void deveCalcularTotalComUmServico() {
            OrdemServico os = recebida();
            os.adicionarServico(execucaoServico(1L));

            assertThat(os.calcularValorTotal()).isEqualByComparingTo(new BigDecimal("150.00"));
        }

        @Test
        @DisplayName("deve somar os valores de múltiplos serviços")
        void deveCalcularTotalComMultiplosServicos() {
            OrdemServico os = recebida();
            os.adicionarServico(execucaoServico(1L));

            Servico s2 = new Servico(2L, "Alinhamento", "Desc");
            os.adicionarServico(new ExecucaoServico(s2, new BigDecimal("80.00")));

            assertThat(os.calcularValorTotal()).isEqualByComparingTo(new BigDecimal("230.00"));
        }
    }
}
