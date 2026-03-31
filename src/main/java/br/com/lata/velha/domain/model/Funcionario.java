package br.com.lata.velha.domain.model;

import br.com.lata.velha.domain.exception.InvalidLoginException;
import br.com.lata.velha.domain.valueObject.Senha;

import java.util.Objects;

public class Funcionario {

    private Long id;
    private String nome;
    private String username;
    private Senha senha;
    private Cargo cargo;
    private boolean ativo = true;

    public Funcionario() {
    }

    public Funcionario(Long id, String nome, String username, Senha senha, Cargo cargo) {
        this.id = id;
        setNome(nome);
        setUsername(username);
        this.senha = senha;
        this.cargo = cargo;
        this.ativo = true;
    }

    public Funcionario(Long id, String nome, String username, Senha senha, Cargo cargo, boolean ativo) {
        this.id = id;
        setNome(nome);
        setUsername(username);
        this.senha = senha;
        this.cargo = cargo;
        this.ativo = ativo;
    }

    // --- business rules ---

    public void authenticateOrFail(String rawPassword) {
        if (rawPassword == null || senha == null || !senha.matches(rawPassword)) {
            throw new InvalidLoginException();
        }
    }

    public void desativar() {
        if (!this.ativo) {
            throw new IllegalArgumentException("Funcionário já está desativado");
        }
        this.ativo = false;
    }

    public boolean hasRole(String roleName) {
        if (cargo == null) {
            return false;
        }
        return cargo.hasRole(roleName);
    }

    // --- getters and setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome do funcionário não pode ser vazio");
        this.nome = nome;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username não pode ser vazio");
        this.username = username;
    }

    public Senha getSenha() { return senha; }
    public void setSenha(Senha senha) { this.senha = senha; }

    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Funcionario that = (Funcionario) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Funcionario{id=" + id + ", nome='" + nome + "', username='" + username + "'}";
    }
}