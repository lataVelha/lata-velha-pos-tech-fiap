package br.com.lata.velha.authentication.domain.entities;

import br.com.lata.velha.authentication.domain.valueObjects.Credential;
import br.com.lata.velha.authentication.domain.valueObjects.Senha;
import br.com.lata.velha.shared.domain.valueObjects.UserId;

import java.time.LocalDateTime;
import java.util.Set;

public final class User {
    private final UserId userId;
    private final String username;
    private final String email;
    private final Credential credential;
    private final Set<Role> roles;
    private boolean ativo;
    private final LocalDateTime criacaoDate;
    private LocalDateTime ultimoLoginDate;

    public User(UserId userId, String username, String email, Credential credential, Set<Role> roles, boolean isActive, LocalDateTime criacaoDate, LocalDateTime ultimoLoginDate) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.credential = credential;
        this.roles = roles;
        this.ativo = isActive;
        this.criacaoDate = criacaoDate;
        this.ultimoLoginDate = ultimoLoginDate;
    }

    public boolean login(Senha senha) {
        if (!credential.match(senha.getValor())) return false;
        this.ultimoLoginDate = LocalDateTime.now();
        return true;
    }

    public void addRole(Role role) {
        if(role == null)
            throw new IllegalArgumentException("Tentando inserir uma role null ao usuário: " + userId.toString());
        this.roles.add(role);
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    //region GETTERS
    public UserId getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Credential getCredential() {
        return credential;
    }

    public LocalDateTime getCriacaoDate() {
        return criacaoDate;
    }

    public LocalDateTime getUltimoLoginDate() {
        return ultimoLoginDate;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public boolean isAtivo() {
        return ativo;
    }
    //endregion
}

