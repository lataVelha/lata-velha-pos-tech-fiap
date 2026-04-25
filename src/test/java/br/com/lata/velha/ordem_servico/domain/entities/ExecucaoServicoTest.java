package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ExecucaoServico")
class ExecucaoServicoTest {

    private static final Long SERVICO_ID = 99L;
    private static final Long OS_ID = 10L;
    private static final Long EXEC_ID = 1L;
    private static final Long ATENDENTE_ID = 2L;
    private static final Long MECANICO_ID = 3L;

    private static ExecucaoServico build(StatusExecucaoServico status, Set<PecaAlocada> pecas) {
        return new ExecucaoServico(EXEC_ID, SERVICO_ID, OS_ID, status, new BigDecimal("150.00"),
                pecas, null, null, null, null, LocalDateTime.now());
    }

    private static ExecucaoServico pendente() {
        return build(StatusExecucaoServico.PENDENTE, new HashSet<>());
    }

    private static ExecucaoServico aprovado() {
        return build(StatusExecucaoServico.APROVADO, new HashSet<>());
    }

    private static ExecucaoServico emExecucao() {
        return build(StatusExecucaoServico.EM_EXECUCAO, new HashSet<>());
    }

    private static PecaAlocada peca(Long pecaId, StatusPecaAlocada status, int solicitada, int reservada, int instalada) {
        return new PecaAlocada(null, pecaId, EXEC_ID, new BigDecimal("30.00"),
                solicitada, reservada, 0, instalada, status, LocalDateTime.now());
    }

    @Nested
    @DisplayName("construtor")
    class Construtor {

