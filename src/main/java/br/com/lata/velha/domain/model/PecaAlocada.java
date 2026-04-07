package br.com.lata.velha.domain.model;

import java.util.Objects;

public class PecaAlocada {

    private Long id;
    private Long pecaId;
    private Long servicoOsId;
    private Integer quantidadeAlocada;

    public PecaAlocada() {
    }

    public PecaAlocada(Long id, Long pecaId, Long servicoOsId, Integer quantidadeAlocada) {
        this.id = id;
        setPecaId(pecaId);
        setServicoOsId(servicoOsId);
        setQuantidadeAlocada(quantidadeAlocada);
    }

    public Long getId() {
        return id;
    }

    public Long getPecaId() {
        return pecaId;
    }

    public void setPecaId(Long pecaId) {
        if (pecaId == null)
            throw new IllegalArgumentException("ID da peça não pode ser nulo");
        this.pecaId = pecaId;
    }

    public Long getServicoOsId() {
        return servicoOsId;
    }

    public void setServicoOsId(Long servicoOsId) {
        if (servicoOsId == null)
            throw new IllegalArgumentException("ID do serviço não pode ser nulo");
        this.servicoOsId = servicoOsId;
    }

    public Integer getQuantidadeAlocada() {
        return quantidadeAlocada;
    }

    public void setQuantidadeAlocada(Integer quantidadeAlocada) {
        if (quantidadeAlocada == null || quantidadeAlocada <= 0)
            throw new IllegalArgumentException("Quantidade alocada inválida");
        this.quantidadeAlocada = quantidadeAlocada;
    }

    @Override
    public String toString() {
        return "PecaAlocada{id=" + id +
                ", pecaId=" + pecaId +
                ", servicoOsId=" + servicoOsId +
                ", quantidade=" + quantidadeAlocada + '}';
    }

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
