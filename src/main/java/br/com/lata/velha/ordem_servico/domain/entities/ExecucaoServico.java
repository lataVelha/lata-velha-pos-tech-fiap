package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public final class ExecucaoServico {
    private final Long id;
    private final Long servicoId;
    private final Long ordemServicoId;
    private StatusExecucaoServico status;

    private BigDecimal valorMaoDeObra;
    private Set<PecaAlocada> pecas;

    private Long atendenteAprovacaoId;
    private Long mecanicoResponsavelId;

    private LocalDateTime iniciadoEm;
    private LocalDateTime terminadoEm;
    private LocalDateTime atualizadoEm;

    public ExecucaoServico(Long id, Long servicoId, Long ordemServicoId, StatusExecucaoServico status, BigDecimal valorMaoDeObra, Set<PecaAlocada> pecas, Long atendenteAprovacaoId, Long mecanicoResponsavelId, LocalDateTime iniciadoEm, LocalDateTime terminadoEm, LocalDateTime atualizadoEm) {
        validateServicoId(servicoId);
        validateOrdemServicoId(ordemServicoId);
        validateMaodeObra(valorMaoDeObra);
        this.id = id;
        this.status = status;
        this.servicoId = servicoId;
        this.ordemServicoId = ordemServicoId;
        this.atendenteAprovacaoId = atendenteAprovacaoId;
        this.mecanicoResponsavelId = mecanicoResponsavelId;
        this.valorMaoDeObra = valorMaoDeObra;
        this.pecas = pecas;
        this.iniciadoEm = iniciadoEm;
        this.terminadoEm = terminadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static ExecucaoServico create(Long servicoId, Long ordemServicoId, BigDecimal valorMaoDeObra) {
        var statusInicial = StatusExecucaoServico.PENDENTE;
        var atualizadoEm = LocalDateTime.now();
        return new ExecucaoServico(null, servicoId, ordemServicoId, statusInicial, valorMaoDeObra, new HashSet<>(), null, null, null, null, atualizadoEm);
    }

    public void aprovar(Long atendenteId) {
        if (this.status != StatusExecucaoServico.PENDENTE)
            throw new IllegalStateException("Só serviços pendentes podem ser aprovados");
        this.status = statusAprovacao();
        this.atendenteAprovacaoId = atendenteId;
        touch();
    }

    public void recusar(Long atendenteId) {
        if (status != StatusExecucaoServico.PENDENTE)
            throw new IllegalStateException("Só serviços pendentes podem ser aprovados");
        this.status = StatusExecucaoServico.RECUSADO;
        this.atendenteAprovacaoId = atendenteId;
        this.terminadoEm = LocalDateTime.now();
        touch();
    }

    public void iniciar(Long mecanicoResponsavelId) {
        if (!this.isAprovado())
            throw new IllegalStateException("Só serviços aprovados podem ser iniciados");
        var temPecasFaltantes = this.pecas.stream()
                .anyMatch(PecaAlocada::isAguardandoPeca);
        if(temPecasFaltantes)
            throw new IllegalStateException("Não pode iniciar serviço com peças faltantes");
        this.status = StatusExecucaoServico.EM_EXECUCAO;
        this.mecanicoResponsavelId = mecanicoResponsavelId;
        this.iniciadoEm = LocalDateTime.now();
        touch();
    }

    public void finalizar() {
        if (status != StatusExecucaoServico.EM_EXECUCAO)
            throw new IllegalStateException("Não é possível finalizar um serviço que não está em execução");
        this.instalarPecasRestantes();
        var temPecasNaoInstaladas = this.pecas.stream()
                .anyMatch(peca -> !peca.isInstalada());
        if(temPecasNaoInstaladas)
            throw new IllegalStateException("Não é possível finalizar um serviço com peças não instaladas");
        this.status = StatusExecucaoServico.FINALIZADO;
        this.terminadoEm = LocalDateTime.now();
        touch();
    }

    public void adicionarPeca(PecaAlocada peca) {
        if (status == StatusExecucaoServico.FINALIZADO)
            throw new IllegalStateException("Serviço já finalizado");
        if (peca == null)
            throw new IllegalArgumentException("Peça inválida");
        var pecaDuplicada = pecas.stream()
                .anyMatch(p -> p.getPecaId().equals(peca.getPecaId()));
        if (pecaDuplicada)
            throw new IllegalStateException("Peça já adicionada ao serviço");
        if(peca.isAguardandoPeca())
            this.status = StatusExecucaoServico.AGUARDANDO_PECA;
        pecas.add(peca);
        touch();
    }

    public void reservarPeca(PecaEstoque estoque) {
        if(estoque == null)
            throw new IllegalArgumentException("Peca inválida");
        var peca = this.pecas.stream()
                .filter(pecaAlocada -> pecaAlocada.getPecaId().equals(estoque.getPecaId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Peca invalida"));
        peca.reservar(estoque);
        if(peca.isReservada())
            this.status = statusAprovacao();
    }

    public void instalarPecasRestantes() {
        this.pecas.forEach(peca -> {
            var pecasParaInstalar = peca.getQuantidadeSolicitada() - peca.getQuantidadeInstalada();
            if (pecasParaInstalar > 0)
                peca.instalar(pecasParaInstalar);
        });
    }

    public BigDecimal calcularTotal() {
        if(this.isRecusado()) return BigDecimal.ZERO;
        BigDecimal totalPecas = this.pecas.stream()
                .map(PecaAlocada::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalPecas.add(valorMaoDeObra);
    }

    public boolean isPendente() {
        return this.status.equals(StatusExecucaoServico.PENDENTE);
    }

    public boolean isAprovado() {
        return this.status.equals(StatusExecucaoServico.APROVADO);
    }

    public boolean isRecusado() {
        return this.status.equals(StatusExecucaoServico.RECUSADO);
    }

    public boolean isFinalizado() {
        return this.status.equals(StatusExecucaoServico.FINALIZADO);
    }

    public boolean isConcluido() {
        return this.isFinalizado() || this.isRecusado();
    }

    public Long getId() {
        return id;
    }

    public StatusExecucaoServico getStatus() {
        return status;
    }

    public Long getOrdemServicoId() {
        return ordemServicoId;
    }

    public Long getServicoId() {
        return servicoId;
    }

    public Long getAtendenteAprovacaoId() {
        return atendenteAprovacaoId;
    }

    public Long getMecanicoResponsavelId() {
        return mecanicoResponsavelId;
    }

    public LocalDateTime getIniciadoEm() {
        return iniciadoEm;
    }

    public LocalDateTime getTerminadoEm() {
        return terminadoEm;
    }

    public Set<PecaAlocada> getPecas() {
        return pecas;
    }

    public BigDecimal getValorMaoDeObra() {
        return valorMaoDeObra;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    // TODO remove this setter
    public void setStatus(StatusExecucaoServico status) {
        this.status = status;
    }

    private void touch() {
        this.atualizadoEm = LocalDateTime.now();
    }

    private void validateServicoId(Long servico) {
        if (servico == null)
            throw new IllegalArgumentException("Serviço obrigatório");
    }

    private void validateOrdemServicoId(Long ordemServicoId) {
        if(ordemServicoId == null)
            throw new IllegalArgumentException("Ordem de serviço é obrigatória");
    }

    private void validateMaodeObra(BigDecimal valorMaoDeObra) {
        if (valorMaoDeObra == null || valorMaoDeObra.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor inválido");
        }
    }

    private StatusExecucaoServico statusAprovacao() {
        var aguardandoPecas = this.pecas.stream()
                .anyMatch(PecaAlocada::isAguardandoPeca);
        if(aguardandoPecas)
            return StatusExecucaoServico.AGUARDANDO_PECA;
        return StatusExecucaoServico.APROVADO;
    }
}