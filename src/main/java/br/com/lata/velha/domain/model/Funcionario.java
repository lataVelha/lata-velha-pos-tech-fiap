package br.com.lata.velha.domain.model;


import java.util.Objects;

import br.com.lata.velha.domain.valueObject.Senha;

public class Funcionario {

    private Long id;
    private String nome;
    private String username;
    private Senha senha;
    private Cargo cargo;

    public Funcionario() {
    }

    public Funcionario(Long id, String nome, String username, Senha senha, Cargo cargo) {
        this.id = id;
        setNome(nome);
        setUsername(username);
        this.senha = senha;
        this.cargo = cargo;
    }


    public boolean autenticar(String senhaPlana) {
        if (senhaPlana == null || senha == null) {
            return false;
        }
        return senha.corresponde(senhaPlana);
    }

    public boolean possuiRole(String nomeRole) {
        if (cargo == null) {
            return false;
        }
        return cargo.possuiRole(nomeRole);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do funcionário não pode ser vazio");
        }
        this.nome = nome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username não pode ser vazio");
        }
        this.username = username;
    }

    public Senha getSenha() {
        return senha;
    }

    public void setSenha(Senha senha) {
        this.senha = senha;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Funcionario that = (Funcionario) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Funcionario{id=" + id + ", nome='" + nome + "', username='" + username + "'}";
    }
}