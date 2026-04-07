package br.com.lata.velha.domain.model;

public class PecaEstoque {

    private Long pecaId;
    private Integer quantidadeArmazenada;

    public PecaEstoque() {
    }

    public PecaEstoque(Long pecaId, Integer quantidadeArmazenada) {
        this.pecaId = pecaId;
        setQuantidadeArmazenada(quantidadeArmazenada);
    }

    public Long getPecaId() {
        return pecaId;
    }

    public Integer getQuantidadeArmazenada() {
        return quantidadeArmazenada;
    }

    public void adicionar(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade de entrada inválida");
        }

        this.quantidadeArmazenada += quantidade;
    }

    public void remover(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade de saída inválida");
        }

        if (this.quantidadeArmazenada < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente para a peça informada");
        }

        this.quantidadeArmazenada -= quantidade;
    }

    public void ajustar(Integer quantidadeArmazenada) {
        setQuantidadeArmazenada(quantidadeArmazenada);
    }

    public void setQuantidadeArmazenada(Integer quantidadeArmazenada) {
        if (quantidadeArmazenada == null || quantidadeArmazenada < 0)
            throw new IllegalArgumentException("Quantidade armazenada inválida");
        this.quantidadeArmazenada = quantidadeArmazenada;
    }

    @Override
    public String toString() {
        return "PecaEstoque{pecaId=" + pecaId +
                ", quantidade=" + quantidadeArmazenada + '}';
    }
}