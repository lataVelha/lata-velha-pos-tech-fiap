package br.com.lata.velha.domain.valueObject;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Value Object que encapsula a senha do funcionário.
 *
 * A verificação de hash é delegada a uma função injetada na criação,
 * mantendo o domínio desacoplado de qualquer framework (BCrypt, Spring Security, etc).
 */
public class Senha {

    private final String hash;
    private final BiFunction<String, String, Boolean> verificador;

    private Senha(String hash, BiFunction<String, String, Boolean> verificador) {
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("Hash da senha não pode ser vazio");
        }
        Objects.requireNonNull(verificador, "Verificador de senha é obrigatório");
        this.hash = hash;
        this.verificador = verificador;
    }

    public static Senha fromHash(String hash, BiFunction<String, String, Boolean> verificador) {
        return new Senha(hash, verificador);
    }

    // --- business method ---

    public boolean matches(String senhaPlana) {
        if (senhaPlana == null || senhaPlana.isBlank()) {
            return false;
        }
        return verificador.apply(senhaPlana, this.hash);
    }

    // --- getter ---

    public String getHash() { return hash; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(hash, ((Senha) o).hash);
    }

    @Override
    public int hashCode() { return Objects.hash(hash); }

    @Override
    public String toString() { return "Senha{***}"; }
}