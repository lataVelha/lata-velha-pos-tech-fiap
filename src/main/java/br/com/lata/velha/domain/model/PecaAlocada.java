package br.com.lata.velha.domain.model;

import java.util.Objects;

public class PecaAlocada {

    private Long id;
    private Peca peca;
    private Integer quantidadeAlocada;

    public PecaAlocada() {
    }

    public PecaAlocada(Long id, Peca peca, Integer quantidadeAlocada) {
        this.id = id;
        setPeca(peca);
        setQuantidadeAlocada(quantidadeAlocada);
    }

    public Long getId() {
        return id;
    }

    public Peca getPeca() {
        return peca;
    }

    public void setPeca(Peca peca) {
        if (peca == null)
            throw new IllegalArgumentException("Peça não pode ser nula");
        this.peca = peca;
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
                ", peca=" + peca +
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