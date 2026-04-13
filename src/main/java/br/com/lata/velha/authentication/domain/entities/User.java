package br.com.lata.velha.authentication.domain.entities;

import br.com.lata.velha.authentication.domain.valueObjects.Credential;
import br.com.lata.velha.authentication.domain.exceptions.InvalidLoginException;
import br.com.lata.velha.shared.domain.valueObjects.Email;
import br.com.lata.velha.shared.domain.valueObjects.UserId;

import java.time.LocalDateTime;
import java.util.Set;

public final class User {
    private final UserId id;
    private final String username;
    private final Email email;
    private final Credential credential;
    private final Set<Role> roles;
    private boolean ativo;
    private final LocalDateTime criacaoDate;
    private LocalDateTime ultimoLoginDate;

    public User(UserId id, String username, Email email, Credential credential, Set<Role> roles, boolean isActive, LocalDateTime criacaoDate, LocalDateTime ultimoLoginDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.credential = credential;
        this.roles = roles;
        this.ativo = isActive;
        this.criacaoDate = criacaoDate;
        this.ultimoLoginDate = ultimoLoginDate;
    }

    public static User create(Email email, Credential credential, Set<Role> roles) {
        var id = UserId.random();
        var creationDate = LocalDateTime.now();
        return new User(id, email.getValor(), email, credential, roles, true, creationDate, null);
    }

    public boolean login(String senha) {
        if(!isAtivo()) throw new InvalidLoginException("Usuário inativo");
        if (!credential.match(senha)) throw new InvalidLoginException();
        this.ultimoLoginDate = LocalDateTime.now();
        return true;
    }

    public void addRole(Role role) {
        if(role == null)
            throw new IllegalArgumentException("Tentando inserir uma role null ao usuário: " + id.toString());
        this.roles.add(role);
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }

    //region GETTERS
    public UserId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Email getEmail() {
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

