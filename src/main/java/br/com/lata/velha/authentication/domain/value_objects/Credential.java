package br.com.lata.velha.authentication.domain.value_objects;

import br.com.lata.velha.authentication.domain.services.PasswordHasher;

import java.util.Objects;

public final class Credential {
    private final String credential;
    private final PasswordHasher hasher;

    private Credential(String value, PasswordHasher hasher) {
        this.credential = value;
        this.hasher = hasher;
    }

    public static Credential fromSenha(Senha senha, PasswordHasher hasher) {
        var hashedPassword = hasher.hashSenha(senha);
        return new Credential(hashedPassword, hasher);
    }

    public static Credential fromHash(String hash, PasswordHasher hasher) {
        return new Credential(hash, hasher);
    }

    public String getHash() {
        return credential;
    }

    public boolean match(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) return false;
        return hasher.match(this, rawPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Credential that)) return false;
        return Objects.equals(credential, that.credential);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(credential);
    }
}
