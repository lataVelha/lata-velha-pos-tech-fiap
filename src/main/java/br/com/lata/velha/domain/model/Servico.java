package br.com.lata.velha.domain.model;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public class Servico {

    private Long id;
    private String nome;
    private String descricao;

    public Servico() {
    }

    public Servico(Long id, String nome, String descricao) {
        this.id = id;
        setNome(nome);
        setDescricao(descricao);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do serviço não pode ser vazio");
        }
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição do serviço não pode ser vazia");
        }
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "Servico{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Servico)) return false;
        Servico servico = (Servico) o;
        return Objects.equals(id, servico.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setId(Long servicoId) {
    }
}