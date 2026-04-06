package br.com.lata.velha.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Peca {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal valor;
    private boolean ativo = true;

    public Peca() {
    }

    public Peca(Long id, String nome, String descricao, BigDecimal valor) {
        this(id, nome, descricao, valor, true);
    }

    public Peca(Long id, String nome, String descricao, BigDecimal valor, boolean ativo) {
        this.id = id;
        setNome(nome);
        setDescricao(descricao);
        setValor(valor);
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome da peça não pode ser vazio");
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank())
            throw new IllegalArgumentException("Descrição da peça não pode ser vazia");
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Valor da peça deve ser maior que zero");
        this.valor = valor;
    }

    public void desativar() {
        if (!this.ativo) {
            throw new IllegalArgumentException("Peça já está desativada");
        }
        this.ativo = false;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Peca{id=" + id + ", nome='" + nome + "', valor=" + valor + ", ativo=" + ativo + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Peca)) return false;
        Peca peca = (Peca) o;
        return Objects.equals(id, peca.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}