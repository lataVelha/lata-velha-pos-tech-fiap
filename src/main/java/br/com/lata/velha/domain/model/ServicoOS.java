package br.com.lata.velha.domain.model;

import br.com.lata.velha.domain.enuns.StatusServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServicoOS {

    private Long id;
    private StatusServico status;
    private Servico servico;
    private Long atendenteId;

    private LocalDateTime iniciadoEm;
    private LocalDateTime terminadoEm;
    private Long mecanicoResponsavelId;

    private List<PecaAlocada> pecas = new ArrayList<>();

    private BigDecimal valorMaoDeObra;
    private LocalDateTime atualizadoEm;

    public ServicoOS() {
    }

    public ServicoOS(Long id, Servico servico, BigDecimal valorMaoDeObra) {
        this.id = id;
        setServico(servico);
        setValorMaoDeObra(valorMaoDeObra);
        this.status = StatusServico.PENDENTE;
        this.iniciadoEm = LocalDateTime.now();
    }

    /* ===================== REGRAS ===================== */

    public void aprovado(Long atendente) {
        this.status = StatusServico.APROVADO;
        this.atendenteId = atendente;
        this.atualizadoEm  = LocalDateTime.now();
    }

    public void recusado(Long atendente) {
        this.status = StatusServico.RECUSADO;
        this.atendenteId = atendente;
        this.atualizadoEm = LocalDateTime.now();
        this.terminadoEm = LocalDateTime.now();
    }

    public void finalizado(Long mecanicoId) {
        this.status = StatusServico.FINALIZADO;
        this.mecanicoResponsavelId = mecanicoId;
        this.terminadoEm = LocalDateTime.now();
    }

    public void adicionarPeca(PecaAlocada peca) {
        if (peca == null)
            throw new IllegalArgumentException("Peça inválida");

        pecas.add(peca);
        this.atualizadoEm = LocalDateTime.now();
    }

    public double calcularTotal() {
        double totalPecas = pecas.stream()
                .mapToDouble(p -> 0.0) // Requires fetching peca details from DB, not directly in domain if decoupled. Simplified for compilation.
                .sum();

        return totalPecas + valorMaoDeObra.doubleValue();
    }

    /* ===================== GETTERS ===================== */

    public Long getId() {
        return id;
    }

    public StatusServico getStatus() {
        return status;
    }

    public Servico getServico() {
        return servico;
    }

    public LocalDateTime getIniciadoEm() {
        return iniciadoEm;
    }

    public LocalDateTime getTerminadoEm() {
        return terminadoEm;
    }

    public Long getMecanicoResponsavelId() {
        return mecanicoResponsavelId;
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

    /* ===================== SETTERS CONTROLADOS ===================== */

    public void setStatus(StatusServico status) {
        this.status = status;
    }

    public void setServico(Servico servico) {
        if (servico == null)
            throw new IllegalArgumentException("Serviço obrigatório");
        this.servico = servico;
    }

    public void setValorMaoDeObra(BigDecimal valorMaoDeObra) {
        if (valorMaoDeObra == null || valorMaoDeObra.doubleValue() < 0)
            throw new IllegalArgumentException("Valor inválido");
        this.valorMaoDeObra = valorMaoDeObra;
    }

    public void setMecanicoResponsavelId(Long mecanicoResponsavelId) {
        this.mecanicoResponsavelId = mecanicoResponsavelId;
    }

    public void setIniciadoEm(LocalDateTime iniciadoEm) {
        this.iniciadoEm = iniciadoEm;
    }

    public void setTerminadoEm(LocalDateTime terminadoEm) {
        this.terminadoEm = terminadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}