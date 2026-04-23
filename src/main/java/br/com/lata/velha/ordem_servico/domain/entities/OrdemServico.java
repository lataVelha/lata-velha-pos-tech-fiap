package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class OrdemServico {
    private final Long id;
    private final Long proprietarioId;
    private final Long veiculoId;
    private final String reclamacaoCliente;
    private StatusOrdemServico status;

    private final LocalDateTime criadoEm;
    private LocalDateTime iniciadoEm;
    private LocalDateTime finalizadoEm;
    private LocalDateTime entregueEm;
    private LocalDateTime atualizadoEm;

    private Long atendenteInicioId;
    private Long mecanicoResponsavelId;

    private final List<ExecucaoServico> execucaoServicos;

    public OrdemServico(Long id, Long proprietarioId, Long veiculoId, String reclamacaoCliente, StatusOrdemServico status, LocalDateTime criadoEm, LocalDateTime iniciadoEm, LocalDateTime finalizadoEm, LocalDateTime entregueEm, LocalDateTime atualizadoEm, Long atendenteInicioId, Long mecanicoResponsavelId, List<ExecucaoServico> execucaoServicos) {
        this.id = id;
        this.proprietarioId = proprietarioId;
        this.veiculoId = veiculoId;
        this.reclamacaoCliente = reclamacaoCliente;
        this.status = status;
        this.criadoEm = criadoEm;
        this.iniciadoEm = iniciadoEm;
        this.finalizadoEm = finalizadoEm;
        this.entregueEm = entregueEm;
        this.atualizadoEm = atualizadoEm;
        this.atendenteInicioId = atendenteInicioId;
        this.mecanicoResponsavelId = mecanicoResponsavelId;
        this.execucaoServicos = execucaoServicos;
    }

    public static OrdemServico create(Long proprietarioId, Long veiculoId, String reclamacaoCliente, Long atendenteInicioId) {
        return new OrdemServico(null, proprietarioId, veiculoId, reclamacaoCliente, StatusOrdemServico.RECEBIDA, LocalDateTime.now(), null, null, null, null, atendenteInicioId, null, new ArrayList<>());
    }

    /* ================== FLUXO ================== */

    public void iniciarDiagnostico(Long mecanicoId) {
        validarStatus(StatusOrdemServico.RECEBIDA);
        this.mecanicoResponsavelId = mecanicoId;
        this.status = StatusOrdemServico.EM_DIAGNOSTICO;
        touch();
    }

    public void finalizarDiagnostico() {
        validarStatus(StatusOrdemServico.EM_DIAGNOSTICO);
        if (execucaoServicos.isEmpty()) {
            this.status = StatusOrdemServico.FINALIZADA;
            this.finalizadoEm = LocalDateTime.now();
        } else {
            this.status = StatusOrdemServico.AGUARDANDO_APROVACAO;
        }
        touch();
    }

    public void aprovar(Long atendenteId) {
        validarStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
        boolean temServicoNaoDecidido = execucaoServicos.stream()
                .anyMatch(ExecucaoServico::isPendente);
        if (temServicoNaoDecidido)
            throw new IllegalStateException("Todos os serviços devem estar com status APROVADO ou RECUSADO antes de aprovar a OS.");

        boolean nenhumAprovado = execucaoServicos.stream()
                .allMatch(ExecucaoServico::isRecusado);
        if (nenhumAprovado)
            throw new IllegalStateException("É necessário pelo menos um serviço aprovado para aprovar a OS.");
        this.atendenteInicioId = atendenteId;
        this.status = StatusOrdemServico.APROVADA;
        touch();
    }

    public void iniciarExecucao() {
        validarStatus(StatusOrdemServico.APROVADA);
        this.status = StatusOrdemServico.EM_EXECUCAO;
        this.iniciadoEm = LocalDateTime.now();
        touch();
    }

    public void reprovar(Long atendenteId) {
        validarStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);
        this.atendenteInicioId = atendenteId;
        this.status = StatusOrdemServico.REPROVADA;
        this.finalizadoEm = LocalDateTime.now();
        touch();
    }

    public void finalizar(Long mecanicoId) {
        validarStatus(StatusOrdemServico.EM_EXECUCAO);
        boolean existeExecucaoNaoConcluida = execucaoServicos.stream()
                .anyMatch(s -> !s.isConcluido());
        if (existeExecucaoNaoConcluida)
            throw new IllegalStateException("Existem execuções de serviço não finalizadas para esta OS!");
        this.mecanicoResponsavelId = mecanicoId;
        this.status = StatusOrdemServico.FINALIZADA;
        this.finalizadoEm = LocalDateTime.now();
        touch();
    }

    public void entregar(Long atendenteId) {
        if (!this.isFinalizada() && !this.isReprovada())
            throw new IllegalStateException("Esta Ordem de Serviço não foi Finalizada: " + this.getId());
        this.atendenteInicioId =atendenteId;
        this.status = StatusOrdemServico.ENTREGUE;
        this.entregueEm = LocalDateTime.now();
        touch();
    }

    public void adicionarServico(ExecucaoServico servico) {
        if (servico == null)
            throw new IllegalArgumentException("Serviço inválido");

        if (status == StatusOrdemServico.FINALIZADA || status == StatusOrdemServico.ENTREGUE)
            throw new IllegalStateException("Não é possível adicionar serviço");

        boolean jaExiste = execucaoServicos.stream()
                .anyMatch(s -> s.getServicoId()
                        .equals(servico.getServicoId()));

        if (jaExiste)
            throw new IllegalStateException("Serviço já adicionado");

        execucaoServicos.add(servico);
        touch();
    }

    public void iniciarExecucaoServico(Long execucaoId, Long mecanicoId) {
        if (!this.isEmAndamento())
            throw new IllegalStateException("Esta Ordem de Serviço não pode ter serviços iniciados: " + this.getId());
        var execucao = this.execucaoServicos.stream()
                .filter(e -> e.getId().equals(execucaoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado na OS: " + execucaoId));
        if (!this.isEmExecucao()) this.iniciarExecucao();
        execucao.iniciar(mecanicoId);
    }

    public void finalizarExecucaoServico(Long execucaoId, Long mecanicoId) {
        if (!this.isEmExecucao())
            throw new IllegalStateException("Esta Ordem de Serviço não está em execução: " + this.getId());

        var execucao = this.getExecucaoServicos().stream()
                .filter(e -> e.getId().equals(execucaoId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado na OS: " + execucaoId));
        // TODO implemnt separate instalation process, now all parts are installed at once on finish
        execucao.instalarPecasRestantes();
        execucao.finalizar();

        if (this.todosServicosConcluidos())
            this.finalizar(mecanicoId);
    }

    public boolean isAprovada() {
        return this.status == StatusOrdemServico.APROVADA;
    }

    public boolean isEmExecucao() {
        return this.status == StatusOrdemServico.EM_EXECUCAO;
    }

    public boolean isEmAndamento() {
        return this.isAprovada() || this.isEmExecucao();
    }

    public boolean isReprovada() {
        return this.status.equals(StatusOrdemServico.REPROVADA);
    }

    public boolean isFinalizada() {
        return this.status.equals(StatusOrdemServico.FINALIZADA);
    }

    public boolean todosServicosConcluidos() {
        return this.execucaoServicos.stream()
                .allMatch(ExecucaoServico::isConcluido);
    }

    public BigDecimal calcularValorTotal() {
        return execucaoServicos.stream()
                .map(ExecucaoServico::calcularTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /* ================== HELPERS ================== */

    private void validarStatus(StatusOrdemServico esperado) {
        if (this.status != esperado) {
            throw new IllegalStateException(
                    "Status inválido. Atual: " + status +
                            " esperado: " + esperado
            );
        }
    }

    private void touch() {
        this.atualizadoEm = LocalDateTime.now();
    }

    /* ================== GETTERS ================== */

    public Long getId() { return id; }
    public Long getProprietarioId() { return proprietarioId; }
    public Long getVeiculoId() { return veiculoId; }
    public String getReclamacaoCliente() { return reclamacaoCliente; }
    public StatusOrdemServico getStatus() { return status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getIniciadoEm() { return iniciadoEm; }
    public LocalDateTime getFinalizadoEm() { return finalizadoEm; }
    public LocalDateTime getEntregueEm() { return entregueEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public Long getAtendenteInicioId() { return atendenteInicioId; }
    public Long getMecanicoResponsavelId() { return mecanicoResponsavelId; }
    public List<ExecucaoServico> getExecucaoServicos() { return execucaoServicos; }

    /* ================== OBJECT ================== */

    @Override
    public String toString() {
        return "OrdemServico{id=" + id +
                ", status=" + status +
                ", valorTotal=" + calcularValorTotal() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdemServico)) return false;
        OrdemServico that = (OrdemServico) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}