        @Test
        @DisplayName("deve lançar exceção quando servicoId é nulo")
        void deveLancarExcecaoQuandoServicoIdNulo() {
            assertThatThrownBy(() ->
                    new ExecucaoServico(null, null, OS_ID, StatusExecucaoServico.PENDENTE,
                            BigDecimal.ZERO, new HashSet<>(), null, null, null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Serviço obrigatório");
        }

        @Test
        @DisplayName("deve lançar exceção quando ordemServicoId é nulo")
        void deveLancarExcecaoQuandoOrdemServicoIdNulo() {
            assertThatThrownBy(() ->
                    new ExecucaoServico(null, SERVICO_ID, null, StatusExecucaoServico.PENDENTE,
                            BigDecimal.ZERO, new HashSet<>(), null, null, null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Ordem de serviço é obrigatória");
        }

        @Test
        @DisplayName("deve lançar exceção quando valorMaoDeObra é nulo")
        void deveLancarExcecaoQuandoValorNulo() {
            assertThatThrownBy(() ->
                    new ExecucaoServico(null, SERVICO_ID, OS_ID, StatusExecucaoServico.PENDENTE,
                            null, new HashSet<>(), null, null, null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Valor inválido");
        }

        @Test
        @DisplayName("deve lançar exceção quando valorMaoDeObra é negativo")
        void deveLancarExcecaoQuandoValorNegativo() {
            assertThatThrownBy(() ->
                    new ExecucaoServico(null, SERVICO_ID, OS_ID, StatusExecucaoServico.PENDENTE,
                            new BigDecimal("-1"), new HashSet<>(), null, null, null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Valor inválido");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("deve criar com status PENDENTE e sem peças")
        void deveCriarComStatusPendente() {
            ExecucaoServico exec = ExecucaoServico.create(SERVICO_ID, OS_ID, new BigDecimal("100.00"));

            assertThat(exec.getId()).isNull();
            assertThat(exec.getServicoId()).isEqualTo(SERVICO_ID);
            assertThat(exec.getOrdemServicoId()).isEqualTo(OS_ID);
            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.PENDENTE);
            assertThat(exec.getValorMaoDeObra()).isEqualByComparingTo("100.00");
            assertThat(exec.getPecas()).isEmpty();
            assertThat(exec.getAtualizadoEm()).isNotNull();
        }
    }

    @Nested
    @DisplayName("aprovar")
    class Aprovar {

        @Test
        @DisplayName("deve transicionar para APROVADO quando pendente sem peças aguardando")
        void deveAprovarQuandoPendenteSemPecas() {
            ExecucaoServico exec = pendente();

            exec.aprovar(ATENDENTE_ID);

            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.APROVADO);
            assertThat(exec.getAtendenteAprovacaoId()).isEqualTo(ATENDENTE_ID);
        }

        @Test
        @DisplayName("deve transicionar para AGUARDANDO_PECA quando tem peça em ENCOMENDA")
        void deveAprovarParaAguardandoPecaQuandoTemPecaEncomendada() {
            var pecaEncomendada = peca(5L, StatusPecaAlocada.ENCOMENDA, 2, 0, 0);
            ExecucaoServico exec = build(StatusExecucaoServico.PENDENTE, new HashSet<>(Set.of(pecaEncomendada)));

            exec.aprovar(ATENDENTE_ID);

            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.AGUARDANDO_PECA);
        }

        @Test
        @DisplayName("deve transicionar para AGUARDANDO_PECA quando tem peça em PARCIAL")
        void deveAprovarParaAguardandoPecaQuandoTemPecaParcial() {
            var pecaParcial = peca(5L, StatusPecaAlocada.PARCIAL, 3, 1, 0);
            ExecucaoServico exec = build(StatusExecucaoServico.PENDENTE, new HashSet<>(Set.of(pecaParcial)));

            exec.aprovar(ATENDENTE_ID);

            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.AGUARDANDO_PECA);
        }

        @Test
        @DisplayName("deve lançar exceção quando status não é PENDENTE")
        void deveLancarExcecaoQuandoNaoPendente() {
            ExecucaoServico exec = aprovado();

            assertThatThrownBy(() -> exec.aprovar(ATENDENTE_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Só serviços pendentes podem ser aprovados");
        }
    }

    @Nested
    @DisplayName("recusar")
    class Recusar {

        @Test
        @DisplayName("deve transicionar para RECUSADO e atribuir atendente")
        void deveRecusarEAtribuirAtendente() {
            ExecucaoServico exec = pendente();

            exec.recusar(ATENDENTE_ID);

            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.RECUSADO);
            assertThat(exec.getAtendenteAprovacaoId()).isEqualTo(ATENDENTE_ID);
            assertThat(exec.getTerminadoEm()).isNotNull();
        }

        @Test
        @DisplayName("deve lançar exceção quando status não é PENDENTE")
        void deveLancarExcecaoQuandoNaoPendente() {
            ExecucaoServico exec = aprovado();

            assertThatThrownBy(() -> exec.recusar(ATENDENTE_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Só serviços pendentes podem ser aprovados");
        }
    }

    @Nested
    @DisplayName("iniciar")
    class Iniciar {

        @Test
        @DisplayName("deve transicionar para EM_EXECUCAO e atribuir mecânico")
        void deveIniciarEAtribuirMecanico() {
            ExecucaoServico exec = aprovado();

            exec.iniciar(MECANICO_ID);

            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.EM_EXECUCAO);
            assertThat(exec.getMecanicoResponsavelId()).isEqualTo(MECANICO_ID);
            assertThat(exec.getIniciadoEm()).isNotNull();
        }

        @Test
        @DisplayName("deve lançar exceção quando status não é APROVADO")
        void deveLancarExcecaoQuandoNaoAprovado() {
            ExecucaoServico exec = pendente();

            assertThatThrownBy(() -> exec.iniciar(MECANICO_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Só serviços aprovados podem ser iniciados");
        }

        @Test
        @DisplayName("deve lançar exceção quando tem peças faltantes")
        void deveLancarExcecaoQuandoTemPecasFaltantes() {
            var pecaEncomendada = peca(5L, StatusPecaAlocada.ENCOMENDA, 2, 0, 0);
            ExecucaoServico exec = build(StatusExecucaoServico.APROVADO, new HashSet<>(Set.of(pecaEncomendada)));

            assertThatThrownBy(() -> exec.iniciar(MECANICO_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Não pode iniciar serviço com peças faltantes");
        }
    }

    @Nested
    @DisplayName("finalizar")
    class Finalizar {

        @Test
        @DisplayName("deve transicionar para FINALIZADO sem peças")
        void deveFinalizarSemPecas() {
            ExecucaoServico exec = emExecucao();

            exec.finalizar();

            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.FINALIZADO);
            assertThat(exec.getTerminadoEm()).isNotNull();
        }

        @Test
        @DisplayName("deve transicionar para FINALIZADO com todas as peças instaladas")
        void deveFinalizarComPecasInstaladas() {
            var pecaInstalada = peca(5L, StatusPecaAlocada.INSTALADA, 2, 2, 2);
            ExecucaoServico exec = build(StatusExecucaoServico.EM_EXECUCAO, new HashSet<>(Set.of(pecaInstalada)));

            exec.finalizar();

            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.FINALIZADO);
        }

        @Test
        @DisplayName("deve lançar exceção quando status não é EM_EXECUCAO")
        void deveLancarExcecaoQuandoNaoEmExecucao() {
            ExecucaoServico exec = aprovado();

            assertThatThrownBy(() -> exec.finalizar())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Não é possível finalizar um serviço que não está em execução");
        }

        @Test
        @DisplayName("deve lançar exceção quando tem peças não instaladas")
        void deveLancarExcecaoQuandoTemPecasNaoInstaladas() {
            var pecaNaoInstalada = peca(5L, StatusPecaAlocada.RESERVADA, 2, 2, 0);
            ExecucaoServico exec = build(StatusExecucaoServico.EM_EXECUCAO, new HashSet<>(Set.of(pecaNaoInstalada)));

            assertThatThrownBy(() -> exec.finalizar())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Não é possível finalizar um serviço com peças não instaladas");
        }
    }

    @Nested
    @DisplayName("adicionarPeca")
    class AdicionarPeca {

        @Test
        @DisplayName("deve adicionar peça sem alterar status quando peça não está aguardando")
        void deveAdicionarPecaSemMudarStatus() {
            ExecucaoServico exec = pendente();
            var pecaReservada = peca(5L, StatusPecaAlocada.RESERVADA, 2, 2, 0);

            exec.adicionarPeca(pecaReservada);

            assertThat(exec.getPecas()).hasSize(1);
            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.PENDENTE);
        }

        @Test
        @DisplayName("deve mudar status para AGUARDANDO_PECA quando peça está em ENCOMENDA")
        void deveMudarStatusParaAguardandoPecaQuandoPecaEncomendada() {
            ExecucaoServico exec = pendente();
            var pecaEncomendada = peca(5L, StatusPecaAlocada.ENCOMENDA, 2, 0, 0);

            exec.adicionarPeca(pecaEncomendada);

            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.AGUARDANDO_PECA);
        }

        @Test
        @DisplayName("deve lançar exceção quando serviço já está finalizado")
        void deveLancarExcecaoQuandoFinalizado() {
            ExecucaoServico exec = build(StatusExecucaoServico.FINALIZADO, new HashSet<>());

            assertThatThrownBy(() -> exec.adicionarPeca(peca(5L, StatusPecaAlocada.RESERVADA, 1, 1, 0)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Serviço já finalizado");
        }

        @Test
        @DisplayName("deve lançar exceção quando peça é nula")
        void deveLancarExcecaoQuandoPecaNula() {
            ExecucaoServico exec = pendente();

            assertThatThrownBy(() -> exec.adicionarPeca(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Peça inválida");
        }

        @Test
        @DisplayName("deve lançar exceção quando peça já foi adicionada")
        void deveLancarExcecaoQuandoPecaDuplicada() {
            ExecucaoServico exec = pendente();
            exec.adicionarPeca(peca(5L, StatusPecaAlocada.RESERVADA, 2, 2, 0));

            assertThatThrownBy(() -> exec.adicionarPeca(peca(5L, StatusPecaAlocada.RESERVADA, 1, 1, 0)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Peça já adicionada ao serviço");
        }
    }

    @Nested
    @DisplayName("reservarPeca")
    class ReservarPeca {

        private static PecaEstoque estoque(Long pecaId, int disponivel) {
            return new PecaEstoque(pecaId, disponivel, disponivel);
        }

        @Test
        @DisplayName("deve lançar exceção quando estoque é nulo")
        void deveLancarExcecaoQuandoEstoqueNulo() {
            ExecucaoServico exec = build(StatusExecucaoServico.AGUARDANDO_PECA,
                    new HashSet<>(Set.of(peca(5L, StatusPecaAlocada.ENCOMENDA, 2, 0, 0))));

            assertThatThrownBy(() -> exec.reservarPeca(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Peca inválida");
        }

        @Test
        @DisplayName("deve lançar exceção quando a peça não pertence ao execucao")
        void deveLancarExcecaoQuandoPecaNaoPertenceAoExecucao() {
            ExecucaoServico exec = build(StatusExecucaoServico.AGUARDANDO_PECA,
                    new HashSet<>(Set.of(peca(5L, StatusPecaAlocada.ENCOMENDA, 2, 0, 0))));

            assertThatThrownBy(() -> exec.reservarPeca(estoque(99L, 5)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("deve reservar peça completamente e mudar status para APROVADO quando não há outras pendentes")
        void deveReservarEMudarParaAprovadoQuandoUnicaPeca() {
            var pecaEncomendada = peca(5L, StatusPecaAlocada.ENCOMENDA, 2, 0, 0);
            ExecucaoServico exec = build(StatusExecucaoServico.AGUARDANDO_PECA,
                    new HashSet<>(Set.of(pecaEncomendada)));

            exec.reservarPeca(estoque(5L, 10));

            assertThat(pecaEncomendada.getStatus()).isEqualTo(StatusPecaAlocada.RESERVADA);
            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.APROVADO);
        }

        @Test
        @DisplayName("deve manter AGUARDANDO_PECA quando ainda há outra peça pendente")
        void deveMantereAguardandoPecaQuandoOutraPecaAindaPendente() {
            var pecaReservada = new PecaAlocada(1L, 5L, EXEC_ID, new BigDecimal("30.00"),
                    2, 0, 0, 0, StatusPecaAlocada.ENCOMENDA, LocalDateTime.now());
            var outraPecaPendente = new PecaAlocada(2L, 6L, EXEC_ID, new BigDecimal("30.00"),
                    1, 0, 0, 0, StatusPecaAlocada.ENCOMENDA, LocalDateTime.now());
            ExecucaoServico exec = build(StatusExecucaoServico.AGUARDANDO_PECA,
                    new HashSet<>(Set.of(pecaReservada, outraPecaPendente)));

            exec.reservarPeca(estoque(5L, 10));

            assertThat(pecaReservada.getStatus()).isEqualTo(StatusPecaAlocada.RESERVADA);
            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.AGUARDANDO_PECA);
        }

        @Test
        @DisplayName("deve manter AGUARDANDO_PECA quando peça fica apenas parcialmente reservada")
        void deveMantereAguardandoPecaQuandoPecaParcial() {
            var pecaEncomendada = peca(5L, StatusPecaAlocada.ENCOMENDA, 5, 0, 0);
            ExecucaoServico exec = build(StatusExecucaoServico.AGUARDANDO_PECA,
                    new HashSet<>(Set.of(pecaEncomendada)));

            exec.reservarPeca(estoque(5L, 2));

            assertThat(pecaEncomendada.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.AGUARDANDO_PECA);
        }
    }

    @Nested
    @DisplayName("instalarPecasRestantes")
    class InstalarPecasRestantes {

        @Test
        @DisplayName("deve instalar todas as peças restantes")
        void deveInstalarTodasAsPecasRestantes() {
            var peca = peca(5L, StatusPecaAlocada.RESERVADA, 2, 2, 0);
            ExecucaoServico exec = build(StatusExecucaoServico.EM_EXECUCAO, new HashSet<>(Set.of(peca)));

            exec.instalarPecasRestantes();

            assertThat(peca.getStatus()).isEqualTo(StatusPecaAlocada.INSTALADA);
            assertThat(peca.getQuantidadeInstalada()).isEqualTo(2);
        }

        @Test
        @DisplayName("não deve lançar exceção quando não há peças")
        void naoDeveLancarExcecaoSemPecas() {
            ExecucaoServico exec = emExecucao();

            exec.instalarPecasRestantes();

            assertThat(exec.getPecas()).isEmpty();
        }
    }

    @Nested
    @DisplayName("booleans")
    class Booleans {

        @Test
        @DisplayName("isPendente retorna true somente quando PENDENTE")
        void isPendente() {
            assertThat(pendente().isPendente()).isTrue();
            assertThat(aprovado().isPendente()).isFalse();
        }

        @Test
        @DisplayName("isAprovado retorna true somente quando APROVADO")
        void isAprovado() {
            assertThat(aprovado().isAprovado()).isTrue();
            assertThat(pendente().isAprovado()).isFalse();
        }

        @Test
        @DisplayName("isRecusado retorna true somente quando RECUSADO")
        void isRecusado() {
            assertThat(build(StatusExecucaoServico.RECUSADO, new HashSet<>()).isRecusado()).isTrue();
            assertThat(pendente().isRecusado()).isFalse();
        }

        @Test
        @DisplayName("isFinalizado retorna true somente quando FINALIZADO")
        void isFinalizado() {
            assertThat(build(StatusExecucaoServico.FINALIZADO, new HashSet<>()).isFinalizado()).isTrue();
            assertThat(pendente().isFinalizado()).isFalse();
        }

        @Test
        @DisplayName("isConcluido retorna true quando FINALIZADO")
        void isConcluidoQuandoFinalizado() {
            assertThat(build(StatusExecucaoServico.FINALIZADO, new HashSet<>()).isConcluido()).isTrue();
        }

        @Test
        @DisplayName("isConcluido retorna true quando RECUSADO")
        void isConcluidoQuandoRecusado() {
            assertThat(build(StatusExecucaoServico.RECUSADO, new HashSet<>()).isConcluido()).isTrue();
        }

        @Test
        @DisplayName("isConcluido retorna false quando PENDENTE")
        void isConcluidoFalsoQuandoPendente() {
            assertThat(pendente().isConcluido()).isFalse();
        }
    }

    @Nested
    @DisplayName("getters")
    class Getters {

        @Test
        @DisplayName("deve retornar todos os valores corretamente")
        void deveRetornarTodosOsValores() {
            var iniciadoEm = LocalDateTime.of(2024, 1, 1, 10, 0);
            var terminadoEm = LocalDateTime.of(2024, 1, 1, 12, 0);
            var atualizadoEm = LocalDateTime.of(2024, 1, 1, 12, 0);

            ExecucaoServico exec = new ExecucaoServico(EXEC_ID, SERVICO_ID, OS_ID,
                    StatusExecucaoServico.EM_EXECUCAO, new BigDecimal("200.00"),
                    new HashSet<>(), ATENDENTE_ID, MECANICO_ID,
                    iniciadoEm, terminadoEm, atualizadoEm);

            assertThat(exec.getId()).isEqualTo(EXEC_ID);
            assertThat(exec.getServicoId()).isEqualTo(SERVICO_ID);
            assertThat(exec.getOrdemServicoId()).isEqualTo(OS_ID);
            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.EM_EXECUCAO);
            assertThat(exec.getValorMaoDeObra()).isEqualByComparingTo("200.00");
            assertThat(exec.getPecas()).isEmpty();
            assertThat(exec.getAtendenteAprovacaoId()).isEqualTo(ATENDENTE_ID);
            assertThat(exec.getMecanicoResponsavelId()).isEqualTo(MECANICO_ID);
            assertThat(exec.getIniciadoEm()).isEqualTo(iniciadoEm);
            assertThat(exec.getTerminadoEm()).isEqualTo(terminadoEm);
            assertThat(exec.getAtualizadoEm()).isEqualTo(atualizadoEm);
        }
    }

    @Nested
    @DisplayName("setStatus")
    class SetStatus {

        @Test
        @DisplayName("deve alterar o status")
        void deveAlterarStatus() {
            ExecucaoServico exec = pendente();

            exec.setStatus(StatusExecucaoServico.APROVADO);

            assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.APROVADO);
        }
    }

    @Nested
    @DisplayName("calcularTotal")
    class CalcularTotal {

        @Test
        @DisplayName("deve retornar zero quando serviço está RECUSADO")
        void deveRetornarZeroQuandoRecusado() {
            ExecucaoServico exec = build(StatusExecucaoServico.RECUSADO, new HashSet<>());

            assertThat(exec.calcularTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("deve ignorar peças ao calcular total quando RECUSADO")
        void deveIgnorarPecasQuandoRecusado() {
            ExecucaoServico exec = build(StatusExecucaoServico.RECUSADO, new HashSet<>());
            exec.adicionarPeca(peca(5L, StatusPecaAlocada.RESERVADA, 3, 3, 0));

            assertThat(exec.calcularTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("deve retornar somente mão de obra quando sem peças")
        void deveRetornarMaoDeObraSemPecas() {
            ExecucaoServico exec = pendente();

            assertThat(exec.calcularTotal()).isEqualByComparingTo(new BigDecimal("150.00"));
        }

        @Test
        @DisplayName("deve somar mão de obra e peças")
        void deveSomarMaoDeObraEPecas() {
            ExecucaoServico exec = aprovado();
            exec.adicionarPeca(peca(5L, StatusPecaAlocada.RESERVADA, 2, 2, 0));

            // 150 + (30 * 2) = 210
            assertThat(exec.calcularTotal()).isEqualByComparingTo(new BigDecimal("210.00"));
        }

        @Test
        @DisplayName("deve calcular total quando status é FINALIZADO")
        void deveCalcularTotalQuandoFinalizado() {
            ExecucaoServico exec = build(StatusExecucaoServico.FINALIZADO, new HashSet<>());

            assertThat(exec.calcularTotal()).isEqualByComparingTo(new BigDecimal("150.00"));
        }
    }
}
