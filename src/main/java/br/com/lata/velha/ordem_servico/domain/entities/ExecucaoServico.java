package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExecucaoServico {

    private Long id;
    private StatusExecucaoServico status;
    private Servico servico;

    private Long atendenteId;
    private Long mecanicoResponsavelId;

    private LocalDateTime iniciadoEm;
    private LocalDateTime terminadoEm;
    private LocalDateTime atualizadoEm;

    private BigDecimal valorMaoDeObra;

    private List<PecaAlocada> pecas = new ArrayList<>();

    public ExecucaoServico() {
    }

    public ExecucaoServico(Servico servico, BigDecimal valorMaoDeObra) {
        setServico(servico);
        setValorMaoDeObra(valorMaoDeObra);
        this.status = StatusExecucaoServico.PENDENTE;
        this.atualizadoEm = LocalDateTime.now();
    }

    /* =========================
       REGRAS DE FLUXO
    ========================= */

    public void aprovar(Long atendenteId) {

        if (status != StatusExecucaoServico.PENDENTE) {
            throw new IllegalStateException("Só serviços pendentes podem ser aprovados");
        }

        this.status = StatusExecucaoServico.APROVADO;
        this.atendenteId = atendenteId;
        this.iniciadoEm = LocalDateTime.now();
        touch();
    }

    public void recusar(Long atendenteId) {

        if (status == StatusExecucaoServico.FINALIZADO) {
            throw new IllegalStateException("Serviço já finalizado");
        }

        this.status = StatusExecucaoServico.RECUSADO;
        this.atendenteId = atendenteId;
        this.terminadoEm = LocalDateTime.now();
        touch();
    }

    public void processarPeca(PecaAlocada peca, int quantidade, Long mecanicoId) {

        if (status != StatusExecucaoServico.APROVADO &&
                status != StatusExecucaoServico.EM_EXECUCAO) {
            throw new IllegalStateException(
                    "Serviço deve estar aprovado ou em execução para processar peças"
            );
        }

        if (peca == null) {
            throw new IllegalArgumentException("Peça inválida");
        }

        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }

        this.mecanicoResponsavelId = mecanicoId;

        this.status = StatusExecucaoServico.EM_EXECUCAO;

        touch();
    }

    public boolean finalizar(Long mecanicoId) {

        if (status != StatusExecucaoServico.EM_EXECUCAO) {
                return false;
            }

        if (pecas.stream().anyMatch(p -> !p.isProcessada())) {
            throw new IllegalStateException("Existem peças não processadas");
        }

        this.status = StatusExecucaoServico.FINALIZADO;
        this.mecanicoResponsavelId = mecanicoId;
        this.terminadoEm = LocalDateTime.now();

        touch();

        return true;
    }

    /* =========================
       PEÇAS
    ========================= */

    public void adicionarPeca(PecaAlocada peca) {

        if (status == StatusExecucaoServico.FINALIZADO) {
            throw new IllegalStateException("Serviço já finalizado");
        }

        if (peca == null) {
            throw new IllegalArgumentException("Peça inválida");
        }

        boolean existe = pecas.stream()
                .anyMatch(p -> p.getPecaId().equals(peca.getPecaId()));

        if (existe) {
            throw new IllegalStateException("Peça já adicionada ao serviço");
        }

        pecas.add(peca);
        touch();
    }
    public void atualizarPeca(PecaAlocada pecaAtualizada) {

        if (status == StatusExecucaoServico.FINALIZADO) {
            throw new IllegalStateException("Serviço já finalizado");
        }

        if (pecaAtualizada == null) {
            throw new IllegalArgumentException("Peça inválida");
        }

        PecaAlocada pecaExistente = pecas.stream()
                .filter(p -> p.getPecaId().equals(pecaAtualizada.getPecaId()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("Peça não encontrada no serviço")
                );

        pecaExistente.setQuantidadeReservada(pecaAtualizada.getQuantidadeReservada());
        pecaExistente.setQuantidadeEncomendada(pecaAtualizada.getQuantidadeEncomendada());
        pecaExistente.setStatus(pecaAtualizada.getStatus());

        this.touch();
    }

    /* =========================
       REGRA DE NEGÓCIO
    ========================= */

    public BigDecimal calcularTotal() {

        BigDecimal totalPecas = BigDecimal.ZERO; // simplificado (ajustável)

        return totalPecas.add(valorMaoDeObra != null ? valorMaoDeObra : BigDecimal.ZERO);
    }

    /* =========================
       HELPERS
    ========================= */

    private void touch() {
        this.atualizadoEm = LocalDateTime.now();
    }

    public boolean isAprovado() {
        return StatusExecucaoServico.APROVADO.equals(status);
    }

    public boolean isEmExecucao() {
        return StatusExecucaoServico.EM_EXECUCAO.equals(status);
    }

    public boolean isFinalizado() {
        return StatusExecucaoServico.FINALIZADO.equals(status);
    }

    /* =========================
       GETTERS
    ========================= */

    public Long getId() {
        return id;
    }

    public StatusExecucaoServico getStatus() {
        return status;
    }

    public Servico getServico() {
        return servico;
    }

    public Long getAtendenteId() {
        return atendenteId;
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

    public List<PecaAlocada> getPecas() {
        return pecas;
    }

    public BigDecimal getValorMaoDeObra() {
        return valorMaoDeObra;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    /* =========================
       SETTERS CONTROLADOS
    ========================= */

    public void setId(Long id) {
        this.id = id;
    }

    public void setServico(Servico servico) {
        if (servico == null) {
            throw new IllegalArgumentException("Serviço obrigatório");
        }
        this.servico = servico;
    }

    public void setValorMaoDeObra(BigDecimal valorMaoDeObra) {
        if (valorMaoDeObra == null || valorMaoDeObra.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor inválido");
        }
        this.valorMaoDeObra = valorMaoDeObra;
    }
    public void setStatus(StatusExecucaoServico status) {
        this.status = status;
    }

    public void setMecanicoResponsavelId(Long id) {
        this.mecanicoResponsavelId = id;
    }

    public void setIniciadoEm(LocalDateTime value) {
        this.iniciadoEm = value;
    }

    public void setTerminadoEm(LocalDateTime value) {
        this.terminadoEm = value;
    }

    public void setAtualizadoEm(LocalDateTime value) {
        this.atualizadoEm = value;
    }

    public boolean isRecusado() {
        return StatusExecucaoServico.RECUSADO.equals(this.status);
    }
}