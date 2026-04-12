package br.com.lata.velha.domain.model;

import br.com.lata.velha.authentication.domain.valueObjects.Credential;
import br.com.lata.velha.domain.exception.InvalidLoginException;

import java.util.Objects;

public class Funcionario {

    private Long id;
    private String nome;
    private String username;
    private Credential credential;
    private Cargo cargo;
    private boolean ativo = true;

    public Funcionario() {
    }

    public Funcionario(Long id, String nome, String username, Credential credential, Cargo cargo) {
        this.id = id;
        setNome(nome);
        setUsername(username);
        this.credential = credential;
        this.cargo = cargo;
        this.ativo = true;
    }

    public Funcionario(Long id, String nome, String username, Credential credential, Cargo cargo, boolean ativo) {
        this.id = id;
        setNome(nome);
        setUsername(username);
        this.credential = credential;
        this.cargo = cargo;
        this.ativo = ativo;
    }

    // --- business rules ---

    public void authenticateOrFail(String rawPassword) {
        if (credential == null || !credential.match(rawPassword)) {
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

    public Credential getCredential() { return credential; }
    public void setCredential(Credential credential) { this.credential = credential; }

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