package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;

import java.time.LocalDateTime;
import java.util.Objects;

public final class PecaAlocada {
    private final Long id;
    private final Long pecaId;
    private final Long execucaoServicoId;

    private Integer quantidadeSolicitada;
    private Integer quantidadeReservada;
    private Integer quantidadeEncomendada;

    private StatusPecaAlocada status;
    private LocalDateTime atualizado;

    public PecaAlocada(Long id, Long pecaId, Long execucaoServicoId, Integer quantidadeSolicitada, Integer quantidadeReservada, Integer quantidadeEncomendada, StatusPecaAlocada status, LocalDateTime atualizado) {
        validatePecaId(pecaId);
        validateExecucaoServicoId(execucaoServicoId);

        this.id = id;
        this.pecaId = pecaId;
        this.execucaoServicoId = execucaoServicoId;

        setQuantidadeSolicitada(quantidadeSolicitada);
        this.quantidadeReservada = quantidadeReservada;
        this.quantidadeEncomendada = quantidadeEncomendada;
        this.status = status;
        this.atualizado = atualizado;
    }

    public static PecaAlocada create(Long pecaId, Long execucaoServicoId, Integer quantidadeSolicitada) {
        var quantidadeReservadaInicial = 0;
        var quantidadeEncomendadaInicial = 0;
        var statusInicial = StatusPecaAlocada.ORCAMENTO;
        return new PecaAlocada(null, pecaId, execucaoServicoId, quantidadeSolicitada, quantidadeReservadaInicial, quantidadeEncomendadaInicial, statusInicial, LocalDateTime.now());
    }

    public void reservar(PecaEstoque estoque) {
        if (estoque != null && estoque.getQuantidadeDisponivel() > 0)
            reservarEstoque(estoque);
        encomendarFaltante();

        if (this.quantidadeEncomendada > 0) {
            this.status = StatusPecaAlocada.PARCIAL;
        } else {
            this.quantidadeEncomendada = 0;
            this.status = StatusPecaAlocada.RESERVADA;
        }

        touch();
    }

    public void instalada(Integer quantidade) {
        if (quantidade == null || quantidade <= 0)
            throw new IllegalArgumentException("Quantidade inválida");

        if (quantidadeReservada < quantidade)
            throw new IllegalStateException("Quantidade reservada insuficiente para instalar");

        this.quantidadeReservada -= quantidade;

        if (this.quantidadeReservada == 0 && this.quantidadeEncomendada == 0) {
            this.status = StatusPecaAlocada.INSTALADA;
        }

        touch();
    }

    public boolean isProcessada() {
        return quantidadeSolicitada >= quantidadeReservada;
    }
    /* ================= HELPERS ================= */

    public boolean totalmenteReservada() {
        return quantidadeReservada != null &&
                quantidadeReservada.equals(quantidadeSolicitada);
    }

    private void touch() {
        this.atualizado = LocalDateTime.now();
    }
    /* ================= GETTERS ================= */

    public Long getId() { return id; }
    public Long getPecaId() { return pecaId; }
    public Long getExecucaoServicoId() { return execucaoServicoId; }
    public Integer getQuantidadeSolicitada() { return quantidadeSolicitada; }
    public Integer getQuantidadeReservada() { return quantidadeReservada; }
    public Integer getQuantidadeEncomendada() { return quantidadeEncomendada; }
    public StatusPecaAlocada getStatus() { return status; }
    public LocalDateTime getAtualizado() { return atualizado; }

    /* ================= SETTERS CONTROLADOS ================= */
    public void setQuantidadeReservada(Integer quantidadeReservada) {
        this.quantidadeReservada = quantidadeReservada;
    }

    public void setQuantidadeEncomendada(Integer quantidadeEncomendada) {
        this.quantidadeEncomendada = quantidadeEncomendada;
    }

    public void setStatus(StatusPecaAlocada status) {
        this.status = status;
    }

    /* ================= OBJECT ================= */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PecaAlocada)) return false;
        PecaAlocada that = (PecaAlocada) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private void validateExecucaoServicoId(Long execucaoServicoId) {
        if (execucaoServicoId == null)
            throw new IllegalArgumentException("Serviço obrigatório");
    }

    private void validatePecaId(Long pecaId) {
        if (pecaId == null)
            throw new IllegalArgumentException("Peça obrigatória");
    }

    private void reservarEstoque(PecaEstoque estoque) {
        int quantidadeReservar = Math.min(estoque.getQuantidadeDisponivel(), getRestante());
        estoque.alocar(quantidadeReservar);
        this.quantidadeReservada += quantidadeReservar;
    }

    private void encomendarFaltante() {
        this.quantidadeEncomendada = getRestante();
    }

    private int getRestante() {
        return this.quantidadeSolicitada - this.quantidadeReservada;
    }

    private void setQuantidadeSolicitada(Integer quantidadeSolicitada) {
        if (quantidadeSolicitada == null || quantidadeSolicitada <= 0)
            throw new IllegalArgumentException("Quantidade inválida");
        this.quantidadeSolicitada = quantidadeSolicitada;
    }
}