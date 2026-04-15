package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;

import java.time.LocalDateTime;
import java.util.Objects;

public class PecaAlocada {

    private Long id;
    private Long pecaId;
    private Long servicoOsId;

    private Integer quantidadeSolicitada;
    private Integer quantidadeReservada;
    private Integer quantidadeEncomendada;

    private StatusPecaAlocada status;
    private LocalDateTime atualizado;

    public PecaAlocada() {}

    public PecaAlocada(Long pecaId, Long servicoOsId, Integer quantidadeSolicitada) {
        setPecaId(pecaId);
        setServicoOsId(servicoOsId);
        setQuantidadeSolicitada(quantidadeSolicitada);

        this.quantidadeReservada = 0;
        this.quantidadeEncomendada = 0;
        this.status = StatusPecaAlocada.ORCAMENTO;
        this.atualizado = LocalDateTime.now();
    }

    public PecaAlocada(Long id, Long pecaId, Long servicoOsId, Integer quantidadeSolicitada) {
        this.id = id;
        setPecaId(pecaId);
        setServicoOsId(servicoOsId);
        setQuantidadeSolicitada(quantidadeSolicitada);
        this.quantidadeReservada = 0;
        this.quantidadeEncomendada = 0;
        this.status = StatusPecaAlocada.ORCAMENTO;
        this.atualizado = LocalDateTime.now();
    }

    public PecaAlocada(Long id, Long pecaId, Long servicoOsId, Integer quantidadeSolicitada, Integer quantidadeReservada, Integer quantidadeEncomendada, StatusPecaAlocada status, LocalDateTime atualizado) {
        this.id = id;
        this.pecaId = pecaId;
        this.servicoOsId = servicoOsId;
        this.quantidadeSolicitada = quantidadeSolicitada;
        this.quantidadeReservada = quantidadeReservada;
        this.quantidadeEncomendada = quantidadeEncomendada;
        this.status = status;
        this.atualizado = atualizado;
    }

    /* ================= REGRAS ================= */

    public void reservar(Integer quantidadeDisponivel) {

        if (quantidadeDisponivel == null || quantidadeDisponivel <= 0) {
            encomendarTotal();
            return;
        }

        int restante = quantidadeSolicitada - quantidadeReservada;

        int reservar = Math.min(quantidadeDisponivel, restante);

        this.quantidadeReservada += reservar;

        int faltante = quantidadeSolicitada - quantidadeReservada;

        if (faltante > 0) {
            this.quantidadeEncomendada = faltante;
            this.status = StatusPecaAlocada.PARCIAL;
        } else {
            this.quantidadeEncomendada = 0;
            this.status = StatusPecaAlocada.RESERVADA;
        }

        touch();
    }

    public void encomendarTotal() {
        this.quantidadeReservada = 0;
        this.quantidadeEncomendada = quantidadeSolicitada;
        this.status = StatusPecaAlocada.ENCOMENDA;
        touch();
    }

    public void movimentarParaReservado(Integer quantidade) {

        if (quantidade == null || quantidade <= 0)
            throw new IllegalArgumentException("Quantidade inválida");

        this.quantidadeReservada += quantidade;
        this.quantidadeEncomendada -= quantidade;

        if (quantidadeEncomendada <= 0) {
            quantidadeEncomendada = 0;
            status = StatusPecaAlocada.RESERVADA;
        } else {
            status = StatusPecaAlocada.PARCIAL;
        }

        touch();
    }

    public void instalada(Integer quantidade) {

        if (quantidade == null || quantidade <= 0)
            throw new IllegalArgumentException("Quantidade inválida");

        if (quantidadeReservada < quantidade) {
            throw new IllegalStateException(
                    "Quantidade reservada insuficiente para instalar"
            );
        }

        this.quantidadeReservada -= quantidade;

        if (quantidadeReservada == 0 && quantidadeEncomendada == 0) {
            this.status = StatusPecaAlocada.INSTALADA;
        } else {
            throw new IllegalStateException("Peça não totalmente instalada");
        }

        touch();
    }

    /* ================= HELPERS ================= */

    public boolean totalmenteReservada() {
        return quantidadeReservada != null &&
                quantidadeReservada.equals(quantidadeSolicitada);
    }

    public boolean parcialmenteReservada() {
        return quantidadeReservada != null &&
                quantidadeReservada > 0 &&
                quantidadeReservada < quantidadeSolicitada;
    }

    public boolean totalmenteInstalada() {
        return quantidadeReservada == 0 &&
                quantidadeEncomendada == 0 &&
                status == StatusPecaAlocada.INSTALADA;
    }

    private void touch() {
        this.atualizado = LocalDateTime.now();
    }

    /* ================= GETTERS ================= */

    public Long getId() { return id; }
    public Long getPecaId() { return pecaId; }
    public Long getServicoOsId() { return servicoOsId; }
    public Integer getQuantidadeSolicitada() { return quantidadeSolicitada; }
    public Integer getQuantidadeReservada() { return quantidadeReservada; }
    public Integer getQuantidadeEncomendada() { return quantidadeEncomendada; }
    public StatusPecaAlocada getStatus() { return status; }
    public LocalDateTime getAtualizado() { return atualizado; }

    /* ================= SETTERS CONTROLADOS ================= */

    public void setId(Long id) {
        this.id = id;
    }

    public void setPecaId(Long pecaId) {
        if (pecaId == null)
            throw new IllegalArgumentException("Peça obrigatória");
        this.pecaId = pecaId;
    }

    public void setServicoOsId(Long servicoOsId) {
        if (servicoOsId == null)
            throw new IllegalArgumentException("Serviço obrigatório");
        this.servicoOsId = servicoOsId;
    }

    public void setQuantidadeSolicitada(Integer quantidadeSolicitada) {
        if (quantidadeSolicitada == null || quantidadeSolicitada <= 0)
            throw new IllegalArgumentException("Quantidade inválida");
        this.quantidadeSolicitada = quantidadeSolicitada;
    }

    public void setQuantidadeReservada(Integer quantidadeReservada) {
        this.quantidadeReservada = quantidadeReservada;
    }

    public void setQuantidadeEncomendada(Integer quantidadeEncomendada) {
        this.quantidadeEncomendada = quantidadeEncomendada;
    }

    public void setStatus(StatusPecaAlocada status) {
        this.status = status;
    }

    public void setAtualizado(LocalDateTime atualizado) {
        this.atualizado = atualizado;
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
}